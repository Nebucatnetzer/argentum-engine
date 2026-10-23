package com.wingedsheep.mtg.sets.definitions.dsk

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.limited.BoosterStrategy
import com.wingedsheep.sdk.limited.LandSlotBooster
import com.wingedsheep.sdk.limited.PlayBooster
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Duskmourn: House of Horror (2024)
 *
 * Set Code: DSK
 * Release Date: September 27, 2024
 */
object DuskmournSet : MtgSet {

    override val code = "DSK"
    override val displayName = "Duskmourn: House of Horror"
    override val releaseDate = "2024-09-27"
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
     * Play Booster: the non-foil wildcard is 18.1% C / 67.1% U / 15.1% R-M and the traditional-foil wildcard 58% C /
     * 32% U / 10% R-M. The land slot is one of the ten common dual lands half the time and a basic otherwise. Figures
     * from WotC's "Collecting Duskmourn: House of Horror" article, as kept in taw/magic-search-engine `data/boosters`.
     */
    override val boosterStrategy: BoosterStrategy by lazy {
        LandSlotBooster(
            base = PlayBooster(
                wildcardSlots = listOf(
                    PlayBooster.wildcard(common = 0.181, uncommon = 0.671, rareOrMythic = 0.151),
                    PlayBooster.wildcard(common = 0.58, uncommon = 0.32, rareOrMythic = 0.1),
                ),
            ),
            nonbasicLandNames = setOf(
                "Abandoned Campground", "Bleeding Woods", "Etched Cornfield", "Lakeside Shack", "Murky Sewer",
                "Neglected Manor", "Peculiar Lighthouse", "Raucous Carnival", "Razortrap Gorge",
                "Strangled Cemetery",
            ),
            basicLands = basicLands.filter { it.metadata.inBooster },
            nonbasicChance = 0.5,
        )
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.dsk.cards"
}
