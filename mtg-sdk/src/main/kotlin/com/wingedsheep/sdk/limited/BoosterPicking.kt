package com.wingedsheep.sdk.limited

import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import kotlin.random.Random

/** Picks cards by rarity without repeating names within a single booster. */
internal class RarityPicker(
    pool: List<CardDefinition>,
    private val random: Random,
    private val used: MutableSet<String> = mutableSetOf(),
) {
    private val byRarity: Map<Rarity, MutableList<CardDefinition>> =
        pool.groupBy { it.metadata.rarity }.mapValues { it.value.toMutableList() }

    fun hasAny(rarity: Rarity): Boolean = !byRarity[rarity].isNullOrEmpty()

    /** Exclude [name] from later picks — for cards a strategy placed without this picker. */
    fun markUsed(name: String) {
        used.add(name)
    }

    fun pick(rarity: Rarity): CardDefinition? {
        val available = byRarity[rarity]?.filter { it.name !in used } ?: return null
        if (available.isEmpty()) return null
        val picked = available.random(random)
        used.add(picked.name)
        return picked
    }

    /** A rare, upgraded to a mythic with [mythicChance] when the pool has one; null when neither is left. */
    fun pickRareOrMythic(mythicChance: Double): CardDefinition? {
        val rolledMythic = hasAny(Rarity.MYTHIC) && random.nextDouble() < mythicChance
        return (if (rolledMythic) pick(Rarity.MYTHIC) else null)
            ?: pick(Rarity.RARE)
            ?: pick(Rarity.MYTHIC)
    }

    /** A card of a rarity rolled from [weights]; a rolled rarity with nothing left falls through to the rest by weight. */
    fun pickWeighted(weights: Map<Rarity, Double>): CardDefinition? {
        val rolled = rollRarity(weights, random)
        return pick(rolled) ?: weights.entries.sortedByDescending { it.value }.firstNotNullOfOrNull { pick(it.key) }
    }
}

/** A rarity drawn with probability proportional to its weight. */
internal fun rollRarity(weights: Map<Rarity, Double>, random: Random): Rarity {
    var roll = random.nextDouble() * weights.values.sum()
    for ((rarity, weight) in weights) {
        roll -= weight
        if (roll < 0) return rarity
    }
    return weights.keys.last()
}

internal fun requireRarityWeights(weights: Map<Rarity, Double>, what: String) {
    require(weights.isNotEmpty() && weights.values.all { it >= 0.0 } && weights.values.sum() > 0.0) {
        "$what must be non-empty, non-negative and sum above zero: $weights"
    }
}

/** Picks one of [items] with probability proportional to [weight]; null when nothing has positive weight. */
internal fun <T> weightedPick(items: List<T>, random: Random, weight: (T) -> Int): T? {
    val total = items.sumOf { weight(it).coerceAtLeast(0) }
    if (total <= 0) return null
    var roll = random.nextInt(total)
    for (item in items) {
        roll -= weight(item).coerceAtLeast(0)
        if (roll < 0) return item
    }
    return null
}
