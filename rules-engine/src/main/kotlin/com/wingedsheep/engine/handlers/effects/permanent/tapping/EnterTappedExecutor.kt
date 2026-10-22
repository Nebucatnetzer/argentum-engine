package com.wingedsheep.engine.handlers.effects.permanent.tapping

import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.PredicateEvaluator
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.handlers.effects.EnterUntappedReplacements
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.sdk.scripting.effects.EnterTappedEffect
import kotlin.reflect.KClass

/**
 * Executor for [EnterTappedEffect] — the "this permanent enters tapped" rider of an
 * `OnEnterRun` as-enters replacement.
 *
 * Marks the entering permanent tapped the way every other tapped-entry path does (a bare
 * [TappedComponent], no `TappedEvent` — it is put onto the battlefield tapped, it doesn't become
 * tapped), unless an [EnterUntappedReplacements] effect (The Wandering Minstrel, Spelunking)
 * applies to it, which wins (CR 616.1).
 */
class EnterTappedExecutor(
    private val predicateEvaluator: PredicateEvaluator,
) : EffectExecutor<EnterTappedEffect> {

    override val effectType: KClass<EnterTappedEffect> = EnterTappedEffect::class

    override fun execute(
        state: GameState,
        effect: EnterTappedEffect,
        context: EffectContext
    ): EffectResult {
        val entityId = context.sourceId ?: return EffectResult.success(state)
        if (entityId !in state.getBattlefield()) return EffectResult.success(state)
        if (EnterUntappedReplacements.entersUntapped(state, entityId, context.controllerId, predicateEvaluator)) {
            return EffectResult.success(state)
        }
        return EffectResult.success(state.updateEntity(entityId) { c -> c.with(TappedComponent) })
    }
}
