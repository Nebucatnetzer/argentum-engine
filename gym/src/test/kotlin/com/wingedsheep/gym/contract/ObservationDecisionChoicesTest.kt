package com.wingedsheep.gym.contract

import com.wingedsheep.engine.core.ChooseModeDecision
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.DecisionContext
import com.wingedsheep.engine.core.GameConfig
import com.wingedsheep.engine.core.MayAbilityContinuation
import com.wingedsheep.engine.core.ModeOption
import com.wingedsheep.engine.core.PendingDecision
import com.wingedsheep.engine.core.PlayerConfig
import com.wingedsheep.engine.core.SearchCardInfo
import com.wingedsheep.engine.core.SearchLibraryDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.TargetRequirementInfo
import com.wingedsheep.engine.core.suspendForDecision
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.gym.GameEnvironment
import com.wingedsheep.mtg.sets.definitions.por.PortalSet
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe

class ObservationDecisionChoicesTest : FunSpec({

    fun newEnv(): GameEnvironment {
        val registry = CardRegistry().apply {
            register(PortalSet.cards)
            register(PortalSet.basicLands)
        }
        val env = GameEnvironment.create(registry)
        env.reset(
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
        return env
    }

    /** Suspend [env] on [question] for [decider] and observe it from [perspective]. */
    fun observeDecision(
        env: GameEnvironment,
        decider: EntityId,
        perspective: EntityId,
        revealAll: Boolean = false,
        question: (String) -> PendingDecision
    ): PendingDecisionView {
        val suspended = env.state.suspendForDecision(
            question = question,
            answer = MayAbilityContinuation(
                playerId = decider,
                sourceName = null,
                effectIfYes = null,
                effectIfNo = null,
                effectContext = EffectContext(sourceId = null, controllerId = decider)
            )
        )
        val result = ObservationBuilder(env.cardRegistry).build(suspended.state, perspective, emptyList(), revealAll)
        return (result.observation as TrainingObservation).pendingDecision!!
    }

    test("choose-targets lists legal targets per requirement for the decider only") {
        val env = newEnv()
        val (alice, bob) = env.playerIds
        val creatureA = EntityId("creature-a")
        val creatureB = EntityId("creature-b")
        val question = { id: String ->
            ChooseTargetsDecision(
                id = id,
                playerId = alice,
                prompt = "Choose targets",
                context = DecisionContext(),
                targetRequirements = listOf(
                    TargetRequirementInfo(index = 0, description = "target creature", minTargets = 1, maxTargets = 1),
                    TargetRequirementInfo(index = 1, description = "target player", minTargets = 0, maxTargets = 1)
                ),
                legalTargets = mapOf(0 to listOf(creatureA, creatureB), 1 to listOf(alice, bob))
            )
        }

        val deciderView = observeDecision(env, alice, alice, question = question)
        deciderView.requiresStructuredResponse shouldBe true
        deciderView.targetRequirements shouldContainExactly listOf(
            TargetRequirementView(0, "target creature", 1, 1, listOf(creatureA, creatureB)),
            TargetRequirementView(1, "target player", 0, 1, listOf(alice, bob))
        )

        observeDecision(env, alice, bob, question = question).targetRequirements.shouldBeEmpty()
    }

    test("select-cards lists only selectable options") {
        val env = newEnv()
        val alice = env.playerIds[0]
        val keep = EntityId("card-keep")
        val locked = EntityId("card-locked")
        val view = observeDecision(env, alice, alice) { id ->
            SelectCardsDecision(
                id = id,
                playerId = alice,
                prompt = "Choose 2 cards to discard",
                context = DecisionContext(),
                options = listOf(keep, locked, EntityId("card-other")),
                minSelections = 2,
                maxSelections = 2,
                nonSelectableOptions = listOf(locked)
            )
        }
        view.candidateIds shouldContainExactly listOf(keep, EntityId("card-other"))
        view.shape.minSelections shouldBe 2
    }

    test("library search exposes hit identities to the searcher but not to the opponent") {
        val env = newEnv()
        val (alice, bob) = env.playerIds
        val hit = EntityId("library-hit")
        val question = { id: String ->
            SearchLibraryDecision(
                id = id,
                playerId = bob,
                prompt = "Search for a land",
                context = DecisionContext(),
                options = listOf(hit),
                minSelections = 0,
                maxSelections = 1,
                cards = mapOf(hit to SearchCardInfo(name = "Mountain", manaCost = "", typeLine = "Basic Land — Mountain")),
                filterDescription = "a land card"
            )
        }

        val searcher = observeDecision(env, bob, bob, question = question)
        searcher.candidateIds shouldContainExactly listOf(hit)
        searcher.candidateInfo[hit]!!.name shouldBe "Mountain"

        val opponent = observeDecision(env, bob, alice, question = question)
        opponent.candidateIds.shouldBeEmpty()
        opponent.candidateInfo.shouldBeEmpty()

        observeDecision(env, bob, alice, revealAll = true, question = question).candidateIds shouldContainExactly listOf(hit)
    }

    test("multi-mode choice lists every mode with its availability") {
        val env = newEnv()
        val alice = env.playerIds[0]
        val view = observeDecision(env, alice, alice) { id ->
            ChooseModeDecision(
                id = id,
                playerId = alice,
                prompt = "Choose two",
                context = DecisionContext(),
                modes = listOf(ModeOption(0, "Draw a card"), ModeOption(1, "Gain 3 life"), ModeOption(2, "Deal 2 damage", available = false)),
                minModes = 2,
                maxModes = 2
            )
        }
        view.requiresStructuredResponse shouldBe true
        view.modes shouldContainExactly listOf(
            DecisionModeView(0, "Draw a card", true),
            DecisionModeView(1, "Gain 3 life", true),
            DecisionModeView(2, "Deal 2 damage", false)
        )
    }
})
