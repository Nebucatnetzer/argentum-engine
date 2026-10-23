package com.wingedsheep.mtg.sets.definitions.neo

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
 * Kamigawa: Neon Dynasty
 *
 * Set Code: NEO
 */
object NeonDynastySet : MtgSet {

    override val code = "NEO"
    override val displayName = "Kamigawa: Neon Dynasty"
    override val releaseDate = "2022-02-18"

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
     * Draft Booster: 1 rare or mythic, 1 double-faced common or uncommon, 3 uncommons, 9 commons and a
     * basic land (supplied at deck building), per the set's product overview. WotC doesn't publish the
     * double-faced slot's common/uncommon split; 2:1 is the per-card sheet frequency estimate (10 of 101
     * commons vs 3 of 80 uncommons per pack).
     */
    override val boosterStrategy: BoosterStrategy = DedicatedSlotBooster(
        base = StandardBooster(commons = 9, uncommons = 3),
        slots = listOf(DedicatedSlot(SheetSelector.DoubleFaced, mapOf(Rarity.COMMON to 2.0, Rarity.UNCOMMON to 1.0))),
    )

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.neo.cards"
}
