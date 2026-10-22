package com.wingedsheep.engine.core

import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import kotlinx.serialization.Serializable

/**
 * The choices an action needs that its ID alone can't carry.
 *
 * An action ID resolves to the [com.wingedsheep.engine.legalactions.LegalAction] the enumerator
 * produced, and for several action types that `GameAction` is a *template*, not a complete move:
 * `DeclareAttackers` is enumerated with an empty attacker map (the enumerator advertises the
 * candidates in `validAttackers` / `validAttackTargets` and leaves the choice to the player), and
 * the same is true of `DeclareBlockers`, of a spell's targets, and of X. Submitting such an action
 * with nothing attached is a legal move — it just means "attack with nobody", "block with nobody",
 * "no targets" — so a host that forgets to complete the template fails silently.
 *
 * These params are how any host that consumes legal-action templates (the Gym, an AI controller)
 * completes one. The completed action is then validated by the engine like any other: an illegal
 * attacker assignment is rejected rather than quietly dropped.
 *
 * Not expressible here, deliberately — each has its own channel:
 * - Complex decisions (target-selection pauses, damage assignment, ordering, …) → a
 *   [SubmitDecision] carrying a typed [DecisionResponse].
 * - Attacking bands (CR 702.22), alternative cost payments, convoke/delve/improvise selections.
 *   A step carrying params for an action that can't use them is rejected with a message naming
 *   the action, never ignored.
 *
 * @property attackers attacker entity id → the player, planeswalker or battle it attacks.
 * @property blockers blocker entity id → the attackers it blocks, in order.
 * @property targets Targets for a cast/activation, in target-requirement order. Ids are resolved
 *   against the current state: a player id becomes a player target, an object on the stack a spell
 *   target, a battlefield permanent a permanent target, and a card in any other zone a card target.
 * @property xValue The value chosen for X.
 * @property targetGroups Targets split by target requirement, for spells and abilities with more than
 *   one ("target creature you control … target creature an opponent controls"). Sets the action's
 *   `targetGroupCounts` so the engine doesn't have to infer the boundaries. Exclusive with [targets].
 * @property sacrifice Permanents sacrificed to pay an additional cost.
 * @property discard Cards discarded to pay an additional cost.
 * @property exile Cards exiled to pay an additional cost, craft materials included.
 * @property tap Permanents tapped to pay a "tap N untapped …" cost.
 * @property bounce Permanents returned to hand to pay an additional cost.
 * @property variablePermanents Permanents chosen for a variable-count cost whose count is the payer's
 *   choice — "sacrifice any number of Spirits", or Teamwork's "tap creatures with total power N".
 * @property crew Creatures tapped to crew a vehicle.
 * @property saddle Creatures tapped to saddle a mount.
 */
@Serializable
data class ActionParams(
    val attackers: Map<EntityId, EntityId> = emptyMap(),
    val blockers: Map<EntityId, List<EntityId>> = emptyMap(),
    val targets: List<EntityId> = emptyList(),
    val xValue: Int? = null,
    val targetGroups: List<List<EntityId>> = emptyList(),
    val sacrifice: List<EntityId> = emptyList(),
    val discard: List<EntityId> = emptyList(),
    val exile: List<EntityId> = emptyList(),
    val tap: List<EntityId> = emptyList(),
    val bounce: List<EntityId> = emptyList(),
    val variablePermanents: List<EntityId> = emptyList(),
    val crew: List<EntityId> = emptyList(),
    val saddle: List<EntityId> = emptyList()
) {
    val isEmpty: Boolean
        get() = populatedFields.isEmpty()

    /** The names of the fields actually carrying a choice — the vocabulary of the error messages. */
    internal val populatedFields: List<String>
        get() = buildList {
            if (attackers.isNotEmpty()) add("attackers")
            if (blockers.isNotEmpty()) add("blockers")
            if (targets.isNotEmpty()) add("targets")
            if (xValue != null) add("xValue")
            if (targetGroups.isNotEmpty()) add("targetGroups")
            if (sacrifice.isNotEmpty()) add("sacrifice")
            if (discard.isNotEmpty()) add("discard")
            if (exile.isNotEmpty()) add("exile")
            if (tap.isNotEmpty()) add("tap")
            if (bounce.isNotEmpty()) add("bounce")
            if (variablePermanents.isNotEmpty()) add("variablePermanents")
            if (crew.isNotEmpty()) add("crew")
            if (saddle.isNotEmpty()) add("saddle")
        }

    companion object {
        val EMPTY = ActionParams()
    }
}

/**
 * Folds [ActionParams] into the template `GameAction` an action ID resolved to.
 *
 * Pure — it builds the action the engine will then validate; it does not check legality itself.
 * Anything it cannot express is an [IllegalArgumentException] rather than a silently
 * dropped choice, which is the failure mode this whole type exists to remove.
 */
object ActionParameterizer {

    fun apply(action: GameAction, params: ActionParams, state: GameState): GameAction {
        if (params.isEmpty) return action

        return when (action) {
            is DeclareAttackers -> {
                params.allowOnly(action, "attackers")
                action.copy(attackers = params.attackers)
            }

            is DeclareBlockers -> {
                params.allowOnly(action, "blockers")
                action.copy(blockers = params.blockers)
            }

            is CastSpell -> {
                params.allowOnly(action, "targets", "targetGroups", "xValue", *COST_FIELDS)
                val (targets, groupCounts) = params.chosenTargets(state)
                action.copy(
                    targets = targets ?: action.targets,
                    targetGroupCounts = groupCounts ?: action.targetGroupCounts,
                    xValue = params.xValue ?: action.xValue,
                    additionalCostPayment = params.costPayment(action.additionalCostPayment)
                )
            }

            is ActivateAbility -> {
                params.allowOnly(action, "targets", "targetGroups", "xValue", *COST_FIELDS)
                val (targets, groupCounts) = params.chosenTargets(state)
                action.copy(
                    targets = targets ?: action.targets,
                    targetGroupCounts = groupCounts ?: action.targetGroupCounts,
                    xValue = params.xValue ?: action.xValue,
                    costPayment = params.costPayment(action.costPayment)
                )
            }

            is CrewVehicle -> {
                params.allowOnly(action, "crew")
                action.copy(crewCreatures = params.crew)
            }

            is SaddleMount -> {
                params.allowOnly(action, "saddle")
                action.copy(saddleCreatures = params.saddle)
            }

            else -> throw IllegalArgumentException(
                "Action ${action::class.simpleName} takes no step params; got $params"
            )
        }
    }

    /**
     * Turn a bare entity id into the [ChosenTarget] variant its current zone implies. The caller
     * sends ids because that is all the observation exposes; which variant an id means is a fact
     * about the game state, not about the request.
     *
     * Public so the zone dispatch can be asserted directly — it is the only part of this object that
     * reads live state, and driving a game to each of the four zones just to reach it would test the
     * driver, not the dispatch.
     */
    fun resolveTarget(id: EntityId, state: GameState): ChosenTarget = when {
        id in state.turnOrder -> ChosenTarget.Player(id)
        id in state.stack -> ChosenTarget.Spell(id)
        id in state.getBattlefield() -> ChosenTarget.Permanent(id)
        else -> {
            val key = state.zones.entries.firstOrNull { (_, ids) -> id in ids }?.key
                ?: throw IllegalArgumentException("Target $id is in no zone of the current state")
            ChosenTarget.Card(cardId = id, ownerId = key.ownerId, zone = key.zoneType)
        }
    }

    /** The [ActionParams] fields that fill an [AdditionalCostPayment]. */
    private val COST_FIELDS = arrayOf("sacrifice", "discard", "exile", "tap", "bounce", "variablePermanents")

    /** Resolved targets and, for [ActionParams.targetGroups], the per-requirement counts; nulls keep the template's. */
    private fun ActionParams.chosenTargets(state: GameState): Pair<List<ChosenTarget>?, List<Int>?> {
        require(targets.isEmpty() || targetGroups.isEmpty()) {
            "Send either 'targets' or 'targetGroups', not both"
        }
        return when {
            targetGroups.isNotEmpty() ->
                targetGroups.flatten().map { resolveTarget(it, state) } to targetGroups.map { it.size }
            targets.isNotEmpty() -> targets.map { resolveTarget(it, state) } to null
            else -> null to null
        }
    }

    /** Fold the cost-choice fields into [existing], leaving any field the caller didn't send as it was. */
    private fun ActionParams.costPayment(existing: AdditionalCostPayment?): AdditionalCostPayment? {
        if (sacrifice.isEmpty() && discard.isEmpty() && exile.isEmpty() && tap.isEmpty() && bounce.isEmpty() &&
            variablePermanents.isEmpty()
        ) {
            return existing
        }
        val base = existing ?: AdditionalCostPayment()
        return base.copy(
            sacrificedPermanents = sacrifice.ifEmpty { base.sacrificedPermanents },
            discardedCards = discard.ifEmpty { base.discardedCards },
            exiledCards = exile.ifEmpty { base.exiledCards },
            tappedPermanents = tap.ifEmpty { base.tappedPermanents },
            bouncedPermanents = bounce.ifEmpty { base.bouncedPermanents },
            variableCostPermanents = variablePermanents.ifEmpty { null } ?: base.variableCostPermanents
        )
    }

    /**
     * Reject any populated field [action] can't use. Stated as a whitelist rather than as one
     * rejection per inapplicable field, so a new [ActionParams] field is inapplicable everywhere by
     * default instead of being silently dropped by every branch that forgot to name it.
     */
    private fun ActionParams.allowOnly(action: GameAction, vararg allowed: String) {
        val unusable = populatedFields - allowed.toSet()
        require(unusable.isEmpty()) {
            "Step param(s) ${unusable.joinToString(", ") { "'$it'" }} " +
                "are not applicable to ${action::class.simpleName}"
        }
    }
}
