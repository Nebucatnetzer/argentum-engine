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
 * Ancient Amphitheater
 * Land
 *
 * As this land enters, you may reveal a Giant card from your hand.
 * If you don't, this land enters tapped.
 * {T}: Add {R} or {W}.
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
val AncientAmphitheater = card("Ancient Amphitheater") {
    typeLine = "Land"
    colorIdentity = "RW"
    oracleText = "As this land enters, you may reveal a Giant card from your hand. " +
        "If you don't, this land enters tapped.\n{T}: Add {R} or {W}."

    replacementEffect(
        OnEnterRun(
            Effects.MayRevealCardFromHand(
                filter = GameObjectFilter.Any.withSubtype("Giant"),
                otherwise = Effects.EnterTapped(),
            )
        )
    )

    activatedAbility {
        cost = AbilityCost.Tap
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "266"
        artist = "Rob Alexander"
        flavorText = "The arbiter Galanda Feudkiller judges Lorwyn's squabbles from a lofty perspective."
        imageUri = "https://cards.scryfall.io/normal/front/2/f/2ff3a82d-e794-4c3b-994d-b28b8eb7eac4.jpg?1783942848"
    }
}
