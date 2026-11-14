package com.wingedsheep.mtg.sets.definitions.trc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Kolinahr Priest
 * {2}{U}
 * Creature — Vulcan Monk
 * 3/2
 * Flash
 */
val KolinahrPriest = card("Kolinahr Priest") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Vulcan Monk"
    oracleText = "Flash (You may cast this spell any time you could cast an instant.)"
    power = 3
    toughness = 2
    keywords(Keyword.FLASH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "146"
        artist = "Andrey Kuzinskiy"
        flavorText = "\"What thee are about to see comes down from the time of the beginning without change. This is the Vulcan heart; this is the Vulcan soul. This is our way.\"\n—T'Pau"
        imageUri = "https://cards.scryfall.io/normal/front/0/1/0167486d-ed77-4612-a385-25251b544a14.jpg?1785981062"
    }
}
