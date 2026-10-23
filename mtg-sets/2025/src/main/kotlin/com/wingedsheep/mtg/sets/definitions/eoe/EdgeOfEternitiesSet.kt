package com.wingedsheep.mtg.sets.definitions.eoe

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.limited.BoosterStrategy
import com.wingedsheep.sdk.limited.PlayBooster
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Edge of Eternities Set (2025)
 *
 * Set Code: EOE
 * Release Date: August 1, 2025
 * Card Count: 261
 */
object EdgeOfEternitiesSet : MtgSet {

    override val code = "EOE"
    override val displayName = "Edge of Eternities"
    override val releaseDate = "2025-08-01"
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
     * Play Booster: the non-foil wildcard is 12.5% C / 62.5% U / 10.6% R / 0.2% M and the traditional-foil wildcard
     * 58% C / 32% U / 6.4% R / 1.1% M. Stellar Sights (EOS, not in the engine) and the treatment shares drop out. The
     * land slot is basics only. Figures from WotC's "Collecting Edge of Eternities" article, as kept in
     * taw/magic-search-engine `data/boosters`.
     */
    override val boosterStrategy: BoosterStrategy = PlayBooster(
        wildcardSlots = listOf(
            PlayBooster.wildcard(common = 0.125, uncommon = 0.625, rare = 0.106, mythic = 0.00225),
            PlayBooster.wildcard(common = 0.58, uncommon = 0.32, rare = 0.064, mythic = 0.011),
        ),
    )

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.eoe.cards"
}
