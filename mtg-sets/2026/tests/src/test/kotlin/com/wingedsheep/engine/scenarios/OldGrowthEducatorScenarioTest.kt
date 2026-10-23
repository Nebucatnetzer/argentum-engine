package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.OrderTriggersDecision
import com.wingedsheep.engine.core.TriggerOrderResponse
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario tests for Old-Growth Educator (Secrets of Strixhaven #207).
 *
 * Infusion — When this creature enters, put two +1/+1 counters on it if you gained life this turn.
 *
 * Per the card's ruling the ability triggers whether or not you've gained life, and checks as it
 * resolves — so Bogwater Lumaret's lifegain from the same entry counts if it resolves first.
 */
class OldGrowthEducatorScenarioTest : ScenarioTestBase() {

    private fun TestGame.plusOneCounters(): Int =
        state.getEntity(findPermanent("Old-Growth Educator")!!)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    private fun setup(withLumaret: Boolean = true): TestGame {
        var builder = scenario()
            .withPlayers("Player", "Opponent")
            .withCardInHand(1, "Old-Growth Educator")
            .withLandsOnBattlefield(1, "Swamp", 2)
            .withLandsOnBattlefield(1, "Forest", 2)
            .withLifeTotal(1, 20)
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        if (withLumaret) builder = builder.withCardOnBattlefield(1, "Bogwater Lumaret")
        return builder.build()
    }

    init {
        context("Infusion checks as it resolves") {

            test("with automatic ordering, Bogwater Lumaret's lifegain resolves first and the counters go on") {
                val game = setup()
                game.castSpell(1, "Old-Growth Educator").error shouldBe null
                game.resolveStack()

                game.getPendingDecision() shouldBe null
                game.getLifeTotal(1) shouldBe 21
                withClue("the life gained by the simultaneous trigger counts") {
                    game.plusOneCounters() shouldBe 2
                }
            }

            test("ordering it yourself: Infusion is offered last and marked, and either order is honoured") {
                for ((order, counters) in listOf(listOf(0, 1) to 2, listOf(1, 0) to 0)) {
                    val game = setup()
                    game.state = game.state.withManualTriggerOrdering(game.player1Id, true)
                    game.castSpell(1, "Old-Growth Educator").error shouldBe null
                    game.resolveStack()

                    val decision = game.getPendingDecision().shouldBeInstanceOf<OrderTriggersDecision>()
                    decision.triggers.map { it.sourceName } shouldContainExactly
                        listOf("Bogwater Lumaret", "Old-Growth Educator")
                    decision.triggers.map { it.checksOnResolution } shouldContainExactly listOf(false, true)

                    game.submitDecision(TriggerOrderResponse(decision.id, order)).error shouldBe null
                    game.resolveStack()

                    game.getLifeTotal(1) shouldBe 21
                    withClue("resolution order $order") { game.plusOneCounters() shouldBe counters }
                }
            }

            test("without any lifegain the ability still resolves, doing nothing") {
                val game = setup(withLumaret = false)
                game.castSpell(1, "Old-Growth Educator").error shouldBe null
                game.resolveStack()

                game.getPendingDecision() shouldBe null
                game.plusOneCounters() shouldBe 0
            }
        }
    }
}
