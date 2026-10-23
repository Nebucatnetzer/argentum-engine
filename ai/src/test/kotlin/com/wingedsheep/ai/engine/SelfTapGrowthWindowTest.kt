package com.wingedsheep.ai.engine

import com.wingedsheep.ai.engine.knowledge.HoldPolicy
import com.wingedsheep.ai.engine.knowledge.IntentCatalog
import com.wingedsheep.ai.engine.knowledge.TimingVerdict
import com.wingedsheep.ai.puzzles.advanceToPriority
import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.GameAction
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * [AiProfile.tapToGrowAtTheirEndStep]: Clockwork Hydra's `{T}: Put a +1/+1 counter on this
 * creature` waits for the opponent's end step instead of costing the Hydra its attack or its block.
 *
 * The player report (2026-10-23): the engine AI tapped the Hydra every turn, in its own precombat
 * main and in the opponent's, so it grew and never fought. Each decision-level test pins the answer
 * without the flag beside the one with it, so the misplay stays visible.
 */
class SelfTapGrowthWindowTest : ScenarioTestBase() {

    private val intents by lazy { IntentCatalog.of(cardRegistry) }
    private val off by lazy { HoldPolicy(intents) }
    private val on by lazy { HoldPolicy(intents, tapToGrowAtTheirEndStep = true) }

    private val liveWithoutFlag = AiProfile.LIVE.copy(id = "live-without-tapgrow", tapToGrowAtTheirEndStep = false)

    /** A Clockwork Hydra at four counters facing a Grizzly Bears, seat 1 holding priority in [step] of [activePlayer]'s turn. */
    private fun hydraBoard(activePlayer: Int, step: Step): TestGame {
        val game = scenario().withPlayers()
            .withRngSeed(20261023L)
            .withActivePlayer(activePlayer)
            .withCardOnBattlefield(1, "Clockwork Hydra")
            .withLandsOnBattlefield(1, "Plains", 3)
            .withCardOnBattlefield(2, "Grizzly Bears")
            .withLandsOnBattlefield(2, "Forest", 3)
            .apply { repeat(8) { withCardInLibrary(1, "Plains") } }
            .apply { repeat(8) { withCardInLibrary(2, "Forest") } }
            .build()
        val hydra = game.findPermanent("Clockwork Hydra")!!
        game.state = game.state.updateEntity(hydra) { c ->
            c.with((c.get<CountersComponent>() ?: CountersComponent()).withCounters(CounterType.PLUS_ONE_PLUS_ONE, 4))
        }
        return game.advanceToPriority(1, step)
    }

    private fun TestGame.hydraVerdict(policy: HoldPolicy): TimingVerdict {
        val grow = cardRegistry.getCard("Clockwork Hydra")!!.script.activatedAbilities.single()
        val activation = ActivateAbility(player1Id, findPermanent("Clockwork Hydra")!!, grow.id)
        return policy.verdictFor(state, player1Id, "Clockwork Hydra", activation = activation)
    }

    private fun TestGame.activatesHydra(profile: AiProfile): Boolean {
        val action: GameAction = AIPlayer.create(cardRegistry, player1Id, profile).chooseAction(state)
        return withClue("chose $action") {
            action is ActivateAbility &&
                state.getEntity(action.sourceId)?.get<CardComponent>()?.name == "Clockwork Hydra"
        }
    }

    init {
        test("our precombat main: the tap would cost the attack, so it is held") {
            val game = hydraBoard(activePlayer = 1, step = Step.PRECOMBAT_MAIN)

            game.hydraVerdict(off) shouldBe TimingVerdict.Neutral
            game.hydraVerdict(on) shouldBe TimingVerdict.NoWindow
            game.activatesHydra(liveWithoutFlag) shouldBe true
            game.activatesHydra(AiProfile.LIVE) shouldBe false
        }

        test("our postcombat main: the tap would still cost the block on their turn") {
            val game = hydraBoard(activePlayer = 1, step = Step.POSTCOMBAT_MAIN)

            game.hydraVerdict(on) shouldBe TimingVerdict.NoWindow
        }

        test("their precombat main: the tap would cost the block, so it is held") {
            val game = hydraBoard(activePlayer = 2, step = Step.PRECOMBAT_MAIN)

            game.hydraVerdict(on) shouldBe TimingVerdict.NoWindow
            game.activatesHydra(liveWithoutFlag) shouldBe true
            game.activatesHydra(AiProfile.LIVE) shouldBe false
        }

        test("their end step: the tap is free, and the AI takes the counter") {
            val game = hydraBoard(activePlayer = 2, step = Step.END)

            game.hydraVerdict(on) shouldBe TimingVerdict.Neutral
            game.activatesHydra(AiProfile.LIVE) shouldBe true
        }
    }
}
