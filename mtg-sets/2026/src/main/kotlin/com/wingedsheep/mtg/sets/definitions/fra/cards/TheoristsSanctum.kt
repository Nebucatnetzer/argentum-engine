package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.OnEnterRun

/**
 * Theorist's Sanctum
 * Land — Island
 *
 * ({T}: Add {U}.)
 * As this land enters, you may behold a Jace. If you don't, this land enters tapped.
 * {2}{U}, {T}: Empower Jace 2.
 *
 * The shadowland / Lorwyn reveal-land shape ([OnEnterRun] around an optional choice whose
 * "if you don't" rider taps the land), with [Effects.Behold] in place of a hand reveal: behold
 * (CR 701.4a) also accepts a Jace permanent you control, such as a Jace token. The {U} ability
 * comes from the Island subtype.
 */
val TheoristsSanctum = card("Theorist's Sanctum") {
    manaCost = ""
    colorIdentity = "U"
    typeLine = "Land — Island"
    oracleText = "({T}: Add {U}.)\n" +
        "As this land enters, you may behold a Jace. If you don't, this land enters tapped. " +
        "(To behold a Jace, choose a Jace you control or reveal a Jace card from your hand.)\n" +
        "{2}{U}, {T}: Empower Jace 2."

    replacementEffect(
        OnEnterRun(
            Effects.Behold(
                filter = GameObjectFilter.Any.withSubtype("Jace"),
                otherwise = Effects.EnterTapped(),
            )
        )
    )

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{U}"), Costs.Tap)
        effect = Patterns.Mechanic.empowerJace(2)
        description = "Empower Jace 2."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "191"
        artist = "Adam Paquette"
        imageUri = "https://cards.scryfall.io/normal/front/2/2/22db5bba-46c9-4a26-821d-303ddb386ea4.jpg?1788878256"
        inBooster = false
    }
}
