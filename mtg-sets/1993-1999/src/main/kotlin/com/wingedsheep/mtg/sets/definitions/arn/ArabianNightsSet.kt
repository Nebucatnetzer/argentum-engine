package com.wingedsheep.mtg.sets.definitions.arn

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.limited.BoosterStrategy
import com.wingedsheep.sdk.limited.PrintSheet
import com.wingedsheep.sdk.limited.PrintSheetBooster
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.TokenPrinting

/**
 * Arabian Nights (1993)
 *
 * The first Magic: The Gathering expansion. 78 cards, no block. Many of its
 * mechanics (banding, landwalk variants, coin flips, control-changing effects,
 * subgames, ante) predate or stress the modern rules.
 *
 * Complete: every card we intend to build is built, Shahrazad's Magic subgame (CR 729)
 * included. The one that remains is a policy exclusion listed in
 * `coverage/card-exclusions.json` — Jeweled Bird needs the ante zone the engine doesn't
 * have — so the set is draftable and doesn't carry `incomplete`.
 *
 * Set Code: ARN
 * Release Date: December 17, 1993
 * Card Count: 78
 */
object ArabianNightsSet : MtgSet {

    override val code = "ARN"
    override val displayName = "Arabian Nights"
    override val releaseDate = "1993-12-17"
    override val basicLandsFallbackCode = "POR"
    override val sealedSupported = true

    override val cards: List<CardDefinition> by lazy {
        // Self-stamp [code] onto every card, honouring the MtgSet contract that `cards` is already
        // set-stamped. This makes `CardDefinition.setCode` a reliable "originally printed in ARN"
        // signal for every consumer (engine tests included), not only the game-server load path —
        // which is what City in a Bottle's OriginallyPrintedInSet("ARN") predicate reads.
        CardDiscovery.findIn(CARDS_PACKAGE).map { if (it.setCode == null) it.copy(setCode = code) else it }
    }

    /**
     * ARN printed only Mountain. Keep [basicLandsFallbackCode] for a complete Limited land supply while
     * exposing the set's own printing here so discovery and set coverage include it.
     */
    override val basicLands: List<CardDefinition> by lazy {
        CardDiscovery.findBasicLandsIn(CARDS_PACKAGE, code)
    }

    /**
     * Arabian Nights predates token *cards* — Scryfall has no `tarn` set to sync from — so its token
     * art is self-hosted under `web-client/public/images/tokens/` and declared here, the same route
     * Invasion, Apocalypse and Odyssey take.
     */
    override val tokenArt: List<TokenPrinting> = listOf(
        TokenPrinting(
            name = "Bird",
            imageUri = "/images/tokens/arn-bird.jpeg",
            power = 4,
            toughness = 4,
            colors = setOf(Color.RED),
        ),
        TokenPrinting(
            name = "Djinn",
            imageUri = "/images/tokens/arn-djinn.jpeg",
            power = 5,
            toughness = 5,
            colors = emptySet(),
        ),
    )

    /**
     * Arabian Nights boosters held 8 cards: 6 from the common print sheet and 2 from the uncommon sheet, each card as
     * often as it was printed on its sheet — Flying Men is C5 and Fishliver Oil C3+C1; Scryfall's rares are the U2
     * cards. The sheet's Mountain (C11) is a basic land, which the generator supplies at deck building instead. Counts
     * are from WotC's checklist as kept in taw/magic-search-engine `data/print_sheets`.
     */
    override val boosterStrategy: BoosterStrategy by lazy {
        PrintSheetBooster(listOf(PrintSheet(slots = 6, COMMON_SHEET), PrintSheet(slots = 2, UNCOMMON_SHEET)))
    }

    private val COMMON_SHEET: Map<String, Int> = mapOf(
        "Army of Allah" to 4,
        "Bird Maiden" to 4,
        "Camel" to 5,
        "Cuombajj Witches" to 4,
        "Dandân" to 4,
        "Desert" to 11,
        "Desert Nomads" to 4,
        "Erg Raiders" to 5,
        "Fishliver Oil" to 4,
        "Flying Men" to 5,
        "Ghazbán Ogre" to 4,
        "Giant Tortoise" to 4,
        "Hasran Ogress" to 5,
        "Hurr Jackal" to 4,
        "Kird Ape" to 5,
        "Metamorphosis" to 4,
        "Moorish Cavalry" to 5,
        "Mountain" to 1,
        "Nafs Asp" to 5,
        "Oubliette" to 4,
        "Piety" to 4,
        "Rukh Egg" to 4,
        "Sandstorm" to 4,
        "Stone-Throwing Devils" to 4,
        "Unstable Mutation" to 5,
        "War Elephant" to 4,
        "Wyluli Wolf" to 5,
    )

    private val UNCOMMON_SHEET: Map<String, Int> = mapOf(
        "Abu Ja'far" to 3,
        "Aladdin" to 2,
        "Aladdin's Lamp" to 2,
        "Aladdin's Ring" to 2,
        "Ali Baba" to 3,
        "Ali from Cairo" to 2,
        "Bazaar of Baghdad" to 3,
        "Bottle of Suleiman" to 2,
        "Brass Man" to 3,
        "City in a Bottle" to 2,
        "City of Brass" to 3,
        "Cyclone" to 3,
        "Dancing Scimitar" to 2,
        "Desert Twister" to 3,
        "Diamond Valley" to 2,
        "Drop of Honey" to 2,
        "Ebony Horse" to 2,
        "El-Hajjâj" to 2,
        "Elephant Graveyard" to 2,
        "Erhnam Djinn" to 2,
        "Eye for an Eye" to 3,
        "Flying Carpet" to 3,
        "Guardian Beast" to 2,
        "Ifh-Bíff Efreet" to 2,
        "Island Fish Jasconius" to 2,
        "Island of Wak-Wak" to 2,
        "Jandor's Ring" to 2,
        "Jandor's Saddlebags" to 2,
        "Jeweled Bird" to 3,
        "Jihad" to 2,
        "Junún Efreet" to 2,
        "Juzám Djinn" to 2,
        "Khabál Ghoul" to 3,
        "King Suleiman" to 2,
        "Library of Alexandria" to 3,
        "Magnetic Mountain" to 3,
        "Merchant Ship" to 3,
        "Mijae Djinn" to 2,
        "Oasis" to 4,
        "Old Man of the Sea" to 2,
        "Pyramids" to 2,
        "Repentant Blacksmith" to 2,
        "Ring of Ma'rûf" to 2,
        "Sandals of Abdallah" to 3,
        "Serendib Djinn" to 2,
        "Serendib Efreet" to 2,
        "Shahrazad" to 2,
        "Sindbad" to 3,
        "Singing Tree" to 2,
        "Sorceress Queen" to 3,
        "Ydwen Efreet" to 2,
    )

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.arn.cards"
}
