package com.wingedsheep.sdk.limited

import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.model.CardDefinition
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import kotlin.random.Random

class PrintSheetBoosterTest : DescribeSpec({
    fun card(name: String) = CardDefinition(
        manaCost = ManaCost.ZERO,
        name = name,
        typeLine = TypeLine(cardTypes = setOf(CardType.SORCERY)),
    )

    val commonSheet = PrintSheet(6, (1..20).associate { "C$it" to if (it == 1) 5 else 1 })
    val uncommonSheet = PrintSheet(2, (1..10).associate { "U$it" to if (it <= 5) 3 else 2 })
    val pool = (commonSheet.cardCounts.keys + uncommonSheet.cardCounts.keys + "Off Sheet").map(::card)
    val strategy = PrintSheetBooster(listOf(commonSheet, uncommonSheet))

    describe("8-card early-set packs") {
        it("opens 6 cards from the common sheet and 2 from the uncommon sheet, never repeating a name") {
            repeat(300) { seed ->
                val pack = strategy.generate(pool, Random(seed))
                pack shouldHaveSize 8
                pack.map { it.name }.toSet().size shouldBe 8
                pack.take(6).all { it.name in commonSheet.cardCounts } shouldBe true
                pack.drop(6).all { it.name in uncommonSheet.cardCounts } shouldBe true
            }
        }

        it("opens each card in proportion to its print run") {
            val random = Random(1)
            val packs = 6000
            val counts = mutableMapOf<String, Int>()
            repeat(packs) { strategy.generate(pool, random).forEach { counts.merge(it.name, 1, Int::plus) } }
            // On the uncommon sheet a U3 card shows up 1.5x as often as a U2 card.
            val u3 = (1..5).sumOf { counts["U$it"] ?: 0 } / 5.0
            val u2 = (6..10).sumOf { counts["U$it"] ?: 0 } / 5.0
            (u3 / u2) shouldBe (1.45 plusOrMinus 0.15)
            // Drawn without replacement, a C5 card lands in ~80% of packs and a C1 card in ~27%.
            ((counts["C1"] ?: 0).toDouble() / (counts["C2"] ?: 1)) shouldBe (2.9 plusOrMinus 0.4)
        }

        it("drops banned or unimplemented cards from their sheet") {
            val thin = pool.filter { it.name != "U1" && it.name != "C1" }
            repeat(100) { seed ->
                val names = strategy.generate(thin, Random(seed)).map { it.name }
                names.none { it == "U1" || it == "C1" || it == "Off Sheet" } shouldBe true
            }
        }
    }
})
