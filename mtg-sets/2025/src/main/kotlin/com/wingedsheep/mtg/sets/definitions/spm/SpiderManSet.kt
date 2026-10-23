package com.wingedsheep.mtg.sets.definitions.spm

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.limited.BoosterStrategy
import com.wingedsheep.sdk.limited.LandSlotBooster
import com.wingedsheep.sdk.limited.PlayBooster
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Marvel's Spider-Man (2025)
 *
 * Set Code: SPM
 * Release Date: September 26, 2025
 */
object SpiderManSet : MtgSet {

    override val code = "SPM"
    override val displayName = "Marvel's Spider-Man"
    override val releaseDate = "2025-09-26"

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
     * Play Booster: the non-foil wildcard is 70.8% C / 4.1% U / 20.9% R / 2.9% M (WotC's published figures) and the
     * traditional-foil wildcard 65.8% C / 24.1% U / 7.8% R / 1.1% M. The Source Material card (MAR, not in the engine)
     * stays a common. The land slot is a common dual land half the time and a basic otherwise. Figures from WotC's
     * "Collecting Marvel's Spider-Man" article, as kept in taw/magic-search-engine `data/boosters`.
     */
    override val boosterStrategy: BoosterStrategy by lazy {
        LandSlotBooster(
            base = PlayBooster(
                wildcardSlots = listOf(
                    PlayBooster.wildcard(common = 0.708, uncommon = 0.041, rare = 0.209, mythic = 0.029),
                    PlayBooster.wildcard(common = 0.658, uncommon = 0.241, rare = 0.078, mythic = 0.011),
                ),
            ),
            nonbasicLandNames = setOf(
                "Ominous Asylum", "Savage Mansion", "Sinister Hideout", "Suburban Sanctuary",
                "University Campus",
            ),
            basicLands = basicLands.filter { it.metadata.inBooster },
            nonbasicChance = 0.5,
        )
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.spm.cards"
}
