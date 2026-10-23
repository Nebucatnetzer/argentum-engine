package com.wingedsheep.mtg.sets.definitions.fdn

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.limited.BoosterStrategy
import com.wingedsheep.sdk.limited.LandSlotBooster
import com.wingedsheep.sdk.limited.PlayBooster
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.TokenPrinting

/**
 * Foundations (2024)
 *
 * Set Code: FDN
 * Release Date: November 15, 2024
 *
 * Foundations is a reprint-focused core-style set, used here primarily as a home
 * for Modern-legal staples referenced by MageZero training decks (see
 * backlog/magezero-coverage.md).
 *
 * All 262 booster cards are implemented, so the set is draftable (not `incomplete`). The remaining
 * FDN cards are non-booster exclusives — Beginner Box, Starter Collection, and the 2026 set
 * extension — which the coverage view counts separately from the headline draft pool.
 */
object FoundationsSet : MtgSet {

    override val code = "FDN"
    override val displayName = "Foundations"
    override val releaseDate = "2024-11-15"

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
     * Foundations' own token printings (Scryfall set `tfdn`), so a token minted by an FDN card
     * shows FDN art rather than the engine-wide generic art for its creature type.
     */
    override val tokenArt: List<TokenPrinting> = listOf(
        // tfdn #1 — Arahbo, the First Fang's 1/1 white Cat (art by Leonardo Santanna).
        TokenPrinting(
            name = "Cat",
            imageUri = "https://cards.scryfall.io/normal/front/2/8/2885d54c-9fb2-4f01-8937-54f8ac1ce5bc.jpg?1783908593",
        ),
        // Release the Dogs is reprinted here, and the joke only lands if the four Dogs look like
        // four different dogs — so the FDN printing borrows Jumpstart's four Dog arts rather than
        // repeating the single `tfdn` Dog four times. These rows are hand-authored, so they win
        // over the synced one. See JumpstartSet.tokenArt.
        TokenPrinting(
            name = "Dog",
            imageUri = "/images/tokens/jmp-dog1.jpeg",
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
        ),
        TokenPrinting(
            name = "Dog",
            imageUri = "/images/tokens/jmp-dog2.jpeg",
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
        ),
        TokenPrinting(
            name = "Dog",
            imageUri = "/images/tokens/jmp-dog3.jpeg",
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
        ),
        TokenPrinting(
            name = "Dog",
            imageUri = "/images/tokens/jmp-dog4.jpeg",
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
        ),
    )

    /**
     * Play Booster: the non-foil wildcard is 18.5% C / 60.7% U / 20.8% R-M and the traditional-foil wildcard 59.9% C /
     * 31.4% U / 8.5% R-M. The land slot is a common gain land half the time and a basic otherwise. Figures from WotC's
     * "Collecting Foundations" article, as kept in taw/magic-search-engine `data/boosters`.
     */
    override val boosterStrategy: BoosterStrategy by lazy {
        LandSlotBooster(
            base = PlayBooster(
                wildcardSlots = listOf(
                    PlayBooster.wildcard(common = 0.185, uncommon = 0.607, rareOrMythic = 0.208),
                    PlayBooster.wildcard(common = 0.599, uncommon = 0.314, rareOrMythic = 0.085),
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

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.fdn.cards"
}
