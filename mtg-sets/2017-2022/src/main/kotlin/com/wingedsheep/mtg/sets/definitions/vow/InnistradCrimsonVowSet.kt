package com.wingedsheep.mtg.sets.definitions.vow

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
 * Innistrad: Crimson Vow (2021)
 *
 * Set Code: VOW
 * Release Date: November 19, 2021
 */
object InnistradCrimsonVowSet : MtgSet {

    override val code = "VOW"
    override val displayName = "Innistrad: Crimson Vow"
    override val releaseDate = "2021-11-19"

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    override val basicLands: List<CardDefinition> by lazy {
        CardDiscovery.findBasicLandsIn(CARDS_PACKAGE, code)
    }

    /**
     * Draft Booster, collated like Midnight Hunt's: 1 double-faced common, 9 commons, 2 uncommons, a double-faced
     * uncommon and a single-faced rare or mythic — the double-faced card is the rare (beside a third single-faced
     * uncommon) in 1 pack in 6 — plus a basic land (supplied at deck building). The traditional foil replacing a
     * common in a third of packs is cosmetic and not modelled.
     */
    override val boosterStrategy: BoosterStrategy = DedicatedSlotBooster(
        base = StandardBooster(commons = 9, uncommons = 2),
        slots = listOf(
            DedicatedSlot(SheetSelector.DoubleFaced, mapOf(Rarity.COMMON to 1.0)),
            DedicatedSlot(SheetSelector.DoubleFaced, mapOf(Rarity.UNCOMMON to 1.0), rareChance = 1.0 / 6),
        ),
    )

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.vow.cards"
}
