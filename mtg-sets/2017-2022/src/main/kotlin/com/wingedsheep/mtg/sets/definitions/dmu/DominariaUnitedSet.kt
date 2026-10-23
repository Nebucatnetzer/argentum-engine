package com.wingedsheep.mtg.sets.definitions.dmu

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
 * Dominaria United (2022)
 *
 * Set Code: DMU
 * Release Date: September 9, 2022
 */
object DominariaUnitedSet : MtgSet {

    override val code = "DMU"
    override val displayName = "Dominaria United"
    override val releaseDate = "2022-09-09"

    /**
     * Draft Booster: ten commons, one legendary creature, and either a legendary rare/mythic beside
     * three nonlegendary uncommons (3 packs in 12) or a legendary uncommon beside two nonlegendary
     * uncommons and a nonlegendary rare/mythic (9 in 12). The basic land and the traditional-foil
     * replacement of a common are omitted; the generator supplies basics at deck building.
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

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.dmu.cards"
}
