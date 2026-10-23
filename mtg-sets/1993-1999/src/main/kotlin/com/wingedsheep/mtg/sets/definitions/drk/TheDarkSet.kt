package com.wingedsheep.mtg.sets.definitions.drk

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.limited.BoosterStrategy
import com.wingedsheep.sdk.limited.PrintSheet
import com.wingedsheep.sdk.limited.PrintSheetBooster
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * The Dark (1994)
 *
 * Set Code: DRK
 * Release Date: August 1, 1994
 *
 * Scaffolded to hold the canonical [CardDefinition]s of cards whose earliest real-expansion
 * printing is The Dark (e.g. Bog Imp), with later sets contributing reprint [Printing] rows.
 * Every card in the set is one of those canonicals — The Dark introduced no reprints — so
 * [printings] is empty, and the set has no basic lands of its own (hence [basicLandsFallbackCode]).
 *
 * Since filled in: all 119 cards are implemented and field-verified against Scryfall, so the set
 * is draftable (not `incomplete`).
 */
object TheDarkSet : MtgSet {

    override val code = "DRK"
    override val displayName = "The Dark"
    override val releaseDate = "1994-08-01"
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
     * The Dark boosters held 8 cards: 6 from the common print sheet and 2 from the uncommon sheet,
     * each card as often as it was printed on its sheet — Scryfall's rares are the U1 cards.
     * Counts are from WotC's checklist as kept in taw/magic-search-engine `data/print_sheets`.
     */
    override val boosterStrategy: BoosterStrategy by lazy {
        PrintSheetBooster(listOf(PrintSheet(slots = 6, COMMON_SHEET), PrintSheet(slots = 2, UNCOMMON_SHEET)))
    }

    private val COMMON_SHEET: Map<String, Int> = mapOf(
        "Ashes to Ashes" to 3,
        "Bog Imp" to 3,
        "Bog Rats" to 3,
        "Brainwash" to 3,
        "Carnivorous Plant" to 3,
        "Dark Heart of the Wood" to 3,
        "Deep Water" to 3,
        "Drowned" to 3,
        "Dust to Dust" to 3,
        "Erosion" to 3,
        "Festival" to 3,
        "Fissure" to 3,
        "Gaea's Touch" to 3,
        "Ghost Ship" to 3,
        "Giant Shark" to 3,
        "Goblin Caves" to 3,
        "Goblin Digging Team" to 3,
        "Goblin Hero" to 3,
        "Goblin Rock Sled" to 3,
        "Goblin Shrine" to 3,
        "Goblins of the Flarg" to 3,
        "Holy Light" to 3,
        "Inquisition" to 3,
        "Land Leeches" to 3,
        "Marsh Gas" to 3,
        "Marsh Goblins" to 3,
        "Marsh Viper" to 3,
        "Maze of Ith" to 1,
        "Miracle Worker" to 3,
        "Morale" to 3,
        "Murk Dwellers" to 3,
        "Pikemen" to 3,
        "Riptide" to 3,
        "Savaen Elves" to 3,
        "Scarwood Goblins" to 3,
        "Scavenger Folk" to 3,
        "Squire" to 3,
        "Sunken City" to 3,
        "Venom" to 3,
        "Water Wurm" to 3,
        "Word of Binding" to 3,
    )

    private val UNCOMMON_SHEET: Map<String, Int> = mapOf(
        "Amnesia" to 2,
        "Angry Mob" to 2,
        "Apprentice Wizard" to 1,
        "Ball Lightning" to 1,
        "Banshee" to 2,
        "Barl's Cage" to 1,
        "Blood Moon" to 1,
        "Blood of the Martyr" to 2,
        "Bone Flute" to 2,
        "Book of Rass" to 2,
        "Brothers of Fire" to 2,
        "Cave People" to 2,
        "City of Shadows" to 1,
        "Cleansing" to 1,
        "Coal Golem" to 2,
        "Curse Artifact" to 2,
        "Dance of Many" to 1,
        "Dark Sphere" to 2,
        "Diabolic Machine" to 2,
        "Eater of the Dead" to 2,
        "Electric Eel" to 2,
        "Elves of Deep Shadow" to 2,
        "Eternal Flame" to 1,
        "Exorcist" to 1,
        "Fasting" to 2,
        "Fellwar Stone" to 2,
        "Fire Drake" to 2,
        "Fire and Brimstone" to 2,
        "Flood" to 2,
        "Fountain of Youth" to 2,
        "Frankenstein's Monster" to 1,
        "Goblin Wizard" to 1,
        "Grave Robbers" to 1,
        "Hidden Path" to 1,
        "Inferno" to 1,
        "Knights of Thorn" to 1,
        "Leviathan" to 1,
        "Living Armor" to 2,
        "Lurker" to 1,
        "Mana Clash" to 1,
        "Mana Vortex" to 1,
        "Martyr's Cry" to 1,
        "Merfolk Assassin" to 2,
        "Mind Bomb" to 1,
        "Nameless Race" to 1,
        "Necropolis" to 2,
        "Niall Silvain" to 1,
        "Orc General" to 2,
        "People of the Woods" to 2,
        "Preacher" to 1,
        "Psychic Allergy" to 1,
        "Rag Man" to 1,
        "Reflecting Mirror" to 2,
        "Runesword" to 2,
        "Safe Haven" to 1,
        "Scarecrow" to 2,
        "Scarwood Bandits" to 1,
        "Scarwood Hag" to 2,
        "Season of the Witch" to 1,
        "Sisters of the Flame" to 2,
        "Skull of Orm" to 2,
        "Sorrow's Path" to 1,
        "Spitting Slug" to 2,
        "Standing Stones" to 2,
        "Stone Calendar" to 1,
        "Tangle Kelp" to 2,
        "The Fallen" to 2,
        "Tivadar's Crusade" to 2,
        "Tormod's Crypt" to 2,
        "Tower of Coireall" to 2,
        "Tracker" to 1,
        "Uncle Istvan" to 2,
        "Wand of Ith" to 2,
        "War Barge" to 2,
        "Whippoorwill" to 2,
        "Witch Hunter" to 1,
        "Worms of the Earth" to 1,
        "Wormwood Treefolk" to 1,
    )

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.drk.cards"
}
