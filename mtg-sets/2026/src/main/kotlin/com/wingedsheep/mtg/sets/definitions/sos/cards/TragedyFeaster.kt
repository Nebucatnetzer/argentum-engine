package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.WardCost
import com.wingedsheep.sdk.core.Step

/**
 * Tragedy Feaster — Secrets of Strixhaven #102
 * {2}{B}{B} · Creature — Demon · 7/6
 *
 * Trample
 * Ward—Discard a card.
 * Infusion — At the beginning of your end step, sacrifice a permanent unless you gained
 * life this turn.
 *
 * Infusion is an ability word (no rules meaning) flavoring an end-step trigger gated on the
 * "you gained life this turn" tracker. It is not an intervening "if": the ability triggers every
 * end step and checks as it resolves (per the card's ruling), so lifegain that resolves first —
 * another end-step trigger, or a spell cast in response — still spares you. The effect is
 * [Effects.If] over [Conditions.Not] of [Conditions.YouGainedLifeThisTurn], making the controller
 * sacrifice a permanent of their choice ([SacrificeEffect] over any permanent — the controller-
 * chooses idiom shared with Accursed Centaur). Ward—Discard a card via [WardCost.Discard].
 */
val TragedyFeaster = card("Tragedy Feaster") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Demon"
    power = 7
    toughness = 6
    oracleText = "Trample\n" +
        "Ward—Discard a card.\n" +
        "Infusion — At the beginning of your end step, sacrifice a permanent unless you gained " +
        "life this turn."

    keywords(Keyword.TRAMPLE)
    keywordAbility(KeywordAbility.Ward(WardCost.Discard()))

    triggeredAbility {
        trigger = Triggers.you.beginningOf(Step.END)
        effect = Effects.If(
            Conditions.Not(Conditions.YouGainedLifeThisTurn),
            Effects.SacrificeOwn(GameObjectFilter.Permanent)
        )
        description = "Infusion — At the beginning of your end step, sacrifice a permanent unless " +
            "you gained life this turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "102"
        artist = "Raph Lomotan"
        flavorText = "In the wake of the archaics' rampage, much worse was to come."
        imageUri = "https://cards.scryfall.io/normal/front/b/9/b93cbaad-8ed8-4a1d-b95a-20a616dfedc9.jpg?1775937623"
    }
}
