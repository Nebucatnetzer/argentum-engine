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
 * Auntie's Hovel
 * Land
 *
 * As this land enters, you may reveal a Goblin card from your hand.
 * If you don't, this land enters tapped.
 * {T}: Add {B} or {R}.
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
val AuntiesHovel = card("Auntie's Hovel") {
    typeLine = "Land"
    colorIdentity = "BR"
    oracleText = "As this land enters, you may reveal a Goblin card from your hand. " +
        "If you don't, this land enters tapped.\n{T}: Add {B} or {R}."

    replacementEffect(
        OnEnterRun(
            Effects.MayRevealCardFromHand(
                filter = GameObjectFilter.Any.withSubtype("Goblin"),
                otherwise = Effects.EnterTapped(),
            )
        )
    )

    activatedAbility {
        cost = AbilityCost.Tap
        effect = Effects.AddMana(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "267"
        artist = "Wayne Reynolds"
        flavorText = "The Stinkdrinker warren's hill of salvaged trinkets is large enough to cut a door in."
        imageUri = "https://cards.scryfall.io/normal/front/0/9/098685c9-cd85-4279-a3b5-b495485bba35.jpg?1783942848"
    }
}
