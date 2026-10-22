package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.OnEnterRun
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Wanderwine Hub
 * Land
 *
 * As this land enters, you may reveal a Merfolk card from your hand.
 * If you don't, this land enters tapped.
 * {T}: Add {W} or {U}.
 *
 * One of Lorwyn's five "tribal reveal" duals. Same two atoms as Shadows over Innistrad's
 * shadowlands (see Game Trail):
 *  - [OnEnterRun] — the generic "as ~ enters, run [effect]" replacement wrapper.
 *  - [Effects.MayRevealCardFromHand] — an optional reveal whose `otherwise` rider fires when the
 *    player declines or holds no eligible card; here it taps the land.
 *
 * The only difference from the shadowlands is what the filter reads: a *creature type* on any card
 * type rather than a land subtype. [GameObjectFilter.Any] with a subtype predicate is what makes
 * that work — Lorwyn's Kindred cards (e.g. Crush Underfoot, a Kindred Instant — Giant) carry the
 * creature type without being creatures, and they count for the reveal.
 */
val WanderwineHub = card("Wanderwine Hub") {
    typeLine = "Land"
    colorIdentity = "WU"
    oracleText = "As this land enters, you may reveal a Merfolk card from your hand. " +
        "If you don't, this land enters tapped.\n{T}: Add {W} or {U}."

    replacementEffect(
        OnEnterRun(
            Effects.MayRevealCardFromHand(
                filter = GameObjectFilter.Any.withSubtype("Merfolk"),
                otherwise = Effects.EnterTapped(),
            )
        )
    )

    activatedAbility {
        cost = AbilityCost.Tap
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = Effects.AddMana(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "280"
        artist = "Warren Mahy"
        flavorText = "Below the great river, a bustling hub channels the flow of merrow trade."
        imageUri = "https://cards.scryfall.io/normal/front/c/c/ccec69de-7203-4810-a8ec-8748705ee3a2.jpg?1783942845"
    }
}
