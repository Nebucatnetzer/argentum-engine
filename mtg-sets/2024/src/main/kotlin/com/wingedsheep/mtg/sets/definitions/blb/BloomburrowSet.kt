package com.wingedsheep.mtg.sets.definitions.blb

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.limited.BoosterStrategy
import com.wingedsheep.sdk.limited.PlayBooster
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Bloomburrow Set (2024)
 *
 * Bloomburrow is a plane inhabited entirely by anthropomorphic animals.
 * The set features a variety of creature types including Mice, Rabbits,
 * Frogs, Otters, and more.
 *
 * Set Code: BLB
 * Release Date: August 2, 2024
 * Card Count: 261
 */
object BloomburrowSet : MtgSet {

    override val code = "BLB"
    override val displayName = "Bloomburrow"
    override val releaseDate = "2024-08-02"
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
     * Play Booster: the non-foil wildcard is 20% C / 64.5% U / 15.5% R-M and the traditional-foil wildcard 58% C / 32%
     * U / 10% R-M. The land slot is basics only. Figures from WotC's "Collecting Bloomburrow" article, as kept in
     * taw/magic-search-engine `data/boosters`.
     */
    override val boosterStrategy: BoosterStrategy = PlayBooster(
        wildcardSlots = listOf(
            PlayBooster.wildcard(common = 0.2, uncommon = 0.645, rareOrMythic = 0.155),
            PlayBooster.wildcard(common = 0.58, uncommon = 0.32, rareOrMythic = 0.1),
        ),
    )

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.blb.cards"
}
