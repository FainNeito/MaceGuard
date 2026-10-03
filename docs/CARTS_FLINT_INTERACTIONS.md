# Carts-enabled Warzone flint-and-steel repair

This change is stacked on `codex/warzone-cart-water-arrow-fixes` (upstream PR #44). It contains only the flint-and-steel repair and its regression tests, not the separate local water, Riptide, combat retag, or guild-vault changes.

While CARTS is active, grant WorldGuard's relevant fire-placement and flint-and-steel use delegates inside the effective Warzone without requiring its separate LIGHTER query to return false. Independent interaction/build checks otherwise can reject an operation even when LIGHTER already allows it.

The player, clicked block, and adjacent fire position must remain inside the effective Warzone. Spawn exclusions remain authoritative. Preserve prior Bukkit cancellations and other plugins' delegate vetoes. Do not grant ordinary block placement, chest/door access, normal TNT priming, or Nether Portal placement just because a player holds a lighter. Candle/campfire lighting gets a narrow modification exception. Existing owned-cart, fire-spread and burn protections remain unchanged.

This repair does not automate changes to WorldGuard's LIGHTER flag. That separate proposed feature has not been implemented.

Nine new routing tests exercise LIGHTER already allowed, carts disabled, player outside, target crossing into Spawn/outside, cancellation, left-click, candle/campfire modification, non-fire placement, and protected chest/door/TNT interaction. Paper's Material.isInteractable classification requires its live server registry, so these routing tests substitute only that classification seam.

Player acceptance remains required on Paper/Leaf with WorldGuard: enable CARTS, light fire on a block and ignite an owned player-placed TNT minecart; verify carts-disabled and protected Spawn behavior; confirm fire cannot spread/burn map blocks and unrelated protection cancellations remain respected. No production upload, reload, restart, or configuration change is part of this PR.

## Verification on the exact stacked branch

- Focused Maven verify: 25 ExplosiveControlListenerTest tests passed, including nine new regressions; shaded JAR packaged successfully.
- Full Maven verify: 551 tests, 11 failures, zero errors. Failures are in the unchanged ConfigLoaderTest, WarzoneConfigLoaderTest and WarzoneControlConfigLoaderTest classes, outside this patch. Their baseline failure status has not been independently reproduced without this patch. Do not treat this as a clean full-suite run.
- Local toolchain: JDK 22, compiler release 21; Java-version enforcer explicitly skipped because JDK 21 is unavailable locally.
- The larger local test.8 build passed 583 tests with eight CombatLogX fixture tests excluded, but includes other work not in this focused PR and is not the same artifact.
