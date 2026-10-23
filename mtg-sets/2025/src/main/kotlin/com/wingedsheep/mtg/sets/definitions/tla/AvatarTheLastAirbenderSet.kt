package com.wingedsheep.mtg.sets.definitions.tla

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.limited.BoosterStrategy
import com.wingedsheep.sdk.limited.LandSlotBooster
import com.wingedsheep.sdk.limited.PlayBooster
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Avatar: The Last Airbender (2025)
 *
 * Set Code: TLA
 * Release Date: November 21, 2025
 */
object AvatarTheLastAirbenderSet : MtgSet {

    override val code = "TLA"
    override val displayName = "Avatar: The Last Airbender"
    override val releaseDate = "2025-11-21"

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
     * Play Booster: the non-foil wildcard is 4.2% C / 74.1% U / 16.7% R / 2.6% M and the traditional-foil wildcard
     * 53.9% C / 36.7% U / 6.7% R / 1.2% M. The Source Material card (TLE) stays a common. The land slot is a common
     * dual land half the time and a basic otherwise. Figures from WotC's "Collecting Avatar: The Last Airbender"
     * article, as kept in taw/magic-search-engine `data/boosters`.
     */
    override val boosterStrategy: BoosterStrategy by lazy {
        LandSlotBooster(
            base = PlayBooster(
                wildcardSlots = listOf(
                    PlayBooster.wildcard(common = 0.042, uncommon = 0.741, rare = 0.167, mythic = 0.026),
                    PlayBooster.wildcard(common = 0.539, uncommon = 0.367, rare = 0.067, mythic = 0.012),
                ),
            ),
            nonbasicLandNames = setOf(
                "Airship Engine Room", "Boiling Rock Prison", "Foggy Bottom Swamp", "Kyoshi Village",
                "Meditation Pools", "Misty Palms Oasis", "North Pole Gates", "Omashu City", "Serpent's Pass",
                "Sun-Blessed Peak",
            ),
            basicLands = basicLands.filter { it.metadata.inBooster },
            nonbasicChance = 0.5,
        )
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.tla.cards"
}
