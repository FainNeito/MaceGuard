# Warzone combat and batch modifier implementation

- `WarzoneGuiManager` owns the per-player draft. `RotationManager.previewCustom` validates the composed set; `applyPrepared` changes the live set only after preview and duration confirmation.
- `CombatScopeService` owns a transient latch derived from CombatLogX and WorldGuard. `WarzoneCombatBar` reads the gateway's remaining and maximum seconds; the runtime reconciles and clears its owned bar.
- `CombatElytraPolicy` and `ItemRestrictionListener` apply movement and teleport rules only when the Warzone latch and configured location/carryover scope allow them. Ender Pearls continue through the existing stasis path.
- `WarzoneConfigLoader` supplies defaults for old operator files and validates new boss-bar color settings. The bundled `warzone.yml` documents these settings; `docs/DEPLOYMENT.md` records the separate CombatLogX Cheat Prevention changes needed for ordinary combat outside Warzone.
