# AI-native workflow for tms-demo-autotests

**The one-liner:** talk to the **orchestrator**. Describe the task in plain words; it plans,
implements, tests, reviews, and opens the PR by delegating to a team of specialist agents.

## How to start a run

This repo has no work tracker — you give the orchestrator a free-text task, optionally with a note
on what to emphasize:

> Use the orchestrator: add a test that filtering by "In Progress" hides completed tasks.

> Use the orchestrator: the assign dialog got a new "Notify user" checkbox in the mock app —
> cover it. Keep it to a single PR.

> Use the orchestrator: CI is red on PR #12 — find out why and fix it.

## What happens

1. The orchestrator restates the task as acceptance criteria and checks `.claude/TASKS.md` plus
   open branches/PRs so already-done work is never redone.
2. **planner** (read-only) writes a file-by-file plan.
3. It proposes a delivery shape — several small PRs or one PR — and you pick.
4. **test-engineer** implements (the only agent that edits Kotlin/build/webapp files) →
   **triage-analyst** runs the suite and classifies any failures → **reviewer** (read-only) gates
   the diff → **git-pr-agent** branches, commits, and opens the PR (the only agent doing git).
5. CI (`.github/workflows/test.yml`) runs the suite on the PR; triage-analyst reads red runs.

## Approval gates

Before anything outward-facing (commit, push, PR), the orchestrator pauses and asks. Reply:

- **`go`** — approve that one step;
- **`autopilot`** — relax the optional gates for the rest of the run (force-push/deletions always
  require explicit instruction regardless).

## The roster

| Agent | Does | Exclusively owns |
|---|---|---|
| orchestrator | Delegates, verifies handoffs, keeps the log | `.claude/TASKS.md` |
| planner | Read-only implementation plans | — |
| test-engineer | Writes code | `src/**`, `build.gradle.kts`, `settings.gradle.kts` |
| reviewer | Read-only diff review before PR | — |
| triage-analyst | Runs tests, classifies failures (app bug / test bug / infra) | test execution |
| git-pr-agent | Branch, commit, push, PR | git + GitHub writes |

**Exclusive-ownership rule:** each side-effect surface has exactly one writer. If an agent asks to
touch someone else's surface, that's a bug in the run — say no.

## Where things live

- Committed: `.claude/agents/*.md` (this team), `CLAUDE.md` (house rules), `.github/workflows/`.
- Gitignored, local-only: `.mcp.json` (holds your GitHub PAT — never commit it),
  `.claude/TASKS.md` (run log), `.claude/settings.local.json`.
- After editing `.mcp.json`: restart Claude Code and run `/mcp` — servers load only at startup.

## Ground rules (short version — `CLAUDE.md` is authoritative)

- House style is enforced: fluent page objects, `allureStep()` wrapping, role-based locators, full Allure
  annotations. The reviewer will bounce diffs that don't conform.
- A failing test is evidence, not an obstacle: it's classified as an app bug (reported) or a test
  bug (fixed properly). Nobody weakens an assertion, bumps a timeout, or deletes a test to get green.
- Nothing is faked: every "done" comes with test output, a diff, or a PR URL.
