package com.wingedsheep.mtg.sets.definitions.trc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Klingon Strike Force
 * {3}{R}{R}
 * Creature — Klingon Warrior
 * 5/5
 * Trample
 * Haste
 * When this creature dies, it deals 2 damage to each opponent.
 */
val KlingonStrikeForce = card("Klingon Strike Force") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Klingon Warrior"
    oracleText = "Trample (This creature can deal excess combat damage to the player it's attacking.)\n" +
        "Haste (This creature can attack and {T} as soon as it comes under your control.)\n" +
        "When this creature dies, it deals 2 damage to each opponent."
    power = 5
    toughness = 5
    keywords(Keyword.TRAMPLE, Keyword.HASTE)

    triggeredAbility {
        trigger = Triggers.self.dies()
        effect = Effects.DealDamage(2, EffectTarget.PlayerRef(Player.EachOpponent))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "199"
        artist = "Fahmi Fauzi"
        imageUri = "https://cards.scryfall.io/normal/front/b/c/bc121e37-b01c-4cf3-bd41-19a1cb21caa5.jpg?1784036722"
    }
}
