package com.wingedsheep.mtg.sets.definitions.fem

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.limited.BoosterStrategy
import com.wingedsheep.sdk.limited.PrintSheet
import com.wingedsheep.sdk.limited.PrintSheetBooster
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.TokenPrinting

/**
 * Fallen Empires (1994)
 *
 * Complete: all 102 cards. Holds the canonical [CardDefinition]s of cards whose earliest
 * real-expansion printing is Fallen Empires, with later sets contributing reprint [Printing] rows.
 *
 * Set Code: FEM
 * Release Date: November 1, 1994
 */
object FallenEmpiresSet : MtgSet {

    override val code = "FEM"
    override val displayName = "Fallen Empires"
    override val releaseDate = "1994-11-01"

    /**
     * Fallen Empires printed no basic lands of its own and belongs to no block, so its limited
     * environment borrows Portal's — the same fallback The Dark takes.
     */
    override val basicLandsFallbackCode = "POR"

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
     * Fallen Empires predates token *cards* — there is no `tfem` set on Scryfall to sync from — so
     * the four tokens the set mints borrow the closest matching art from sets that did print one.
     *
     * The fifth, the 1/1 blue Camarid from Homarid Spawning Bed, has never been printed as a token
     * card at all and has no art anywhere on Scryfall, so it has no row here: it resolves through
     * the engine-wide `TokenArt` table instead, which points it at this set's own Homarid.
     */
    override val tokenArt: List<TokenPrinting> = listOf(
        TokenPrinting(
            name = "Citizen",
            imageUri = "https://cards.scryfall.io/normal/front/1/6/165164e7-5693-4d65-b789-8ed8a222365b.jpg?1783933883",
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
        ),
        TokenPrinting(
            name = "Thrull",
            imageUri = "https://cards.scryfall.io/normal/front/2/7/2795320c-57fc-4140-b306-a92237390353.jpg?1783942443",
            power = 0,
            toughness = 1,
            colors = setOf(Color.BLACK),
        ),
        TokenPrinting(
            name = "Saproling",
            imageUri = "https://cards.scryfall.io/normal/front/6/9/698d48fa-d6c1-44b9-8aa2-bb03e0e53788.jpg?1783943085",
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
        ),
        TokenPrinting(
            name = "Goblin",
            imageUri = "https://cards.scryfall.io/normal/front/e/9/e9577d3c-ee19-4b53-adac-b304287a066f.jpg?1783943086",
            power = 1,
            toughness = 1,
            colors = setOf(Color.RED),
        ),
    )

    /**
     * Fallen Empires boosters held 8 cards: 6 from the common print sheet and 2 from the uncommon sheet, each card as
     * often as it was printed on its sheet — the multi-art commons sum their arts (Hymn to Tourach's four C1 arts make
     * it C4); Scryfall's rares are the U1 cards. Counts are from WotC's checklist as kept in taw/magic-search-engine
     * `data/print_sheets`.
     */
    override val boosterStrategy: BoosterStrategy by lazy {
        PrintSheetBooster(listOf(PrintSheet(slots = 6, COMMON_SHEET), PrintSheet(slots = 2, UNCOMMON_SHEET)))
    }

    private val COMMON_SHEET: Map<String, Int> = mapOf(
        "Armor Thrull" to 4,
        "Basal Thrull" to 4,
        "Brassclaw Orcs" to 4,
        "Combat Medic" to 4,
        "Delif's Cone" to 1,
        "Dwarven Soldier" to 3,
        "Elven Fortress" to 4,
        "Elvish Hunter" to 3,
        "Elvish Scout" to 3,
        "Farrel's Zealot" to 3,
        "Goblin Chirurgeon" to 3,
        "Goblin Grenade" to 3,
        "Goblin War Drums" to 4,
        "High Tide" to 3,
        "Homarid" to 4,
        "Homarid Warrior" to 3,
        "Hymn to Tourach" to 4,
        "Icatian Infantry" to 4,
        "Icatian Javelineers" to 3,
        "Icatian Moneychanger" to 3,
        "Icatian Scout" to 4,
        "Initiates of the Ebon Hand" to 3,
        "Merseine" to 4,
        "Mindstab Thrull" to 3,
        "Necrite" to 3,
        "Night Soil" to 3,
        "Orcish Spy" to 3,
        "Orcish Veteran" to 4,
        "Order of Leitbur" to 3,
        "Order of the Ebon Hand" to 3,
        "Spore Cloud" to 3,
        "Thallid" to 4,
        "Thorn Thallid" to 4,
        "Tidal Flats" to 3,
        "Vodalian Mage" to 3,
        "Vodalian Soldiers" to 4,
    )

    private val UNCOMMON_SHEET: Map<String, Int> = mapOf(
        "Aeolipile" to 1,
        "Balm of Restoration" to 1,
        "Bottomless Vault" to 1,
        "Breeding Pit" to 3,
        "Conch Horn" to 1,
        "Deep Spawn" to 3,
        "Delif's Cube" to 1,
        "Derelor" to 1,
        "Draconian Cylix" to 1,
        "Dwarven Armorer" to 1,
        "Dwarven Catapult" to 3,
        "Dwarven Hold" to 1,
        "Dwarven Lieutenant" to 3,
        "Dwarven Ruins" to 2,
        "Ebon Praetor" to 1,
        "Ebon Stronghold" to 2,
        "Elven Lyre" to 1,
        "Elvish Farmer" to 1,
        "Farrel's Mantle" to 3,
        "Farrelite Priest" to 3,
        "Feral Thallid" to 3,
        "Fungal Bloom" to 1,
        "Goblin Flotilla" to 1,
        "Goblin Kites" to 3,
        "Goblin Warrens" to 1,
        "Hand of Justice" to 1,
        "Havenwood Battleground" to 2,
        "Heroism" to 3,
        "Hollow Trees" to 1,
        "Homarid Shaman" to 1,
        "Homarid Spawning Bed" to 3,
        "Icatian Lieutenant" to 1,
        "Icatian Phalanx" to 3,
        "Icatian Priest" to 3,
        "Icatian Skirmishers" to 1,
        "Icatian Store" to 1,
        "Icatian Town" to 1,
        "Implements of Sacrifice" to 1,
        "Orcish Captain" to 3,
        "Orgg" to 1,
        "Raiding Party" to 3,
        "Rainbow Vale" to 1,
        "Ring of Renewal" to 1,
        "River Merfolk" to 1,
        "Ruins of Trokair" to 2,
        "Sand Silos" to 1,
        "Seasinger" to 3,
        "Soul Exchange" to 3,
        "Spirit Shield" to 1,
        "Spore Flower" to 3,
        "Svyelunite Priest" to 3,
        "Svyelunite Temple" to 2,
        "Thallid Devourer" to 3,
        "Thelon's Chant" to 3,
        "Thelon's Curse" to 1,
        "Thelonite Druid" to 3,
        "Thelonite Monk" to 1,
        "Thrull Champion" to 1,
        "Thrull Retainer" to 3,
        "Thrull Wizard" to 3,
        "Tidal Influence" to 3,
        "Tourach's Chant" to 3,
        "Tourach's Gate" to 1,
        "Vodalian Knights" to 1,
        "Vodalian War Machine" to 1,
        "Zelyon Sword" to 1,
    )

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.fem.cards"
}
