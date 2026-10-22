package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.Outcome
import com.wingedsheep.engine.core.TappedEvent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.TheWanderingMinstrel
import com.wingedsheep.mtg.sets.definitions.lrw.cards.AuntiesHovel
import com.wingedsheep.mtg.sets.definitions.stx.cards.FrostboilSnarl
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe

/**
 * The "if you don't, this land enters tapped" rider of an `OnEnterRun` as-enters replacement
 * (`Effects.EnterTapped()`) — the Lorwyn reveal lands, the shadowlands, the snarls.
 *
 * It is an entry modification, not a tap: the land is put onto the battlefield tapped (no
 * "becomes tapped" event), and a "lands you control enter untapped" replacement (The Wandering
 * Minstrel) wins over it (CR 616.1). It used to be `Effects.Tap(EffectTarget.Self)`, which the
 * Minstrel never saw because the played-land path runs `OnEnterRun` before its enters-untapped
 * check.
 */
class EnterTappedRiderScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(TheWanderingMinstrel, AuntiesHovel, FrostboilSnarl))
        // A hand of Forests: nothing to reveal for either land, so the rider always applies.
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("without a reveal the land enters tapped, and doesn't become tapped") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val hovel = driver.putCardInHand(me, "Auntie's Hovel")

        val result = driver.playLand(me, hovel)

        result.outcome shouldBe Outcome.Done
        driver.isTapped(hovel) shouldBe true
        result.events.filterIsInstance<TappedEvent>().shouldBeEmpty()
    }

    test("with The Wandering Minstrel out, the land enters untapped") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        driver.putPermanentOnBattlefield(me, "The Wandering Minstrel")
        val hovel = driver.putCardInHand(me, "Auntie's Hovel")

        driver.playLand(me, hovel).outcome shouldBe Outcome.Done

        driver.isTapped(hovel) shouldBe false
    }

    test("a snarl played under The Wandering Minstrel enters untapped too") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        driver.putPermanentOnBattlefield(me, "The Wandering Minstrel")
        val snarl = driver.putCardInHand(me, "Frostboil Snarl")

        driver.playLand(me, snarl).outcome shouldBe Outcome.Done

        driver.isTapped(snarl) shouldBe false
    }

    test("the Minstrel only covers lands its controller controls") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)
        driver.putPermanentOnBattlefield(opponent, "The Wandering Minstrel")
        val hovel = driver.putCardInHand(me, "Auntie's Hovel")

        driver.playLand(me, hovel).outcome shouldBe Outcome.Done

        driver.isTapped(hovel) shouldBe true
    }
})
