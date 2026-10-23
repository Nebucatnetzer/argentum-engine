package com.wingedsheep.sdk.limited

/**
 * A booster built from fixed per-rarity slot counts ([StandardBooster], [PlayBooster]).
 *
 * The trade below is what lets a [DedicatedSlotBooster] move the pack's rare onto a dedicated
 * sheet: paper packs that put a double-faced or legendary rare in its own slot open one more
 * ordinary uncommon in exchange, so the pack still holds exactly one rare.
 */
interface RarityCountBooster : BoosterStrategy {
    /** Number of dedicated rare-or-mythic slots. */
    val rareSlots: Int

    /** This booster with one rare-or-mythic slot traded for one more uncommon slot. */
    fun withRareSlotAsUncommon(): RarityCountBooster
}
