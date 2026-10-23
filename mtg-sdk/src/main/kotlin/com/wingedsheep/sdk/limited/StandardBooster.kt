package com.wingedsheep.sdk.limited

import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import kotlin.random.Random

/**
 * Standard 15-card booster: N commons + N uncommons + 1 rare slot, with the
 * rare slot upgraded to a mythic with [mythicChance] when mythics exist.
 *
 * No card name is duplicated within a single pack.
 */
data class StandardBooster(
    val commons: Int = 11,
    val uncommons: Int = 3,
    val rares: Int = 1,
    val mythicChance: Double = 0.125,
) : RarityCountBooster {

    override val rareSlots: Int get() = rares

    override fun withRareSlotAsUncommon(): StandardBooster {
        require(rares > 0) { "No rare slot to trade" }
        return copy(uncommons = uncommons + 1, rares = rares - 1)
    }

    override fun generate(pool: List<CardDefinition>, random: Random): List<CardDefinition> {
        val picker = RarityPicker(pool, random)
        val booster = mutableListOf<CardDefinition>()

        repeat(commons) { picker.pick(Rarity.COMMON)?.let(booster::add) }
        repeat(uncommons) { picker.pick(Rarity.UNCOMMON)?.let(booster::add) }
        repeat(rares) {
            val card = picker.pickRareOrMythic(mythicChance)
                ?: picker.pick(Rarity.UNCOMMON)
                ?: picker.pick(Rarity.COMMON)
                ?: throw IllegalStateException("No cards available for booster generation")
            booster.add(card)
        }
        return booster
    }
}
