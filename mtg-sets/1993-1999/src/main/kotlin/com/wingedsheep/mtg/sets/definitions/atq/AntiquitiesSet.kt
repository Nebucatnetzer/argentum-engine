package com.wingedsheep.mtg.sets.definitions.atq

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.limited.BoosterStrategy
import com.wingedsheep.sdk.limited.PrintSheet
import com.wingedsheep.sdk.limited.PrintSheetBooster
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Antiquities (1994)
 *
 * Set Code: ATQ
 * Release Date: 1994-03-04
 *
 * Scaffolded as the canonical home for cards reprinted in later sets (e.g. Eighth
 * Edition), and since filled in: all 84 buildable cards are implemented, so the set is
 * draftable (not `incomplete`). The 85th, Bronze Tablet, needs the ante zone the engine
 * doesn't have and is listed in `coverage/card-exclusions.json` as never-to-implement.
 */
object AntiquitiesSet : MtgSet {

    override val code = "ATQ"
    override val displayName = "Antiquities"
    override val releaseDate = "1994-03-04"
    override val basicLandsFallbackCode = "POR"

    override val cards: List<CardDefinition> by lazy {
        // Self-stamp [code] onto every card, honouring the MtgSet contract that `cards` is already
        // set-stamped. This makes `CardDefinition.setCode` a reliable "originally printed in ATQ"
        // signal for every consumer (engine tests included), not only the game-server load path —
        // which is what Golgothian Sylex's OriginallyPrintedInSet("ATQ") predicate reads.
        CardDiscovery.findIn(CARDS_PACKAGE).map { if (it.setCode == null) it.copy(setCode = code) else it }
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    /**
     * Antiquities boosters held 8 cards: 6 from the common print sheet and 2 from the uncommon sheet, each card as
     * often as it was printed on its sheet — Scryfall's rares are the U1 cards; Mishra's Factory and Strip Mine have
     * arts on both sheets. Counts are from WotC's checklist as kept in taw/magic-search-engine `data/print_sheets`.
     */
    override val boosterStrategy: BoosterStrategy by lazy {
        PrintSheetBooster(listOf(PrintSheet(slots = 6, COMMON_SHEET), PrintSheet(slots = 2, UNCOMMON_SHEET)))
    }

    private val COMMON_SHEET: Map<String, Int> = mapOf(
        "Amulet of Kroog" to 4,
        "Argivian Blacksmith" to 4,
        "Argothian Pixies" to 4,
        "Argothian Treefolk" to 4,
        "Artifact Blast" to 4,
        "Artifact Possession" to 4,
        "Artifact Ward" to 4,
        "Atog" to 4,
        "Battering Ram" to 4,
        "Clay Statue" to 4,
        "Crumble" to 4,
        "Cursed Rack" to 1,
        "Drafna's Restoration" to 4,
        "Dragon Engine" to 4,
        "Feldon's Cane" to 1,
        "Grapeshot Catapult" to 4,
        "Mishra's Factory" to 1,
        "Orcish Mechanics" to 4,
        "Ornithopter" to 4,
        "Phyrexian Gremlins" to 4,
        "Priest of Yawgmoth" to 4,
        "Reconstruction" to 4,
        "Reverse Polarity" to 4,
        "Sage of Lat-Nam" to 4,
        "Staff of Zegon" to 4,
        "Strip Mine" to 1,
        "Tablet of Epityr" to 4,
        "Urza's Chalice" to 4,
        "Urza's Mine" to 6,
        "Urza's Power Plant" to 6,
        "Urza's Tower" to 5,
        "Yotian Soldier" to 4,
    )

    private val UNCOMMON_SHEET: Map<String, Int> = mapOf(
        "Argivian Archaeologist" to 1,
        "Armageddon Clock" to 2,
        "Ashnod's Altar" to 2,
        "Ashnod's Battle Gear" to 2,
        "Ashnod's Transmogrant" to 3,
        "Bronze Tablet" to 1,
        "Candelabra of Tawnos" to 1,
        "Circle of Protection: Artifacts" to 3,
        "Citanul Druid" to 3,
        "Clockwork Avian" to 1,
        "Colossus of Sardia" to 1,
        "Coral Helm" to 1,
        "Damping Field" to 3,
        "Detonate" to 3,
        "Dwarven Weaponsmith" to 3,
        "Energy Flux" to 3,
        "Gaea's Avenger" to 1,
        "Gate to Phyrexia" to 3,
        "Goblin Artisans" to 3,
        "Golgothian Sylex" to 1,
        "Haunting Wind" to 3,
        "Hurkyl's Recall" to 1,
        "Ivory Tower" to 3,
        "Jalum Tome" to 2,
        "Martyrs of Korlis" to 3,
        "Mightstone" to 3,
        "Millstone" to 3,
        "Mishra's Factory" to 3,
        "Mishra's War Machine" to 1,
        "Mishra's Workshop" to 1,
        "Obelisk of Undoing" to 1,
        "Onulet" to 3,
        "Power Artifact" to 3,
        "Powerleech" to 3,
        "Primal Clay" to 3,
        "Rakalite" to 3,
        "Rocket Launcher" to 3,
        "Shapeshifter" to 1,
        "Shatterstorm" to 1,
        "Strip Mine" to 3,
        "Su-Chi" to 3,
        "Tawnos's Coffin" to 1,
        "Tawnos's Wand" to 3,
        "Tawnos's Weaponry" to 3,
        "Tetravus" to 1,
        "The Rack" to 3,
        "Titania's Song" to 3,
        "Transmute Artifact" to 3,
        "Triskelion" to 1,
        "Urza's Avenger" to 1,
        "Urza's Miter" to 1,
        "Wall of Spears" to 3,
        "Weakstone" to 3,
        "Xenic Poltergeist" to 3,
        "Yawgmoth Demon" to 1,
    )

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.atq.cards"
}
