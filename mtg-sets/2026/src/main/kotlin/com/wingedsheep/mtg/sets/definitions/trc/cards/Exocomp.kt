package com.wingedsheep.mtg.sets.definitions.trc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Exocomp
 * {2}{U}
 * Artifact Creature — Construct
 * 3/1
 * Flying
 */
val Exocomp = card("Exocomp") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Artifact Creature — Construct"
    oracleText = "Flying (This creature can't be blocked except by creatures with flying or reach.)"
    power = 3
    toughness = 1
    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "145"
        artist = "Pierre Raveneau"
        flavorText = "Their ability to replicate new circuits as they learn inadvertently bootstrapped exocomps into sentience in the 2360s."
        imageUri = "https://cards.scryfall.io/normal/front/4/6/46012ec9-9605-44fe-84a9-6aab0738737f.jpg?1785981040"
    }
}
