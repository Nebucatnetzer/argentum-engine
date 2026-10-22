package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.ParseOutcome
import com.wingedsheep.assay.syntax.parseLine
import com.wingedsheep.assay.syntax.printLine
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

class XDamageTest : StringSpec({

    fun fragment(line: String): CardFragment =
        Grammar.abilityLine.parseLine(line)
            .shouldBeInstanceOf<ParseOutcome.Accepted<CardFragment>>().value

    fun roundTrips(line: String) {
        Grammar.abilityLine.printLine(fragment(line)) shouldBe line
    }

    "X damage to target creature has the X value and target requirement" {
        val line = "~ deals X damage to target creature."

        fragment(line) shouldBe CardFragment(
            script = CardScript(
                spellEffect = Effects.DealDamage(
                    DynamicAmount.XValue,
                    Targets.bound(),
                ),
                targetRequirements = listOf(
                    Targets.permanent(GameObjectFilter.Creature),
                ),
            ),
        )
        roundTrips(line)
    }

    "X damage composes with an activated ability's X cost" {
        val line =
            "{X}{R}, {T}: ~ deals X damage to target creature without flying."

        val ability = fragment(line).script.activatedAbilities.single()
        val damage = ability.effect.shouldBeInstanceOf<DealDamageEffect>()

        damage.amount shouldBe DynamicAmount.XValue
        roundTrips(line)
    }

    "X damage supports the existing filtered-target vocabulary" {
        roundTrips("~ deals X damage to target creature an opponent controls.")
        roundTrips("~ deals X damage to target creature or planeswalker.")
    }

    "X damage and fixed damage remain different models" {
        fragment("~ deals X damage to target creature.") shouldNotBe
            fragment("~ deals 2 damage to target creature.")
    }

    "plural damage targets are still unsupported by this rule" {
        Grammar.abilityLine
            .parseLine("~ deals X damage to two target creatures.")
            .shouldBeInstanceOf<ParseOutcome.Declined>()
    }
})
