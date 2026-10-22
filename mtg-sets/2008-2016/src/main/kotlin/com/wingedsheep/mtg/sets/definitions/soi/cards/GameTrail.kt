package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.OnEnterRun
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Game Trail
 * Land
 *
 * As this land enters, you may reveal a Mountain or Forest card from your hand.
 * If you don't, this land enters tapped.
 * {T}: Add {R} or {G}.
 *
 * Composed from two atoms:
 *  - [OnEnterRun] — generic "as ~ enters, run [effect]" replacement wrapper.
 *  - [Effects.MayRevealCardFromHand] — atomic optional reveal with an `otherwise`
 *    rider that fires when the player declines or has no eligible card. Here the
 *    rider taps the land, expressing "if you don't, this land enters tapped."
 *
 * The whole SOI shadowland cycle (Choked Estuary, Foreboding Ruins, Fortified
 * Village, Game Trail, Port Town) reuses this exact shape — only the filter
 * subtypes and produced mana differ.
 */
val GameTrail = card("Game Trail") {
    typeLine = "Land"
    colorIdentity = "RG"
    oracleText = "As this land enters, you may reveal a Mountain or Forest card from your hand. " +
        "If you don't, this land enters tapped.\n{T}: Add {R} or {G}."

    replacementEffect(
        OnEnterRun(
            Effects.MayRevealCardFromHand(
                filter = GameObjectFilter.Land.withAnySubtype("Mountain", "Forest"),
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
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "276"
        artist = "Adam Paquette"
        imageUri = "https://cards.scryfall.io/normal/front/2/1/21cb5950-adaa-438d-998b-3a64bd4a2b3e.jpg?1576385553"
        ruling(
            "2016-04-08",
            "Lands don't have a subtype just because they can produce mana of the corresponding color. " +
                "Game Trail itself is neither a Mountain nor a Forest, even though it produces red and green " +
                "mana, so you can't reveal one to satisfy the ability of another.",
        )
        ruling(
            "2016-04-08",
            "If an effect instructs you to put Game Trail onto the battlefield tapped, it will still " +
                "enter the battlefield tapped even if you reveal a land card from your hand.",
        )
        ruling(
            "2016-04-08",
            "You may reveal any land card with either or both of the appropriate subtypes. It doesn't have " +
                "to be a basic land. For example, you could reveal Canopy Vista from the Battle for Zendikar " +
                "set to satisfy the ability of Game Trail.",
        )
        ruling(
            "2016-04-08",
            "If a Mountain or Forest is entering the battlefield from your hand at the same time as " +
                "Game Trail, you may reveal the other land to have Game Trail enter untapped.",
        )
    }
}
