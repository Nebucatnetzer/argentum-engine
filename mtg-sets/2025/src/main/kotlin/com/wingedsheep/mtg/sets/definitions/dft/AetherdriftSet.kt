package com.wingedsheep.mtg.sets.definitions.dft

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.limited.BoosterStrategy
import com.wingedsheep.sdk.limited.LandSlotBooster
import com.wingedsheep.sdk.limited.PlayBooster
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Aetherdrift (2025)
 *
 * Set Code: DFT
 * Release Date: February 14, 2025
 */
object AetherdriftSet : MtgSet {

    override val code = "DFT"
    override val displayName = "Aetherdrift"
    override val releaseDate = "2025-02-14"

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
     * Play Booster: the non-foil wildcard is 12.5% C / 66.7% U / 20.8% R-M and the traditional-foil wildcard 61% C /
     * 30.5% U / 8.5% R-M. The land slot is a common gain land half the time and a basic otherwise. Figures from WotC's
     * "Collecting Aetherdrift" article, as kept in taw/magic-search-engine `data/boosters`.
     */
    override val boosterStrategy: BoosterStrategy by lazy {
        LandSlotBooster(
            base = PlayBooster(
                wildcardSlots = listOf(
                    PlayBooster.wildcard(common = 0.125, uncommon = 0.667, rareOrMythic = 0.208),
                    PlayBooster.wildcard(common = 0.61, uncommon = 0.305, rareOrMythic = 0.085),
                ),
            ),
            nonbasicLandNames = setOf(
                "Bloodfell Caves", "Blossoming Sands", "Dismal Backwater", "Jungle Hollow", "Rugged Highlands",
                "Scoured Barrens", "Swiftwater Cliffs", "Thornwood Falls", "Tranquil Cove",
                "Wind-Scarred Crag",
            ),
            basicLands = basicLands.filter { it.metadata.inBooster },
            nonbasicChance = 0.5,
        )
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.dft.cards"
}
