package com.wingedsheep.mtg.sets.definitions.otj

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.limited.BoosterStrategy
import com.wingedsheep.sdk.limited.LandSlotBooster
import com.wingedsheep.sdk.limited.PlayBooster
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Outlaws of Thunder Junction (2024)
 *
 * Set Code: OTJ
 * Release Date: April 19, 2024
 */
object OutlawsOfThunderJunctionSet : MtgSet {

    override val code = "OTJ"
    override val displayName = "Outlaws of Thunder Junction"
    override val releaseDate = "2024-04-19"
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
     * Play Booster: the non-foil wildcard is 65.4% C / 26% U / 8.3% R-M and the traditional-foil wildcard 58.7% C /
     * 32.4% U / 8.9% R-M. Six commons: the Breaking News card (OTP, not in the engine) and The List / Big Score slot
     * are not modelled. The land slot is a common Desert half the time and a basic otherwise. Figures from WotC's
     * "Collecting Outlaws of Thunder Junction" article, as kept in taw/magic-search-engine `data/boosters`.
     */
    override val boosterStrategy: BoosterStrategy by lazy {
        LandSlotBooster(
            base = PlayBooster(
                commons = 6,
                wildcardSlots = listOf(
                    PlayBooster.wildcard(common = 0.654, uncommon = 0.26, rareOrMythic = 0.083),
                    PlayBooster.wildcard(common = 0.587, uncommon = 0.324, rareOrMythic = 0.089),
                ),
            ),
            nonbasicLandNames = setOf(
                "Abraded Bluffs", "Bristling Backwoods", "Creosote Heath", "Eroded Canyon", "Festering Gulch",
                "Forlorn Flats", "Jagged Barrens", "Lonely Arroyo", "Lush Oasis", "Soured Springs",
            ),
            basicLands = basicLands.filter { it.metadata.inBooster },
            nonbasicChance = 0.5,
        )
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.otj.cards"
}
