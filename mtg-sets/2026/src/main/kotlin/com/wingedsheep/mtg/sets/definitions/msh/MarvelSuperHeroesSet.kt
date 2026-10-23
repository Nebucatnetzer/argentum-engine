package com.wingedsheep.mtg.sets.definitions.msh

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.limited.BoosterStrategy
import com.wingedsheep.sdk.limited.LandSlotBooster
import com.wingedsheep.sdk.limited.PlayBooster
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Marvel Super Heroes (2026)
 *
 * Set Code: MSH
 * Release Date: June 26, 2026
 */
object MarvelSuperHeroesSet : MtgSet {

    override val code = "MSH"
    override val displayName = "Marvel Super Heroes"
    override val releaseDate = "2026-06-26"

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
     * Play Booster: the non-foil wildcard is 13% C / 66.7% U / 17.2% R / 2.1% M and the traditional-foil wildcard
     * 60.8% C / 30.8% U / 7.5% R-M. The Source Material card (MAR) stays a common. The land slot is a common dual land
     * half the time and a basic otherwise. Figures from WotC's "Collecting Marvel Super Heroes" article, as kept in
     * taw/magic-search-engine `data/boosters`.
     */
    override val boosterStrategy: BoosterStrategy by lazy {
        LandSlotBooster(
            base = PlayBooster(
                wildcardSlots = listOf(
                    PlayBooster.wildcard(common = 0.13, uncommon = 0.667, rare = 0.172, mythic = 0.021),
                    PlayBooster.wildcard(common = 0.608, uncommon = 0.308, rareOrMythic = 0.075),
                ),
            ),
            nonbasicLandNames = setOf(
                "A.I.M. Labs", "Asgardian Citadel", "Avengers Hangar", "Birnin Zana Plaza", "Fisk Tower",
                "Hell's Kitchen", "Los Diablos Missile Base", "Pym Technologies", "Stark Industries",
                "Subterranean Cavern",
            ),
            basicLands = basicLands.filter { it.metadata.inBooster },
            nonbasicChance = 0.5,
        )
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.msh.cards"
}
