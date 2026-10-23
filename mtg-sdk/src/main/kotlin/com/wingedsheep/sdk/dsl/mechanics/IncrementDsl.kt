package com.wingedsheep.sdk.dsl

import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.conditions.AnyCondition
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey

/**
 * Add Increment (Secrets of Strixhaven).
 *
 * "Increment (Whenever you cast a spell, if the amount of mana you spent is greater than
 * this creature's power or toughness, put a +1/+1 counter on this creature.)"
 *
 * Wires the full keyword from one call:
 *  - the display-only [KeywordAbility.Increment] (prints the keyword + surfaces
 *    [com.wingedsheep.sdk.core.Keyword.INCREMENT] in the base keyword set), and
 *  - a "whenever you cast a spell" triggered ability whose intervening-if compares the
 *    mana spent on that spell against this creature's power *or* toughness (CR 603.4 — the
 *    condition is checked both when the ability would trigger and again on resolution).
 *
 * The mana spent is the cast-time snapshot carried on the trigger
 * ([ContextPropertyKey.MANA_SPENT_ON_TRIGGERING_SPELL]), not a read of the spell on the stack:
 * the ability still resolves if that spell has been countered or otherwise left the stack, and the
 * second check must still see what was spent on it.
 *
 * "greater than this creature's power or toughness" is true when the mana spent exceeds
 * *either* characteristic, i.e. greater than the smaller of the two — modelled here as an
 * OR over the two `>` comparisons rather than a single `min`, so each side reads the live
 * projected value independently.
 */
fun CardBuilder.increment() {
    keywordAbilityList.add(KeywordAbility.Increment)

    val manaSpent = DynamicAmounts.manaSpentOnTriggeringSpell()
    val incrementCondition = AnyCondition(
        listOf(
            Compare(manaSpent, ComparisonOperator.GT, DynamicAmounts.sourcePower()),
            Compare(manaSpent, ComparisonOperator.GT, DynamicAmounts.sourceToughness())
        )
    )

    triggeredAbilities.add(
        TriggeredAbility.create(
            trigger = Triggers.you.casts(),
            effect = Effects.AddCounters(CounterType.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
            interveningIf = incrementCondition,
            descriptionOverride = "Increment (Whenever you cast a spell, if the amount of mana " +
                "you spent is greater than this creature's power or toughness, put a +1/+1 " +
                "counter on this creature.)"
        )
    )
}
