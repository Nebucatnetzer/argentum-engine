package com.wingedsheep.mtg.sets.definitions.mh3

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.limited.BoosterStrategy
import com.wingedsheep.sdk.limited.DedicatedSlot
import com.wingedsheep.sdk.limited.DedicatedSlotBooster
import com.wingedsheep.sdk.limited.PlayBooster
import com.wingedsheep.sdk.limited.SheetSelector
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.model.TokenPrinting

/**
 * Modern Horizons 3
 *
 * Set Code: MH3
 */
object ModernHorizons3Set : MtgSet {

    override val code = "MH3"
    override val displayName = "Modern Horizons 3"
    override val releaseDate = "2024-06-14"

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val basicLands: List<CardDefinition> by lazy {
        CardDiscovery.findBasicLandsIn(CARDS_PACKAGE, code)
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    /** The Spellgorger Weird minted by Ral and the Implicit Maze, in MH3's own token printing. */
    override val tokenArt: List<TokenPrinting> = listOf(
        TokenPrinting(
            name = "Spellgorger Weird",
            imageUri = "https://cards.scryfall.io/normal/front/3/3/33b63bd0-0b61-4a87-928f-95fc6b5a3150.jpg?1783911112",
        ),
    )

    /**
     * Play Booster: 6 commons, 3 uncommons, a rare or mythic, a new-to-Modern reprint (75% U / 22.5% R / 2.5% M), a
     * non-foil wildcard (42.7% C / 43.6% U / 8% R / 1.3% M) and a traditional-foil wildcard (42.9% C / 43.6% U / 7.9%
     * R / 1.3% M). The reprints appear only in their own slot. The land slot (a common half the time, else a basic),
     * the Special Guest and the Commander mythic share are not modelled. Figures from WotC's "Collecting Modern
     * Horizons 3" article, as kept in taw/magic-search-engine `data/boosters`.
     */
    override val boosterStrategy: BoosterStrategy by lazy {
        DedicatedSlotBooster(
            base = PlayBooster(
                commons = 6,
                wildcardSlots = listOf(
                    PlayBooster.wildcard(common = 0.4273, uncommon = 0.4364, rare = 0.080, mythic = 0.0134),
                    PlayBooster.wildcard(common = 0.4292, uncommon = 0.4355, rare = 0.079, mythic = 0.0134),
                ),
            ),
            slots = listOf(
                DedicatedSlot(
                    SheetSelector.Named(NEW_TO_MODERN_REPRINTS),
                    mapOf(Rarity.UNCOMMON to 0.75, Rarity.RARE to 0.225, Rarity.MYTHIC to 0.025),
                ),
            ),
        )
    }

    /** The new-to-Modern reprints (collector numbers 262–303), which open only in their own slot. */
    private val NEW_TO_MODERN_REPRINTS: Set<String> = setOf(
        "Angel of the Ruins", "Decree of Justice", "Distinguished Conjurer", "Orim's Chant",
        "Recruiter of the Guard", "Sevinne's Reclamation", "Deep Analysis", "Estrid's Invocation",
        "Kappa Cannoneer", "Reef Worm", "Shrieking Drake", "Buried Alive", "K'rrik, Son of Yawgmoth",
        "Nadier's Nightblade", "Ophiomancer", "Toxic Deluge", "Victimize", "Cursed Mirror", "Fledgling Dragon",
        "Laelia, the Blade Reforged", "Meltdown", "Meteoric Mace", "Annoyed Altisaur", "Branching Evolution",
        "Priest of Titania", "Sylvan Safekeeper", "Wirewood Symbiote", "Breya, Etherium Shaper",
        "Kaalia of the Vast", "Emerald Medallion", "Jet Medallion", "Junk Diver", "Pearl Medallion",
        "Ruby Medallion", "Sapphire Medallion", "Urza's Incubator", "Worn Powerstone", "Barbarian Ring",
        "Cephalid Coliseum", "Deserted Temple", "Nesting Grounds", "Phyrexian Tower",
    )

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.mh3.cards"
}
