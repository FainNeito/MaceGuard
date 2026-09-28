# Warzone combat and batch modifier requirements

This SPEAR slice covers the local testing JAR requested on 2026-09-28. `docs/WARZONE.md` remains the broader gameplay reference. Production deployment and a pull request are outside this slice.

## EARS requirements

- **MG-WZ-01:** When an authorized player opens modifier management without an ID, the GUI shall let them select and deselect multiple enabled modifiers across pages before applying an override.
- **MG-WZ-02:** While the draft is being edited, the live modifier set shall remain unchanged. Before applying, the GUI shall display additions and removals, validate conflicts and count limits, require the existing permission and kit-detachment checks, and require a duration choice.
- **MG-WZ-03:** When CombatLogX tags a player inside an effective `warzonerotator-combat-zone: allow` region, MaceGuard shall latch Warzone combat until CombatLogX untag, death, quit, dependency loss, or runtime replacement. A CombatLogX or MaceGuard bypass shall not receive restrictions or a bar.
- **MG-WZ-04:** While Warzone combat is latched, the optional boss bar shall use CombatLogX's remaining and maximum timer values, a red vanilla fill, and `#EE4B00` title text by default. It shall disappear when the latch ends. The red fill, title, text color, and visibility shall be configurable.
- **MG-WZ-04A:** Before showing the Warzone combat bar, MaceGuard shall suppress that player's CombatLogX Boss Bar expansion display without permanently changing the player's preference. When Warzone combat ends, MaceGuard shall hide its bar before restoring CombatLogX's display. If suppression cannot be confirmed, MaceGuard shall not show its bar. Players shall never have both combat bars shown by the two plugins at once.
- **MG-WZ-05:** During ordinary combat without a Warzone latch, MaceGuard shall allow Elytra starts and boosts, Riptide, and non-pearl teleports. When a Warzone latch exists, the configured Warzone movement and teleport restrictions shall apply inside the Warzone and, when carryover is enabled, after exit until CombatLogX untag. Ender Pearl stasis remains a separate decision.
- **MG-WZ-06:** Existing schema-7 operator files without the new `combat.warzone-tag` section shall load with safe defaults. Invalid bar colors or unknown keys shall be rejected without replacing the active runtime.

## Boundaries

Vanilla clients accept only preset boss-bar fill colors; `#EE4B00` is the title color. CombatLogX Cheat Prevention must be configured separately to stop globally blocking Elytra, Riptide, and teleports. A local build does not establish live Paper/Leaf, WorldGuard, CombatLogX, or client behavior.
