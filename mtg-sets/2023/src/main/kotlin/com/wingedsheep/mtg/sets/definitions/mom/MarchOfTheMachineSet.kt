package com.wingedsheep.mtg.sets.definitions.mom

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.limited.BoosterStrategy
import com.wingedsheep.sdk.limited.DedicatedSlot
import com.wingedsheep.sdk.limited.DedicatedSlotBooster
import com.wingedsheep.sdk.limited.LandSlotBooster
import com.wingedsheep.sdk.limited.SheetSelector
import com.wingedsheep.sdk.limited.StandardBooster
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.model.TokenPrinting

/**
 * March of the Machine (2023)
 *
 * Set Code: MOM
 * Release Date: April 21, 2023
 */
object MarchOfTheMachineSet : MtgSet {

    override val code = "MOM"
    override val displayName = "March of the Machine"
    override val releaseDate = "2023-04-21"

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
     * Scryfall's `tmom` sync has no Incubator row — the transforming double-faced token from
     * CR 701.51 — so it is self-hosted under `web-client/public/images/tokens/`. Its back face
     * (Phyrexian) is not set-scoped art: a token keeps the image its *face* declares when it
     * transforms, so that side lives on `PredefinedTokens.Phyrexian`.
     */
    override val tokenArt: List<TokenPrinting> = listOf(
        TokenPrinting(name = "Incubator", imageUri = "/images/tokens/mom-incubator.jpeg"),
    )

    /**
     * Draft Booster: 8 single-faced commons, 2 single-faced uncommons, one transforming double-faced common or
     * uncommon, one uncommon battle and a rare or mythic, where the pack's one rare is single-faced in 98 packs in
     * 140, a battle (beside a third single-faced uncommon) in 27 and a transforming card in 15; plus a land slot
     * holding a common gain land half the time and a basic otherwise. The Multiverse Legends card (MUL, not in the
     * engine) and the traditional foil are not modelled. Figures from WotC's "Collecting March of the Machine"
     * article, as kept in taw/magic-search-engine `data/boosters`.
     */
    override val boosterStrategy: BoosterStrategy by lazy {
        LandSlotBooster(
            base = DedicatedSlotBooster(
                base = StandardBooster(commons = 8, uncommons = 2),
                slots = listOf(
                    DedicatedSlot(
                        SheetSelector.Except(SheetSelector.DoubleFaced, SheetSelector.Battle),
                        mapOf(Rarity.COMMON to 1.0, Rarity.UNCOMMON to 1.0),
                        rareChance = 15.0 / 140,
                    ),
                    DedicatedSlot(SheetSelector.Battle, mapOf(Rarity.UNCOMMON to 1.0), rareChance = 27.0 / 140),
                ),
            ),
            nonbasicLandNames = setOf(
                "Bloodfell Caves", "Blossoming Sands", "Dismal Backwater", "Jungle Hollow", "Rugged Highlands",
                "Scoured Barrens", "Swiftwater Cliffs", "Thornwood Falls", "Tranquil Cove",
                "Wind-Scarred Crag",
            ),
            basicLands = basicLands.filter { it.metadata.inBooster },
            nonbasicChance = 0.5,
        )
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.mom.cards"
}
