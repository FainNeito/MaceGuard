# Warzone combat and batch modifier verification

## Local evidence

Source branch: `codex/warzone-combat-batch-modifiers`, based on `9ea6ad5` (local follow-up tests and SPEAR records added afterward). The implementation predates this SPEAR record, so existing tests are not historical red/green proof.

- Remote Desktop Commander device `Entity` ran Maven 3.9.11 with JDK 22 and Java release 21. Maven enforcer was skipped because the installed JDK is outside the POM's `[21,22)` build contract; JDK 21 verification remains open.
- Focused run: `CombatScopeServiceTest`, `CombatElytraPolicyTest`, `WarzoneConfigLoaderTest`, `WarzoneControlConfigLoaderTest`, and `WarzoneGuiSecurityTest`: 52 passed. A new `WarzoneCombatBarTest` passed in the full run.
- Full `clean verify`: 547 tests passed, 0 failures, 0 errors, 0 skipped; shaded JAR built successfully. The first runs exposed CRLF-sensitive test fixtures and a stale global-combat Elytra expectation; both were updated and the final clean run passed.
- Test artifact: `C:\Users\p_ric\OneDrive\Documents\ChatGPT\Chapter 2\artifacts\maceguard-6.1.8-warzone-combat-batch-test.1.jar` (994,619 bytes). SHA-256: `CECE52BB0E3DBDAAE46527CA93AF5D93FFCC447AE2D224370AEDF26453451DC7`.
- EARS requirements MG-WZ-01 through MG-WZ-06 were reviewed manually; this repository has no project-local SPEAR validator. No live Paper/Leaf or client acceptance was performed.

## Staging acceptance still required

- With CombatLogX tagged inside the effective combat-zone flag, confirm the Warzone bar appears with red fill and orange title, counts down on the CombatLogX timer, survives region exit, and disappears on untag, death, quit, dependency disable, and reload.
- Confirm ordinary combat outside the flag permits Elytra starts, boosts, Riptide, `/tpa`, `/home`, `/spawn`, and portals after the documented CombatLogX settings are applied. Confirm Warzone combat blocks configured actions after exit while carryover is enabled.
- Confirm Ender Pearl teleports retain the separate age and stasis policy, and that another plugin's canceled event remains canceled.
- In the modifier GUI, select and deselect several items across pages, preview additions/removals, cancel without changing the live set, then apply once with each supported duration. Exercise conflicts, disabled modifiers, permission changes, count limits, kit detachment, and clear-all.
- Compare Java and Bedrock clients on the target Paper/Leaf build. A local test JAR remains a staging artifact until these checks pass.
