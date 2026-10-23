package com.wingedsheep.sdk.limited

import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import kotlin.random.Random

/**
 * Play Booster (Murders at Karlov Manor, February 2024, onward): the 14-card pack
 * that replaced both Draft and Set Boosters. Paper composition:
 *
 *  - Slots 1–7: 7 commons (slot 7 carries a small Special Guest chance — not modeled)
 *  - Slots 8–10: 3 uncommons
 *  - Slot 11: non-foil wildcard of any rarity
 *  - Slot 12: rare or mythic rare (12.5% mythic)
 *  - Slot 13: land
 *  - Slot 14: traditional-foil wildcard of any rarity
 *
 * Engine adaptation: foils, The List, Special Guests and bonus sheets are not modeled, so
 * both wildcard slots are plain weighted-rarity slots ([wildcardSlots], one rarity mix per
 * slot); the land slot is omitted because the generator strips basic lands from the pool and
 * supplies them separately at deck building. Sets whose land slot carries common nonbasic lands
 * compose with [LandSlotBooster]. Net result: 13 playable non-land cards per pack.
 *
 * The two wildcards have very different paper odds: the non-foil one is mostly uncommon, the
 * foil one mostly common. Each set publishes its own figures in its "Collecting" article; the
 * defaults are typical of the 2024–2026 sets ([DEFAULT_WILDCARD], [DEFAULT_FOIL_WILDCARD]).
 * Products with other wildcard layouts (e.g. March of the Machine: The Aftermath's Epilogue
 * Booster, or Innistrad Remastered's extra retro slot) pass their own list.
 *
 * No card name is duplicated within a single pack.
 */
data class PlayBooster(
    val commons: Int = 7,
    val uncommons: Int = 3,
    val raresOrMythics: Int = 1,
    val mythicChance: Double = 0.125,
    /** One relative rarity mix per wildcard slot; a rarity the pool lacks falls through. */
    val wildcardSlots: List<Map<Rarity, Double>> = listOf(DEFAULT_WILDCARD, DEFAULT_FOIL_WILDCARD),
) : RarityCountBooster {

    init {
        wildcardSlots.forEach { requireRarityWeights(it, "wildcard weights") }
    }

    override val rareSlots: Int get() = raresOrMythics

    override fun withRareSlotAsUncommon(): PlayBooster {
        require(raresOrMythics > 0) { "No rare slot to trade" }
        return copy(uncommons = uncommons + 1, raresOrMythics = raresOrMythics - 1)
    }

    override fun generate(pool: List<CardDefinition>, random: Random): List<CardDefinition> {
        val picker = RarityPicker(pool, random)
        val booster = mutableListOf<CardDefinition>()

        repeat(commons) { picker.pick(Rarity.COMMON)?.let(booster::add) }
        repeat(uncommons) { picker.pick(Rarity.UNCOMMON)?.let(booster::add) }
        repeat(raresOrMythics) {
            val card = picker.pickRareOrMythic(mythicChance)
                ?: picker.pick(Rarity.UNCOMMON)
                ?: picker.pick(Rarity.COMMON)
                ?: throw IllegalStateException("No cards available for booster generation")
            booster.add(card)
        }
        for (weights in wildcardSlots) {
            val pick = picker.pickWeighted(weights)
                ?: picker.pick(Rarity.COMMON)
                ?: picker.pick(Rarity.UNCOMMON)
                ?: picker.pick(Rarity.RARE)
                ?: picker.pick(Rarity.MYTHIC)
            pick?.let(booster::add)
        }
        return booster
    }

    companion object {
        /**
         * A wildcard rarity mix from a set's published per-slot percentages. A combined
         * rare-or-mythic share is split 7:1 rare:mythic, like the rare slot.
         */
        fun wildcard(common: Double, uncommon: Double, rareOrMythic: Double): Map<Rarity, Double> =
            wildcard(common, uncommon, rare = rareOrMythic * 0.875, mythic = rareOrMythic * 0.125)

        /** A wildcard rarity mix with separate rare and mythic shares. */
        fun wildcard(common: Double, uncommon: Double, rare: Double, mythic: Double): Map<Rarity, Double> = linkedMapOf(
            Rarity.COMMON to common,
            Rarity.UNCOMMON to uncommon,
            Rarity.RARE to rare,
            Rarity.MYTHIC to mythic,
        )

        /** Non-foil wildcard: ~17% C, 63% U, 20% R/M — the 2024–2026 sets' typical published odds. */
        val DEFAULT_WILDCARD: Map<Rarity, Double> = wildcard(common = 0.17, uncommon = 0.63, rareOrMythic = 0.20)

        /** Traditional-foil wildcard: ~58% C, 32% U, 10% R/M. */
        val DEFAULT_FOIL_WILDCARD: Map<Rarity, Double> = wildcard(common = 0.58, uncommon = 0.32, rareOrMythic = 0.10)
    }
}
