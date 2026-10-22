package com.wingedsheep.engine.hygiene

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import java.io.File

/**
 * Guards the tap/untap event contract: a permanent that *becomes tapped or untapped while on the
 * battlefield* must go through the [com.wingedsheep.engine.core.tap] /
 * [com.wingedsheep.engine.core.untapOrConsumeStun] atoms, which own the transition guard
 * (CR 603.2f — no event on a non-transition) and the [com.wingedsheep.engine.core.TappedEvent] /
 * [com.wingedsheep.engine.core.UntappedEvent] emission.
 *
 * Open-coding `with(TappedComponent)` / `without<TappedComponent>()` is how the engine repeatedly
 * lost tap events — station creatures and the declare-attackers loop both shipped a silent tap that
 * "becomes tapped" triggers never saw, because the mutation and its event were two separate lines
 * and the event got forgotten. Routing through the atoms makes the event structurally impossible to
 * drop; this test makes the *bypass* structurally impossible to add.
 *
 * If this test fails, replace the raw mutation with `tap(state, id)` (emits [TappedEvent]) or
 * `untapOrConsumeStun(state, id)` (emits [UntappedEvent] / consumes a stun counter), and fold the
 * returned event(s) into the result.
 *
 * Exceptions — files that set/clear [com.wingedsheep.engine.state.components.battlefield.TappedComponent]
 * outside the atoms — are listed in [ALLOWED_FILES] with a justification. Every one is a
 * non-transition: permanents entering the battlefield tapped (taplands, tokens, sneak), phasing in
 * tapped, and the leave-the-battlefield untap cleanup, none of which is a "becomes tapped/untapped"
 * event (CR 603.2f / 603.6e), so none emits one.
 *
 * Regeneration used to be the one real tap on this list. It no longer is: CR 701.19a defines
 * "Regenerate [permanent]" as "…remove all damage marked on it and its controller taps it", and
 * `ZoneMovementUtils.applyRegenerationReplacement` now routes that through
 * [com.wingedsheep.engine.core.tap], so it emits a [TappedEvent] and stamps the first-time window
 * like any other tap. The file stays allowlisted only for its *untap* cleanup write.
 *
 * **Two known holes in the scan**, both currently benign: [RAW_TAP_PATTERN] matches
 * `.with(TappedComponent)` / `.without<TappedComponent>()` but not `components.add(TappedComponent)`
 * (used by the token executors), and [sourceRoot] covers only `rules-engine/src/main/kotlin`, so
 * `game-server`'s scenario builder is out of range. Every current hit of both kinds is a legitimate
 * enters-tapped site — but arguments that lean on this guard being exhaustive should say so.
 */
class TapEventEnforcementTest : FunSpec({

    test("battlefield tap/untap goes through the tap()/untapOrConsumeStun() atoms, not raw TappedComponent") {
        val offenders = findRawTapMutations(sourceRoot())
            .filterNot { it.relativePath in ALLOWED_FILES }

        offenders.map { "${it.relativePath}:${it.lineNumber}: ${it.line.trim()}" }.shouldBeEmpty()
    }
}) {
    companion object {
        /**
         * The tap/untap mutation pattern: `with(TappedComponent)` or `without<TappedComponent>()`.
         * Both the simple and fully-qualified component names are matched.
         */
        private val RAW_TAP_PATTERN =
            Regex("""\.with\(\s*(?:[\w.]+\.)?TappedComponent\s*\)|\.without<\s*(?:[\w.]+\.)?TappedComponent\s*>\s*\(\s*\)""")

        /**
         * Files permitted to touch [TappedComponent] directly. Each is a *non-transition* write —
         * a permanent enters/phases-in tapped, or is untapped as leave-the-battlefield cleanup —
         * which fires no "becomes tapped/untapped" event, so the atoms (which always emit) would be
         * wrong here. No entry grandfathers a real tap transition; see the class KDoc.
         */
        private val ALLOWED_FILES = setOf(
            // The atoms themselves.
            "com/wingedsheep/engine/core/TapHelpers.kt",
            // Permanent spells entering the battlefield tapped: the "enters tapped" replacement and
            // sneak's enters-tapped-and-attacking (CR 506.3a) — entering tapped is not a transition.
            "com/wingedsheep/engine/mechanics/stack/PermanentEntry.kt",
            // Tokens created tapped enter tapped; not a tap transition.
            "com/wingedsheep/engine/handlers/effects/token/CreatePredefinedTokenExecutor.kt",
            "com/wingedsheep/engine/handlers/effects/token/TokenCreationReplacementHelper.kt",
            "com/wingedsheep/engine/handlers/effects/token/TokenFromDefinition.kt",
            // Phasing in tapped restores the pre-phase tapped state (CR 702.26); not a transition.
            "com/wingedsheep/engine/handlers/effects/permanent/phasing/PhaseInLinkedToSourceExecutor.kt",
            // Battlefield entry with `tapped`/`tappedAndAttacking` options — enters tapped.
            "com/wingedsheep/engine/handlers/effects/ZoneTransitionService.kt",
            // A permanent leaving the battlefield is untapped as cleanup (CR 603.6e) — not a
            // transition. Regeneration's tap lives in this file too but is no longer a raw write:
            // it goes through `tap()` (CR 701.19a), so only the untap cleanup needs the exemption.
            "com/wingedsheep/engine/handlers/effects/ZoneMovementUtils.kt",
            // addToZone strips TappedComponent when a card leaves the battlefield — cleanup, no event.
            "com/wingedsheep/engine/state/GameState.kt",
            // A land played from a permission that forces it tapped enters tapped — not a transition.
            "com/wingedsheep/engine/handlers/actions/land/PlayLandHandler.kt",
            // The "if you don't, this land enters tapped" rider of an OnEnterRun as-enters
            // replacement (reveal lands, snarls) — the permanent enters tapped, not a transition.
            "com/wingedsheep/engine/handlers/effects/permanent/tapping/EnterTappedExecutor.kt",
            // Shock-land "pay life or enter tapped": declining to pay makes the land/permanent
            // *enter* tapped (CR 614 replacement on entry), which is not a tap transition.
            "com/wingedsheep/engine/handlers/continuations/ModalAndCloneContinuationResumer.kt",
            // Global "[filter] enter tapped/untapped" replacements applied to a token as it enters
            // (shared helper called by every token executor) — entering tapped/untapped on entry is
            // not a tap transition, so it emits no TappedEvent/UntappedEvent.
            "com/wingedsheep/engine/handlers/effects/EnterTappedReplacements.kt",
        )

        private data class RawTapMutation(
            val relativePath: String,
            val lineNumber: Int,
            val line: String
        )

        /**
         * Resolves the rules-engine `src/main/kotlin` directory regardless of which directory the
         * test runner is invoked from (module root under Gradle; repo root under some IDEs).
         */
        private fun sourceRoot(): File {
            val candidates = listOf(
                File("src/main/kotlin"),
                File("rules-engine/src/main/kotlin")
            )
            return candidates.firstOrNull { it.isDirectory }
                ?: error("Could not locate rules-engine/src/main/kotlin from ${File(".").absolutePath}")
        }

        private fun findRawTapMutations(sourceRoot: File): List<RawTapMutation> {
            val rootPath = sourceRoot.absolutePath.replace('\\', '/')
            val results = mutableListOf<RawTapMutation>()
            sourceRoot.walkTopDown()
                .filter { it.isFile && it.extension == "kt" }
                .forEach { file ->
                    val relative = file.absolutePath.replace('\\', '/').removePrefix("$rootPath/")
                    file.useLines { lines ->
                        lines.forEachIndexed { idx, raw ->
                            val code = stripLineComment(raw)
                            // Skip block-comment / KDoc continuation lines.
                            if (code.trimStart().startsWith("*")) return@forEachIndexed
                            if (RAW_TAP_PATTERN.containsMatchIn(code)) {
                                results += RawTapMutation(relative, idx + 1, raw)
                            }
                        }
                    }
                }
            return results
        }

        /** Drops a trailing `// …` line comment so commented-out examples don't trip the scan. */
        private fun stripLineComment(line: String): String {
            val idx = line.indexOf("//")
            return if (idx >= 0) line.substring(0, idx) else line
        }
    }
}
