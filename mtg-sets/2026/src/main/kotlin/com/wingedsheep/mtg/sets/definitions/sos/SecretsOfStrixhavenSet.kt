package com.wingedsheep.mtg.sets.definitions.sos

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.limited.BoosterStrategy
import com.wingedsheep.sdk.limited.LandSlotBooster
import com.wingedsheep.sdk.limited.PlayBooster
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Secrets of Strixhaven (2026)
 *
 * A return to Strixhaven University, revisiting the five mage colleges and their
 * instant/sorcery "spells matter" identity.
 *
 * Set Code: SOS
 * Release Date: April 24, 2026
 */
object SecretsOfStrixhavenSet : MtgSet {

    override val code = "SOS"
    override val displayName = "Secrets of Strixhaven"
    override val releaseDate = "2026-04-24"
    override val sealedSupported = true

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val basicLands: List<CardDefinition> by lazy {
        CardDiscovery.findBasicLandsIn(CARDS_PACKAGE, code)
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    /**
     * Play Booster: the non-foil wildcard is 39.1% C / 39.1% U / 19.8% R / 2% M and the traditional-foil wildcard
     * 54.4% C / 33.6% U / 8.8% R-M. Six commons: the Mystical Archive card (SOA, not in the engine) is not modelled.
     * The land slot is a common dual land half the time and a basic otherwise. Figures from WotC's "Collecting Secrets
     * of Strixhaven" article, as kept in taw/magic-search-engine `data/boosters`.
     */
    override val boosterStrategy: BoosterStrategy by lazy {
        LandSlotBooster(
            base = PlayBooster(
                commons = 6,
                wildcardSlots = listOf(
                    PlayBooster.wildcard(common = 0.391, uncommon = 0.391, rare = 0.198, mythic = 0.02),
                    PlayBooster.wildcard(common = 0.544, uncommon = 0.336, rareOrMythic = 0.088),
                ),
            ),
            nonbasicLandNames = setOf(
                "Fields of Strife", "Forum of Amity", "Paradox Gardens", "Spectacle Summit", "Titan's Grave",
            ),
            basicLands = basicLands.filter { it.metadata.inBooster },
            nonbasicChance = 0.5,
        )
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.sos.cards"
}
