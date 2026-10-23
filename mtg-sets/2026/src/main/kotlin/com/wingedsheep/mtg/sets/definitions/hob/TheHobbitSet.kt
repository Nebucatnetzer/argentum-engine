package com.wingedsheep.mtg.sets.definitions.hob

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.limited.BoosterStrategy
import com.wingedsheep.sdk.limited.LandSlotBooster
import com.wingedsheep.sdk.limited.PlayBooster
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.TokenPrinting

/**
 * The Hobbit (2026)
 *
 * Set Code: HOB
 * Release Date: August 14, 2026
 * Preview inventory is sourced from Scryfall and may grow before release.
 */
object TheHobbitSet : MtgSet {
    override val code = "HOB"
    override val displayName = "The Hobbit"
    override val releaseDate = "2026-08-14"
    // Complete as of release (193/193). `sealedSupported` gates `fullyImplemented`, so leaving it
    // false would keep a finished set out of the lobby's draft/sealed picker.
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
     * The set is too new to appear in the bulk `tokens.json` sync, so every token `thob` prints is
     * hand-authored here — one row per printed illustration, `normal` full-card images like the
     * sync writes. Drop the block once `just token-art-sync` picks the same art up.
     *
     * These rows are the only place HOB token art lives: the cards themselves pass no `imageUri`,
     * which is what lets a later reprint mint its own set's token.
     */
    override val tokenArt: List<TokenPrinting> = listOf(
        // thob #1 — the Bird Soldier The Eagles Are Coming! schedules for the next upkeep.
        TokenPrinting(
            name = "Bird Soldier",
            imageUri = "https://cards.scryfall.io/normal/front/9/e/9ed2d2f3-e1b0-41e0-a29d-31624e5e9004.jpg?1785524352",
        ),
        // thob #2 — the Soldier every recruit card in the set mints.
        TokenPrinting(
            name = "Human Soldier",
            imageUri = "https://cards.scryfall.io/normal/front/6/0/6007af81-4541-4b55-90ea-03d365362ae5.jpg?1785497653",
        ),
        // thob #3 and #4 — the Army every "amass Goblins" card in the set puts its counters on
        // (Down, Down to Goblin-town, Gathering of Darkness, Rage into the Valley, Tidings of War).
        // Two illustrations, dealt out in order when a card mints more than one.
        TokenPrinting(
            name = "Goblin Army",
            imageUri = "https://cards.scryfall.io/normal/front/2/e/2e2028b1-34c0-40b6-8f65-79f79a279996.jpg?1785497644",
        ),
        TokenPrinting(
            name = "Goblin Army",
            imageUri = "https://cards.scryfall.io/normal/front/0/0/0045408d-4ab9-48bf-84aa-c6d827682090.jpg?1785497563",
        ),
        // thob #5 — the Dragon The Misty Mountains Cold pays a sacrifice for.
        TokenPrinting(
            name = "Dragon",
            imageUri = "https://cards.scryfall.io/normal/front/1/e/1e4408fa-8037-42f1-989e-2da84867f76c.jpg?1785497692",
        ),
        // thob #6 — the Dwarf of An Unexpected Party, Dwarven Shortsword, Fíli the Pathfinder and
        // The Lonely Mountain.
        TokenPrinting(
            name = "Dwarf",
            imageUri = "https://cards.scryfall.io/normal/front/9/f/9fcb3a3f-c0d4-43d4-8549-826a38bfa27d.jpg?1786258756",
        ),
        // thob #7 — Dancing from Dark to Dawn's landfall Bear.
        TokenPrinting(
            name = "Bear",
            imageUri = "https://cards.scryfall.io/normal/front/3/1/31661af9-a40a-418c-82e3-b74aa14cc7c4.jpg?1785497718",
        ),
        // thob #8 — the Elf of Down in the Valley and Thranduil, Sindarin Liege.
        TokenPrinting(
            name = "Elf",
            imageUri = "https://cards.scryfall.io/normal/front/7/6/761c7c31-c6c5-44e2-a845-f590542b6eda.jpg?1785497812",
        ),
        // thob #9 — the Wolf of Chief Warg's Company and Head of the Hunt.
        TokenPrinting(
            name = "Wolf",
            imageUri = "https://cards.scryfall.io/normal/front/e/0/e07312b9-f3c1-4e36-88fc-b29cde581eb6.jpg?1785497932",
        ),
        // thob #10 — the Equipment Dáin Ironfoot and Iron Hills Blacksmith mint from
        // PredefinedTokens.Axe.
        TokenPrinting(
            name = "Axe",
            imageUri = "https://cards.scryfall.io/normal/front/6/f/6f7a3999-e341-43bb-9b8f-6c1a05b98906.jpg?1785497989",
        ),
        // thob #11 — Stone-Giant of High Pass's named Wall token.
        TokenPrinting(
            name = "Stone Boulder",
            imageUri = "https://cards.scryfall.io/normal/front/3/4/3440e247-a733-4609-9d80-d4a5fc58ae46.jpg?1785502811",
        ),
        // thob #12 and #13 — the Treasure of Smaug the Magnificent, Dori, Bearer of Friends,
        // Long-Bodied Grey Dog and the rest of the set's Treasure makers, in two illustrations.
        TokenPrinting(
            name = "Treasure",
            imageUri = "https://cards.scryfall.io/normal/front/c/6/c6e096bb-ad9e-4a8b-8b42-26852fa32c1d.jpg?1783902770",
        ),
        TokenPrinting(
            name = "Treasure",
            imageUri = "https://cards.scryfall.io/normal/front/d/1/d1892b78-7663-4cbd-a732-9a4b0b18d4c8.jpg?1785498054",
        ),
    )

    /**
     * Play Booster: the non-foil wildcard is 75% C / 4.1% U / 20.9% R-M (WotC's published figures) and the
     * traditional-foil wildcard 60.5% C / 31% U / 8.7% R-M. The land slot is a common dual land 60% of the time and a
     * basic otherwise. Figures from WotC's "Collecting The Hobbit" article, as kept in taw/magic-search-engine
     * `data/boosters`.
     */
    override val boosterStrategy: BoosterStrategy by lazy {
        LandSlotBooster(
            base = PlayBooster(
                wildcardSlots = listOf(
                    PlayBooster.wildcard(common = 0.75, uncommon = 0.041, rareOrMythic = 0.209),
                    PlayBooster.wildcard(common = 0.6047, uncommon = 0.3099, rareOrMythic = 0.0865),
                ),
            ),
            nonbasicLandNames = setOf(
                "Elvenking's Halls", "Goblin-town", "Iron Hills", "Lake-town", "Mirkwood",
            ),
            basicLands = basicLands.filter { it.metadata.inBooster },
            nonbasicChance = 0.6,
        )
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.hob.cards"
}
