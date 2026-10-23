package com.wingedsheep.mtg.sets.definitions.tmt

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.limited.BoosterStrategy
import com.wingedsheep.sdk.limited.DedicatedSlot
import com.wingedsheep.sdk.limited.DedicatedSlotBooster
import com.wingedsheep.sdk.limited.LandSlotBooster
import com.wingedsheep.sdk.limited.PlayBooster
import com.wingedsheep.sdk.limited.SheetSelector
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Teenage Mutant Ninja Turtles (2026)
 *
 * Set Code: TMT
 * Release Date: March 6, 2026
 */
object TeenageMutantNinjaTurtlesSet : MtgSet {

    override val code = "TMT"
    override val displayName = "Teenage Mutant Ninja Turtles"
    override val releaseDate = "2026-03-06"
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
     * Play Booster: 7 commons, 2 uncommons, one legendary Turtle — one of the five legendary Turtle commons (68.7%),
     * or the four main Turtles at uncommon (19.6%) or rare/mythic (11.7%, split 2:1 per card) — a non-foil wildcard
     * (25.2% C / 66% U / 8.8% R-M), a rare or mythic and a traditional-foil wildcard (61.9% C / 28.3% U / 9.8% R-M).
     * The Turtle commons and uncommons appear only in their slot; the rare and mythic Turtles also stay on the rare
     * sheet. The land slot is a common dual land 60% of the time and a basic otherwise. Figures from WotC's
     * "Collecting Teenage Mutant Ninja Turtles" article, as kept in taw/magic-search-engine `data/boosters`.
     */
    override val boosterStrategy: BoosterStrategy by lazy {
        LandSlotBooster(
            base = DedicatedSlotBooster(
                base = PlayBooster(
                    uncommons = 2,
                    wildcardSlots = listOf(
                        PlayBooster.wildcard(common = 0.252, uncommon = 0.660, rareOrMythic = 0.088),
                        PlayBooster.wildcard(common = 0.619, uncommon = 0.283, rareOrMythic = 0.098),
                    ),
                ),
                slots = listOf(
                    DedicatedSlot(
                        SheetSelector.Named(LEGENDARY_TURTLES),
                        mapOf(Rarity.COMMON to 0.687, Rarity.UNCOMMON to 0.196, Rarity.RARE to 0.078, Rarity.MYTHIC to 0.039),
                        sharedRarities = setOf(Rarity.RARE, Rarity.MYTHIC),
                    ),
                ),
            ),
            nonbasicLandNames = setOf(
                "Dimension X", "Foot Headquarters", "Illegitimate Business", "Mutant Town", "TCRI Building",
            ),
            basicLands = basicLands.filter { it.metadata.inBooster },
            nonbasicChance = 0.6,
        )
    }

    /** The legendary Turtle slot's sheet: the five legendary Turtle commons and the four main Turtles at each higher rarity. */
    private val LEGENDARY_TURTLES: Set<String> = setOf(
        "Jennika, Bad Apple Big Sister", "Leonardo, Big Brother", "Donatello, Turtle Techie",
        "Raphael, Tough Turtle", "Michelangelo, Game Master", "Leonardo, Leader in Blue",
        "Donatello, Way with Machines", "Raphael, Most Attitude", "Michelangelo, Mutant BFF",
        "Leonardo, Cutting Edge", "Donatello, Gadget Master", "Raphael, the Nightwatcher",
        "Michelangelo, Weirdness to 11", "Leonardo, Sewer Samurai", "Donatello, Mutant Mechanic",
        "Raphael, Ninja Destroyer", "Michelangelo, Improviser",
    )

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.tmt.cards"
}
