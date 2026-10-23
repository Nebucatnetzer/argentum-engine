package com.wingedsheep.mtg.sets.definitions.ptk

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.limited.BoosterStrategy
import com.wingedsheep.sdk.limited.StandardBooster
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Portal Three Kingdoms (1999)
 *
 * Set Code: PTK
 * Release Date: 1999-05-01
 */
object PortalThreeKingdomsSet : MtgSet {

    override val code = "PTK"
    override val displayName = "Portal Three Kingdoms"
    override val releaseDate = "1999-05-01"

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
     * Boosters held 10 cards: 2 basic lands (supplied at deck building), 5 commons, 2 uncommons and a rare.
     */
    override val boosterStrategy: BoosterStrategy = StandardBooster(commons = 5, uncommons = 2)

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.ptk.cards"
}
