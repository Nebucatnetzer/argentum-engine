package com.wingedsheep.mtg.sets.definitions.inr

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.limited.BoosterStrategy
import com.wingedsheep.sdk.limited.DedicatedSlot
import com.wingedsheep.sdk.limited.DedicatedSlotBooster
import com.wingedsheep.sdk.limited.PlayBooster
import com.wingedsheep.sdk.limited.SheetSelector
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Innistrad Remastered (2025)
 *
 * Set Code: INR
 * Release Date: January 24, 2025
 */
object InnistradRemasteredSet : MtgSet {

    override val code = "INR"
    override val displayName = "Innistrad Remastered"
    override val releaseDate = "2025-01-24"
    // Complete as of Invasion of Innistrad (290/290) — the flag is what gates `fullyImplemented`,
    // so leaving it set would keep a finished set out of the lobby's set picker.

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
     * Play Booster: 5 single-faced commons, 1 double-faced common, 3 uncommons, a non-foil wildcard (70% C / 17.5% U /
     * 12.5% R-M), a rare or mythic, a retro-frame card of any rarity (weighted 8:4:2:1 per card over the 40 C / 76 U /
     * 25 R / 17 M retro printings) and a traditional-foil wildcard (60% C / 25% U / 15% R-M). The retro slot opens the
     * rarity, not the retro subset; the basic land is not modelled. Figures from WotC's "Collecting Innistrad
     * Remastered" article, as kept in taw/magic-search-engine `data/boosters`.
     */
    override val boosterStrategy: BoosterStrategy = DedicatedSlotBooster(
        base = PlayBooster(
            commons = 5,
            wildcardSlots = listOf(
                PlayBooster.wildcard(common = 0.700, uncommon = 0.175, rareOrMythic = 0.125),
                PlayBooster.wildcard(common = 320.0, uncommon = 304.0, rare = 50.0, mythic = 17.0),
                PlayBooster.wildcard(common = 0.60, uncommon = 0.25, rareOrMythic = 0.15),
            ),
        ),
        slots = listOf(DedicatedSlot(SheetSelector.DoubleFaced, mapOf(Rarity.COMMON to 1.0))),
    )

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.inr.cards"
}
