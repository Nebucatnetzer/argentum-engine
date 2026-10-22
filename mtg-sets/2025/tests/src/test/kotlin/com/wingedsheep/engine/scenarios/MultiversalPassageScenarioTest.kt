package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.spm.cards.MultiversalPassage
import com.wingedsheep.mtg.sets.definitions.spm.cards.SandmanShiftingScoundrel
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AbilityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import com.wingedsheep.engine.core.Outcome
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Multiversal Passage (SPM #180): "As this land enters, choose a basic land type. Then you may
 * pay 2 life. If you don't, it enters tapped. This land is the chosen type."
 *
 * The whole clause is one [com.wingedsheep.sdk.scripting.OnEnterRun] composing three atoms:
 * ChooseOption(BASIC_LAND_TYPE) → SetLandType(Self, Permanent, fromChosen) → an optional pay-2-life
 * gate whose decline branch taps the land. These tests prove the chosen type sticks (subtype +
 * intrinsic mana), and that the pay/decline branch controls tapped-ness.
 */
/**
 * Landfall witness for the deferred-trigger case below. The entry's `ZoneChangeEvent` is emitted
 * before the as-enters clause pauses for its choice, so this trigger only fires if the paused
 * result carries that event up to the resume path.
 */
private val LandfallWatcher = card("Landfall Watcher") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Creature — Spirit"
    power = 1
    toughness = 1
    oracleText = "Whenever a land you control enters, you gain 1 life."

    triggeredAbility {
        trigger = Triggers.a(GameObjectFilter.Land.youControl()).enters()
        effect = Effects.GainLife(1)
    }
}

class MultiversalPassageScenarioTest : FunSpec({

    fun GameTestDriver.pool(playerId: EntityId): ManaPoolComponent =
        state.getEntity(playerId)?.get<ManaPoolComponent>() ?: ManaPoolComponent()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(
            TestCards.all + MultiversalPassage + SandmanShiftingScoundrel + LandfallWatcher
        )
        return driver
    }

    fun GameTestDriver.playPassageChoosing(player: EntityId, landType: String): EntityId {
        val passage = putCardInHand(player, "Multiversal Passage")
        (playLand(player, passage).outcome is Outcome.Paused) shouldBe true

        // First decision: choose a basic land type.
        val choice = pendingDecision
        choice.shouldBeInstanceOf<ChooseOptionDecision>()
        choice.options shouldContain landType
        submitDecision(player, OptionChosenResponse(choice.id, choice.options.indexOf(landType)))
        return passage
    }

    test("choosing Island makes it an Island that taps for U; paying 2 life enters untapped") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val passage = driver.playPassageChoosing(p1, "Island")

        // Second decision: you may pay 2 life. Pay it -> land enters untapped.
        val payDecision = driver.pendingDecision
        payDecision.shouldBeInstanceOf<YesNoDecision>()
        // Paying is its own reward (no "If you do, ." clause), and the decline branch names the
        // land rather than calling it "this creature".
        payDecision.prompt shouldBe "You may pay 2 life. If you don't, Multiversal Passage enters tapped"
        driver.submitYesNo(p1, true)

        driver.pendingDecision shouldBe null
        driver.state.getEntity(passage)?.has<TappedComponent>() shouldBe false
        driver.getLifeTotal(p1) shouldBe 18

        // It IS an Island now (chosen type replaces its subtypes) ...
        driver.state.projectedState.hasSubtype(passage, "Island").shouldBeTrue()
        // ... and taps for {U} via the intrinsic Island mana ability.
        driver.submitSuccess(ActivateAbility(p1, passage, AbilityId.intrinsicMana('U')))
        driver.pool(p1).blue shouldBe 1
    }

    test("declining the 2 life makes it enter tapped (still the chosen Island type)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val passage = driver.playPassageChoosing(p1, "Island")

        val payDecision = driver.pendingDecision
        payDecision.shouldBeInstanceOf<YesNoDecision>()
        driver.submitYesNo(p1, false)

        driver.pendingDecision shouldBe null
        driver.state.getEntity(passage)?.has<TappedComponent>() shouldBe true
        driver.getLifeTotal(p1) shouldBe 20
        driver.state.projectedState.hasSubtype(passage, "Island").shouldBeTrue()
    }

    test("returning it to the battlefield with an effect asks for the basic land type again") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Sandman's graveyard ability ("return this card and target land card from your graveyard
        // to the battlefield tapped") is a plain MoveToZone onto the battlefield — the same shape
        // every other non-cast entry uses (reanimation, blink, the earthbend return trigger).
        val sandman = driver.putCardInGraveyard(p1, "Sandman, Shifting Scoundrel")
        val passage = driver.putCardInGraveyard(p1, "Multiversal Passage")
        driver.giveMana(p1, Color.GREEN, 5) // {3}{G}{G}

        driver.submitSuccess(
            ActivateAbility(
                playerId = p1,
                sourceId = sandman,
                abilityId = SandmanShiftingScoundrel.activatedAbilities.first().id,
                targets = listOf(ChosenTarget.Card(passage, ownerId = p1, zone = Zone.GRAVEYARD)),
            )
        )
        var guard = 0
        while (driver.state.stack.isNotEmpty() && driver.pendingDecision == null && guard++ < 20) {
            driver.bothPass()
        }

        // The as-enters clause applies to this entry too (it is not limited to playing the land):
        // choose a basic land type, then the pay-2-life gate.
        val choice = driver.pendingDecision
        choice.shouldBeInstanceOf<ChooseOptionDecision>()
        choice.options shouldContain "Mountain"
        driver.submitDecision(p1, OptionChosenResponse(choice.id, choice.options.indexOf("Mountain")))

        val payDecision = driver.pendingDecision
        payDecision.shouldBeInstanceOf<YesNoDecision>()
        driver.submitYesNo(p1, false)

        // It came back as a Mountain rather than a typeless land that can't do anything.
        driver.state.projectedState.hasSubtype(passage, "Mountain").shouldBeTrue()
        driver.untapPermanent(passage) // it returned tapped, per Sandman's ability
        driver.submitSuccess(ActivateAbility(p1, passage, AbilityId.intrinsicMana('R')))
        driver.pool(p1).red shouldBe 1
    }

    test("the entry's landfall trigger survives the as-enters pause and fires after the choice") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(p1, "Landfall Watcher")
        val sandman = driver.putCardInGraveyard(p1, "Sandman, Shifting Scoundrel")
        val passage = driver.putCardInGraveyard(p1, "Multiversal Passage")
        driver.giveMana(p1, Color.GREEN, 5)

        driver.submitSuccess(
            ActivateAbility(
                playerId = p1,
                sourceId = sandman,
                abilityId = SandmanShiftingScoundrel.activatedAbilities.first().id,
                targets = listOf(ChosenTarget.Card(passage, ownerId = p1, zone = Zone.GRAVEYARD)),
            )
        )
        var guard = 0
        while (driver.state.stack.isNotEmpty() && driver.pendingDecision == null && guard++ < 20) {
            driver.bothPass()
        }

        // Paused mid-entry for the basic land type. The land is physically on the battlefield and
        // its ZoneChangeEvent has been emitted, but nothing has resolved off it yet.
        val choice = driver.pendingDecision
        choice.shouldBeInstanceOf<ChooseOptionDecision>()
        driver.getLifeTotal(p1) shouldBe 20

        driver.submitDecision(p1, OptionChosenResponse(choice.id, choice.options.indexOf("Mountain")))
        val payDecision = driver.pendingDecision
        payDecision.shouldBeInstanceOf<YesNoDecision>()
        driver.submitYesNo(p1, false)

        // The entry event was carried through the pause, so the landfall trigger was deferred onto
        // the stack rather than dropped. Drain it.
        guard = 0
        while (driver.state.stack.isNotEmpty() && driver.pendingDecision == null && guard++ < 20) {
            driver.bothPass()
        }
        driver.getLifeTotal(p1) shouldBe 21
    }

    test("choosing Mountain makes it a Mountain that taps for R") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val passage = driver.playPassageChoosing(p1, "Mountain")
        driver.submitYesNo(p1, true)

        driver.state.projectedState.hasSubtype(passage, "Mountain").shouldBeTrue()
        driver.state.projectedState.hasSubtype(passage, "Island") shouldBe false
        driver.submitSuccess(ActivateAbility(p1, passage, AbilityId.intrinsicMana('R')))
        driver.pool(p1).red shouldBe 1
    }
})
