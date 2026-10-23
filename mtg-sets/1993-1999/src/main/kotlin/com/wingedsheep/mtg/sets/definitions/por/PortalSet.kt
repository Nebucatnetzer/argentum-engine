package com.wingedsheep.mtg.sets.definitions.por

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.limited.BoosterStrategy
import com.wingedsheep.sdk.limited.StandardBooster
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Portal Set (1997)
 *
 * Portal was an introductory set designed to teach new players
 * the basics of Magic: The Gathering with simplified rules.
 *
 * Set Code: POR
 * Release Date: June 1997
 * Card Count: 222
 */
object PortalSet : MtgSet {

    override val code = "POR"
    override val displayName = "Portal"
    override val releaseDate = "1997-05-01"
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
     * Boosters held 15 cards: 2 basic lands (supplied at deck building), 9 commons, 3 uncommons and a rare.
     */
    override val boosterStrategy: BoosterStrategy = StandardBooster(commons = 9)

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.por.cards"
}
