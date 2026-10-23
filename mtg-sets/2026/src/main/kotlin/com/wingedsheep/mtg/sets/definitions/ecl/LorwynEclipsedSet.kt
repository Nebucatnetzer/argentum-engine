package com.wingedsheep.mtg.sets.definitions.ecl

import com.wingedsheep.mtg.sets.definitions.ecl.cards.LorwynEclipsedVariantPrintings
import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.limited.BoosterStrategy
import com.wingedsheep.sdk.limited.PlayBooster
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Lorwyn Eclipsed Set (2026)
 *
 * A return to the Lorwyn/Shadowmoor plane featuring tribal themes
 * with Elves, Kithkin, Merfolk, Goblins, and Elementals.
 *
 * Set Code: ECL
 * Release Date: January 23, 2026
 * Card Count: 273
 */
object LorwynEclipsedSet : MtgSet {

    override val code = "ECL"
    override val displayName = "Lorwyn Eclipsed"
    override val releaseDate = "2026-01-23"
    override val sealedSupported = true

    /** ~15% of booster cards roll into their showcase / borderless [printings]. */
    override val boosterVariantChance = 0.15

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val basicLands: List<CardDefinition> by lazy {
        CardDiscovery.findBasicLandsIn(CARDS_PACKAGE, code)
    }

    /**
     * Per-card discovered printing rows (reprints) plus the curated showcase / borderless
     * variant printings ([LorwynEclipsedVariantPrintings]). The variant rows are an aggregate
     * list, which [CardDiscovery.findPrintingsIn] deliberately skips, so they are appended here.
     */
    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE) + LorwynEclipsedVariantPrintings
    }

    /**
     * Play Booster: the non-foil wildcard is 18% C / 58% U / 19% R / 2% M and the traditional-foil wildcard 60.4% C /
     * 29.8% U / 7.6% R-M. The land slot is basics only. Figures from WotC's "Collecting Lorwyn Eclipsed" article, as
     * kept in taw/magic-search-engine `data/boosters`.
     */
    override val boosterStrategy: BoosterStrategy = PlayBooster(
        wildcardSlots = listOf(
            PlayBooster.wildcard(common = 0.18, uncommon = 0.58, rare = 0.19, mythic = 0.02),
            PlayBooster.wildcard(common = 0.604, uncommon = 0.298, rareOrMythic = 0.076),
        ),
    )

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.ecl.cards"
}
