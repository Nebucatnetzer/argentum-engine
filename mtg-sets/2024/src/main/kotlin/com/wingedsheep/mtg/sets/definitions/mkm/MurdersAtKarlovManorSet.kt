package com.wingedsheep.mtg.sets.definitions.mkm

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.limited.BoosterStrategy
import com.wingedsheep.sdk.limited.PlayBooster
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Murders at Karlov Manor (2024)
 *
 * Set Code: MKM
 * Release Date: February 9, 2024
 */
object MurdersAtKarlovManorSet : MtgSet {

    override val code = "MKM"
    override val displayName = "Murders at Karlov Manor"
    override val releaseDate = "2024-02-09"
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
     * Play Booster: the non-foil wildcard is a rare dual land 1 time in 6 and otherwise 20.3% C / 61.6% U / 18.1% R-M
     * and the traditional-foil wildcard 58% C / 32% U / 10% R-M. The List slot and the basic land are not modelled.
     * Figures from WotC's "Collecting Murders at Karlov Manor" article, as kept in taw/magic-search-engine
     * `data/boosters`.
     */
    override val boosterStrategy: BoosterStrategy = PlayBooster(
        wildcardSlots = listOf(
            PlayBooster.wildcard(common = 0.169, uncommon = 0.513, rare = 0.299, mythic = 0.019),
            PlayBooster.wildcard(common = 0.58, uncommon = 0.32, rareOrMythic = 0.1),
        ),
    )

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.mkm.cards"
}
