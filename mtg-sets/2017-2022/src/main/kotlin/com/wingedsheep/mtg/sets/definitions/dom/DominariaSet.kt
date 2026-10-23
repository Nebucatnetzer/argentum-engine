package com.wingedsheep.mtg.sets.definitions.dom

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.limited.BoosterStrategy
import com.wingedsheep.sdk.limited.DedicatedSlot
import com.wingedsheep.sdk.limited.DedicatedSlotBooster
import com.wingedsheep.sdk.limited.SheetSelector
import com.wingedsheep.sdk.limited.StandardBooster
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Dominaria Set (2018)
 *
 * Dominaria was a standalone expansion set that returned to Magic's home plane,
 * featuring historic and legendary themes.
 *
 * Set Code: DOM
 * Release Date: April 27, 2018
 * Card Count: 280
 */
object DominariaSet : MtgSet {

    override val code = "DOM"
    override val displayName = "Dominaria"
    override val releaseDate = "2018-04-27"
    override val sealedSupported = true

    /**
     * Draft Booster: ten commons and one guaranteed legendary creature — a legendary rare or mythic in a quarter of
     * packs (beside three nonlegendary uncommons), otherwise a legendary uncommon beside two nonlegendary uncommons
     * and a nonlegendary rare (the documented 1-in-4 rate, per taw/magic-search-engine `dom/draft.yaml`). The basic
     * land and the traditional-foil replacement of a common are omitted; basics are supplied at deck building.
     */
    override val boosterStrategy: BoosterStrategy = DedicatedSlotBooster(
        base = StandardBooster(commons = 10, uncommons = 2),
        slots = listOf(DedicatedSlot(SheetSelector.LegendaryCreature, mapOf(Rarity.UNCOMMON to 1.0), rareChance = 0.25)),
    )

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val basicLands: List<CardDefinition> by lazy {
        CardDiscovery.findBasicLandsIn(CARDS_PACKAGE, code)
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.dom.cards"
}
