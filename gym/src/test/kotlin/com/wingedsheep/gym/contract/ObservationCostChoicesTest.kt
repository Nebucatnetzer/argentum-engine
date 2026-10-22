package com.wingedsheep.gym.contract

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.CrewVehicle
import com.wingedsheep.engine.core.GameConfig
import com.wingedsheep.engine.core.PlayerConfig
import com.wingedsheep.engine.core.SaddleMount
import com.wingedsheep.engine.legalactions.AdditionalCostData
import com.wingedsheep.engine.legalactions.LegalAction
import com.wingedsheep.engine.legalactions.TapForPowerCreatureData
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.gym.GameEnvironment
import com.wingedsheep.mtg.sets.definitions.por.PortalSet
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly

/**
 * [LegalActionView.costChoices]: each engine cost type must be advertised under the `ActionParams`
 * field whose payment slot that cost is read from, with the bounds the cost actually allows.
 */
class ObservationCostChoicesTest : FunSpec({

    fun newEnv(): GameEnvironment {
        val registry = CardRegistry().apply {
            register(PortalSet.cards)
            register(PortalSet.basicLands)
        }
        return GameEnvironment.create(registry).apply {
            reset(
                GameConfig(
                    players = listOf(
                        PlayerConfig("Alice", Deck.of("Mountain" to 20)),
                        PlayerConfig("Bob", Deck.of("Mountain" to 20))
                    ),
                    skipMulligans = true,
                    startingPlayerIndex = 0,
                    seed = 20261022L
                )
            )
        }
    }

    val a = EntityId("candidate-a")
    val b = EntityId("candidate-b")

    fun costChoicesOf(la: LegalAction): List<CostChoiceView> {
        val env = newEnv()
        val result = ObservationBuilder(env.cardRegistry).build(env.state, env.playerIds[0], listOf(la), false)
        return (result.observation as TrainingObservation).legalActions.single().costChoices
    }

    fun spellWithCost(alice: EntityId, info: AdditionalCostData) = LegalAction(
        action = CastSpell(alice, EntityId("spell")),
        actionType = "CastSpell",
        description = "Cast spell",
        additionalCostInfo = info
    )

    test("a fixed sacrifice pays through sacrifice, exactly the count") {
        val alice = newEnv().playerIds[0]
        costChoicesOf(
            spellWithCost(alice, AdditionalCostData("Sacrifice a creature", "SacrificePermanent", validSacrificeTargets = listOf(a, b)))
        ) shouldContainExactly listOf(CostChoiceView("sacrifice", "Sacrifice a creature", listOf(a, b), 1, 1))
    }

    test("sacrifice any number pays through variablePermanents, up to every candidate") {
        val alice = newEnv().playerIds[0]
        costChoicesOf(
            spellWithCost(
                alice,
                AdditionalCostData("Sacrifice any number of Spirits", "SacrificeVariable", validSacrificeTargets = listOf(a, b), sacrificeCount = 0)
            )
        ) shouldContainExactly listOf(CostChoiceView("variablePermanents", "Sacrifice any number of Spirits", listOf(a, b), 0, 2))
    }

    test("a cost-reduction sacrifice may pick none") {
        val alice = newEnv().playerIds[0]
        costChoicesOf(
            spellWithCost(alice, AdditionalCostData("Sacrifice any number", "SacrificeForCostReduction", validSacrificeTargets = listOf(a, b)))
        ) shouldContainExactly listOf(CostChoiceView("sacrifice", "Sacrifice any number", listOf(a, b), 0, 2))
    }

    test("teamwork pays through variablePermanents, gated on total power") {
        val alice = newEnv().playerIds[0]
        val info = AdditionalCostData(
            "Teamwork 3", "TapForTotalPower",
            tapForPowerCreatures = listOf(TapForPowerCreatureData(a, "A", 2), TapForPowerCreatureData(b, "B", 1)),
            tapForPowerRequired = 3
        )
        costChoicesOf(spellWithCost(alice, info)) shouldContainExactly listOf(
            CostChoiceView("variablePermanents", "Teamwork 3", listOf(a, b), 0, 2, contribution = mapOf(a to 2, b to 1), minTotal = 3)
        )
    }

    test("a cost the params can't carry is not advertised") {
        val alice = newEnv().playerIds[0]
        costChoicesOf(
            spellWithCost(alice, AdditionalCostData("Casualty 2", "Casualty", validSacrificeTargets = listOf(a)))
        ).shouldBeEmpty()
    }

    test("crew and saddle each name their own field") {
        val alice = newEnv().playerIds[0]
        val creatures = listOf(TapForPowerCreatureData(a, "A", 2))
        fun tapForPower(action: com.wingedsheep.engine.core.GameAction) = LegalAction(
            action = action, actionType = "x", description = "d",
            tapForPower = true, tapForPowerRequired = 2, tapForPowerCreatures = creatures
        )
        costChoicesOf(tapForPower(CrewVehicle(alice, EntityId("vehicle"), emptyList()))).map { it.param } shouldContainExactly listOf("crew")
        costChoicesOf(tapForPower(SaddleMount(alice, EntityId("mount"), emptyList()))).map { it.param } shouldContainExactly listOf("saddle")
    }
})
