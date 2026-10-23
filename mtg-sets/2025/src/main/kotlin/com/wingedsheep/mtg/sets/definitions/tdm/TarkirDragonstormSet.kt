package com.wingedsheep.mtg.sets.definitions.tdm

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.limited.BoosterStrategy
import com.wingedsheep.sdk.limited.LandSlotBooster
import com.wingedsheep.sdk.limited.PlayBooster
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Tarkir: Dragonstorm (2025)
 *
 * Set Code: TDM
 * Release Date: April 11, 2025
 */
object TarkirDragonstormSet : MtgSet {

    override val code = "TDM"
    override val displayName = "Tarkir: Dragonstorm"
    override val releaseDate = "2025-04-11"
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
     * Play Booster: the non-foil wildcard is 12.5% C / 58.3% U / 18.2% R-M and the traditional-foil wildcard 56.5% C /
     * 32% U / 7.5% R-M. Booster Fun shares are treatments and drop out. The land slot is a common gain land 100 times
     * in 115 (10 duals at rate 10 vs 15 basic arts at rate 1) and a basic otherwise. Figures from WotC's "Collecting
     * Tarkir: Dragonstorm" article, as kept in taw/magic-search-engine `data/boosters`.
     */
    override val boosterStrategy: BoosterStrategy by lazy {
        LandSlotBooster(
            base = PlayBooster(
                wildcardSlots = listOf(
                    PlayBooster.wildcard(common = 0.125, uncommon = 0.583, rareOrMythic = 0.182),
                    PlayBooster.wildcard(common = 0.565, uncommon = 0.32, rareOrMythic = 0.075),
                ),
            ),
            nonbasicLandNames = setOf(
                "Bloodfell Caves", "Blossoming Sands", "Dismal Backwater", "Jungle Hollow", "Rugged Highlands",
                "Scoured Barrens", "Swiftwater Cliffs", "Thornwood Falls", "Tranquil Cove",
                "Wind-Scarred Crag",
            ),
            basicLands = basicLands.filter { it.metadata.inBooster },
            nonbasicChance = 100.0 / 115,
        )
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.tdm.cards"
}
