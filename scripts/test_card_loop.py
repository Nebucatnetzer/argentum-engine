"""Launcher integration checks without model sessions, GitHub calls, or real sleeps.

Run: python3 -m unittest scripts.test_card_loop
"""
import os
from pathlib import Path
import shutil
import subprocess
import tempfile
import unittest


class CardLoopTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.bin = self.root / "bin"
        self.bin.mkdir()
        (self.root / ".git").mkdir()
        (self.root / "Dina Deck.txt").write_text("1 Ad Nauseam\n")
        shutil.copy(Path(__file__).with_name("card-loop"), self.bin / "card-loop")
        shutil.copy(Path(__file__).with_name("loop-lib.sh"), self.bin / "loop-lib.sh")
        self.stub("git", 'echo "$TEST_ROOT/.git"')
        self.stub("gh", 'echo gh >> "$TEST_ROOT/gh-calls"; exit 0')
        # The session reaper ticks every LOOP_REAP_TICK seconds; those aren't the loop's waits.
        self.stub("sleep", '[ "$1" = "$LOOP_REAP_TICK" ] && exit 0; '
                           'echo "$1" >> "$TEST_ROOT/sleeps"; touch "$TEST_ROOT/.claude/loop-runs/cards-dina-deck.stop"')
        self.stub("codex", """
printf '%s\\n' "$@" > "$TEST_ROOT/args"
while [ "$#" -gt 0 ]; do
  if [ "$1" = --output-last-message ]; then shift; output=$1; fi
  shift
done
if [ "$MODE" = limit ]; then
  echo '{"type":"error","message":"usage limit reached"}'
  exit 1
fi
printf '%s\\n' "$VERDICT" > "$output"
echo '{"type":"item.completed","item":{"type":"agent_message","text":"Summary: checked"}}'
""")
        self.stub("claude", """
printf '%s\\n' "$@" > "$TEST_ROOT/args"
echo "{\\"type\\":\\"result\\",\\"result\\":\\"$VERDICT\\",\\"num_turns\\":1,\\"duration_ms\\":1000}"
""")

    def stub(self, name, body):
        path = self.bin / name
        path.write_text("#!/usr/bin/env bash\n" + body + "\n")
        path.chmod(0o755)

    def run_loop(self, *args, model="", mode="", verdict="CARDS_COMPLETE"):
        env = dict(os.environ, PATH=f"{self.bin}:{os.environ['PATH']}",
                   TEST_ROOT=str(self.root), HOME=str(self.root), LOOP_REAP_TICK="1", MODEL=model, MODE=mode, VERDICT=verdict)
        return subprocess.run(["bash", str(self.bin / "card-loop"), *args],
                              cwd=self.root, env=env, capture_output=True, text=True, timeout=10)

    def prompt(self):
        return "\n".join((self.root / "args").read_text().splitlines())

    def test_help_and_models_do_not_launch_sessions(self):
        for option in ("--help", "-h", "--models"):
            with self.subTest(option=option):
                result = self.run_loop(option)
                self.assertEqual(result.returncode, 0, result.stderr)
                self.assertIn("astra", result.stdout)
                self.assertFalse((self.root / "args").exists())

    def test_list_is_required_and_must_exist(self):
        for args in ((), ("missing.txt",), ("--local",)):
            with self.subTest(args=args):
                result = self.run_loop(*args)
                self.assertEqual(result.returncode, 2)
                self.assertFalse((self.root / "args").exists())

    def test_default_uses_claude_and_pr_mode(self):
        result = self.run_loop("Dina Deck.txt")
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        self.assertIn("a PR per unit, harness claude, model claude-opus-5-5", result.stdout)
        prompt = self.prompt()
        self.assertIn("startswith(\"loop-cards-dina-deck-\")", prompt)
        self.assertIn("PR LIFECYCLE", prompt)
        self.assertIn("card-list-status '", prompt)
        self.assertIn("/Dina Deck.txt' --out", prompt)

    def test_local_mode_with_codex_model_never_uses_prs(self):
        result = self.run_loop("Dina Deck.txt", "codex:GPT-6-Sol", "--local")
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        args = (self.root / "args").read_text().splitlines()
        self.assertEqual(args[:3], ["exec", "--model", "gpt-6-sol"])
        prompt = self.prompt()
        self.assertIn("LOCAL MODE", prompt)
        self.assertIn("loop-cards-dina-deck-local", prompt)
        self.assertNotIn("PR LIFECYCLE", prompt)
        self.assertFalse((self.root / "gh-calls").exists())
        self.assertIn("push it and open one PR", result.stdout)

    def test_local_flag_may_come_first(self):
        result = self.run_loop("--local", "Dina Deck.txt", "astra")
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        self.assertIn("local commits on loop-cards-dina-deck-local", result.stdout)

    def test_claude_alias_resolves_driving_model(self):
        result = self.run_loop("Dina Deck.txt", "sonnet", "--local")
        self.assertEqual(result.returncode, 0, result.stdout)
        self.assertIn("resolve the actual driving", self.prompt())

    def test_stuck_stops(self):
        result = self.run_loop("Dina Deck.txt", "astra", verdict="STUCK G3 commander tax")
        self.assertEqual(result.returncode, 1, result.stdout)
        self.assertIn("cards-dina-deck-cards.md", result.stdout)

    def test_usage_limit_waits_then_honors_stop_file(self):
        result = self.run_loop("Dina Deck.txt", "astra", "--local", mode="limit")
        self.assertEqual(result.returncode, 0, result.stdout)
        self.assertEqual((self.root / "sleeps").read_text(), "900\n")
        self.assertIn("stop file found", result.stdout)


if __name__ == "__main__":
    unittest.main()
