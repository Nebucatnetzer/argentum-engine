package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter

/**
 * Lumaret's Favor — Secrets of Strixhaven #153
 * {1}{G} · Instant
 *
 * Infusion — When you cast this spell, copy it if you gained life this turn. You may choose new
 * targets for the copy.
 * Target creature gets +2/+4 until end of turn.
 *
 * Infusion here is a cast trigger whose condition is checked on resolution, not an intervening
 * "if" — per the card's ruling it triggers regardless, so lifegain in response still copies. The
 * copy-spell sibling of Social Snub, but the copy is **mandatory** (not "you may") and gated on
 * `Conditions.YouGainedLifeThisTurn` through [Effects.If]. `Triggers.self.isCast()` fires from the stack while
 * the spell is still on it; `Effects.CopyTargetSpell(TriggeringEntity)` copies the triggering spell
 * and offers new targets for the copy by default (a copy isn't cast, CR 707.10, so it doesn't
 * re-trigger Infusion). The main spell is the plain `Effects.ModifyStats(2, 4, …)` end-of-turn pump.
 */
val LumaretsFavor = card("Lumaret's Favor") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Infusion — When you cast this spell, copy it if you gained life this turn. " +
        "You may choose new targets for the copy.\n" +
        "Target creature gets +2/+4 until end of turn."

    triggeredAbility {
        trigger = Triggers.self.isCast()
        effect = Effects.If(
            Conditions.YouGainedLifeThisTurn,
            Effects.CopyTargetSpell(target = EffectTarget.TriggeringEntity)
        )
        description = "Infusion — When you cast this spell, copy it if you gained life this turn. " +
            "You may choose new targets for the copy."
    }

    spell {
        val creature = target(TargetFilter.Creature)
        effect = Effects.ModifyStats(2, 4, creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "153"
        artist = "Mariah Tekulve"
        flavorText = "Though the lumarets were ignored by the other Witherbloom students, it was clear to Lluwen they were much more complex than they seemed."
        imageUri = "https://cards.scryfall.io/normal/front/c/5/c5e7c856-8b71-44e6-8998-0b0b3ff0ef99.jpg?1775938045"
    }
}
