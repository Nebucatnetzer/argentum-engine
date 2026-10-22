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
 * Gilt-Leaf Palace
 * Land
 *
 * As this land enters, you may reveal an Elf card from your hand.
 * If you don't, this land enters tapped.
 * {T}: Add {B} or {G}.
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
val GiltLeafPalace = card("Gilt-Leaf Palace") {
    typeLine = "Land"
    colorIdentity = "BG"
    oracleText = "As this land enters, you may reveal an Elf card from your hand. " +
        "If you don't, this land enters tapped.\n{T}: Add {B} or {G}."

    replacementEffect(
        OnEnterRun(
            Effects.MayRevealCardFromHand(
                filter = GameObjectFilter.Any.withSubtype("Elf"),
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
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "268"
        artist = "Christopher Moeller"
        flavorText = "Dawn's Light, greatest palace of Gilt-Leaf Wood, is built on tiers of wood and power."
        imageUri = "https://cards.scryfall.io/normal/front/c/c/cc4bbbb5-4218-4b2a-9ca2-de4d5f5dda19.jpg?1783942847"
    }
}
