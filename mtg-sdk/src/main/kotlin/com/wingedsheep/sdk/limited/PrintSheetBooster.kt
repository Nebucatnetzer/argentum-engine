package com.wingedsheep.sdk.limited

import com.wingedsheep.sdk.model.CardDefinition
import kotlin.random.Random

/**
 * One paper print sheet: each card name with the number of times it appears on the sheet (all its
 * art variants summed). A pack slot drawing from the sheet opens a name with probability
 * proportional to that count.
 */
data class PrintSheet(val slots: Int, val cardCounts: Map<String, Int>) {
    init {
        require(slots >= 0) { "slots must be non-negative" }
        require(cardCounts.values.all { it > 0 }) { "Every card on a print sheet appears at least once" }
    }
}

/**
 * Booster collated straight from print sheets, for the early sets whose packs predate rarity
 * slots: Arabian Nights, Antiquities, The Dark and Fallen Empires opened 8-card packs of 6 cards
 * from the common sheet and 2 from the uncommon sheet, and a card's frequency was the number of
 * times it was printed on its sheet (Arabian Nights' Flying Men is C5, its Fishliver Oil C3+C1).
 * Scryfall's "rare" for these sets is a scarcer card on the uncommon sheet, so a rarity-slot
 * booster can't express them.
 *
 * [StandardBooster] draws uniformly within a rarity and [LandSlotBooster] weights a single named
 * slot; neither takes a per-name print run across every slot, which is this type's axis. A name on
 * neither sheet is never opened; a name on both (Antiquities' Strip Mine) can come from either.
 * Names are not repeated within a pack, and banned or missing cards simply drop off their sheet.
 */
data class PrintSheetBooster(val sheets: List<PrintSheet>) : BoosterStrategy {

    override fun generate(pool: List<CardDefinition>, random: Random): List<CardDefinition> {
        val byName = pool.associateBy { it.name }
        val used = hashSetOf<String>()
        val booster = mutableListOf<CardDefinition>()
        for (sheet in sheets) {
            repeat(sheet.slots) {
                val available = sheet.cardCounts.entries.filter { it.key in byName && it.key !in used }
                val picked = weightedPick(available, random) { it.value } ?: return@repeat
                used.add(picked.key)
                booster.add(byName.getValue(picked.key))
            }
        }
        return booster
    }
}
