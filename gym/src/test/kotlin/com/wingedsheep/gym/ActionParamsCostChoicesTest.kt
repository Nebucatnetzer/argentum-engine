package com.wingedsheep.gym

import com.wingedsheep.engine.core.ActionParameterizer
import com.wingedsheep.engine.core.ActionParams
import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.CrewVehicle
import com.wingedsheep.engine.core.GameConfig
import com.wingedsheep.engine.core.PlayerConfig
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.mtg.sets.definitions.por.PortalSet
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AbilityId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

/**
 * The cost-choice and per-requirement target fields of [ActionParams]: each must land in the slot of
 * the completed `GameAction` the engine reads, and each must be refused by actions that can't use it.
 */
class ActionParamsCostChoicesTest : FunSpec({

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

    val card = EntityId("spell-card")
    val fodderA = EntityId("fodder-a")
    val fodderB = EntityId("fodder-b")

    test("crew fills the vehicle's crew creatures") {
        val env = newEnv()
        val alice = env.playerIds[0]
        val template = CrewVehicle(alice, EntityId("vehicle"), emptyList())
        val completed = ActionParameterizer.apply(template, ActionParams(crew = listOf(fodderA, fodderB)), env.state)
        (completed as CrewVehicle).crewCreatures shouldContainExactly listOf(fodderA, fodderB)
    }

    test("crew is refused by a spell") {
        val env = newEnv()
        val error = shouldThrow<IllegalArgumentException> {
            ActionParameterizer.apply(CastSpell(env.playerIds[0], card), ActionParams(crew = listOf(fodderA)), env.state)
        }
        error.message shouldContain "'crew'"
    }

    test("target groups set targets in requirement order and their group counts") {
        val env = newEnv()
        val (alice, bob) = env.playerIds
        val completed = ActionParameterizer.apply(
            CastSpell(alice, card),
            ActionParams(targetGroups = listOf(listOf(alice), listOf(bob))),
            env.state
        ) as CastSpell
        completed.targets shouldContainExactly listOf(ChosenTarget.Player(alice), ChosenTarget.Player(bob))
        completed.targetGroupCounts shouldBe listOf(1, 1)
    }

    test("targets and target groups together are refused") {
        val env = newEnv()
        val (alice, bob) = env.playerIds
        shouldThrow<IllegalArgumentException> {
            ActionParameterizer.apply(
                CastSpell(alice, card),
                ActionParams(targets = listOf(alice), targetGroups = listOf(listOf(bob))),
                env.state
            )
        }.message shouldContain "not both"
    }

    test("spell cost choices fill the additional cost payment") {
        val env = newEnv()
        val alice = env.playerIds[0]
        val completed = ActionParameterizer.apply(
            CastSpell(alice, card),
            ActionParams(sacrifice = listOf(fodderA), discard = listOf(fodderB)),
            env.state
        ) as CastSpell
        val payment = completed.additionalCostPayment!!
        payment.sacrificedPermanents shouldContainExactly listOf(fodderA)
        payment.discardedCards shouldContainExactly listOf(fodderB)
    }

    test("ability cost choices fill the cost payment") {
        val env = newEnv()
        val alice = env.playerIds[0]
        val template = ActivateAbility(alice, EntityId("source"), AbilityId("ability"))
        val completed = ActionParameterizer.apply(
            template,
            ActionParams(tap = listOf(fodderA, fodderB), exile = listOf(card)),
            env.state
        ) as ActivateAbility
        val payment = completed.costPayment!!
        payment.tappedPermanents shouldContainExactly listOf(fodderA, fodderB)
        payment.exiledCards shouldContainExactly listOf(card)
    }
})
