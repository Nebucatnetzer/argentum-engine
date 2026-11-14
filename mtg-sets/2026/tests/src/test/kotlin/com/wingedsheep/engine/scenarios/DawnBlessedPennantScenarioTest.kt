package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CastChoicesComponent
import com.wingedsheep.engine.state.components.battlefield.ChoiceValue
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.scripting.ChoiceSlot
import io.kotest.matchers.shouldBe

/**
 * Dawn-Blessed Pennant (ECL #254) — "{2}, {T}, Sacrifice this artifact: Return target card of the
 * chosen type from your graveyard to your hand."
 *
 * The Pennant is sacrificed as a cost, so by resolution its chosen type has to be read as it last
 * existed on the battlefield (CR 113.7a); it used to read the stripped graveyard card and fizzle.
 */
class DawnBlessedPennantScenarioTest : ScenarioTestBase() {

    private val abilityId by lazy { cardRegistry.getCard("Dawn-Blessed Pennant")!!.activatedAbilities[0].id }

    init {
        test("returns a card of the chosen type even though the Pennant was sacrificed") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Dawn-Blessed Pennant")
                .withLandsOnBattlefield(1, "Swamp", 2)
                .withCardInGraveyard(1, "Bitterbloom Bearer")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val pennant = game.findPermanent("Dawn-Blessed Pennant")!!
            game.state = game.state.updateEntity(pennant) { c ->
                c.with(CastChoicesComponent(chosen = mapOf(ChoiceSlot.CREATURE_TYPE to ChoiceValue.TextChoice("Faerie"))))
            }
            val bearer = game.findCardsInGraveyard(1, "Bitterbloom Bearer").single()

            game.execute(
                ActivateAbility(
                    playerId = game.player1Id,
                    sourceId = pennant,
                    abilityId = abilityId,
                    targets = listOf(ChosenTarget.Card(bearer, game.player1Id, Zone.GRAVEYARD)),
                )
            ).error shouldBe null
            game.resolveStack()

            game.isInHand(1, "Bitterbloom Bearer") shouldBe true
            game.isInGraveyard(1, "Dawn-Blessed Pennant") shouldBe true
        }
    }
}
