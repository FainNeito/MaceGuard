# SPEAR tasks: Warzone combat and batch modifiers

| ID | Stage | Work | State |
|---|---|---|---|
| MG-WZ-01/02 | spec | Record batch GUI behavior, permissions, and one-apply boundary | Done |
| MG-WZ-03/06 | spec | Record Warzone latch, CombatLogX ownership, and config compatibility | Done |
| MG-WZ-PROVE | prove | Add focused regression evidence for scope, bar, config, and GUI security | Done locally; batch draft needs live UI acceptance |
| MG-WZ-ENGINE | engine | Implement batch GUI, scoped combat policy, and bar | Done locally |
| MG-WZ-ARCH | arch | Review lifecycle, reload, external plugin configuration, and GUI session ownership | Done locally; server integration remains unverified |
| MG-WZ-REFINE | refine | Run focused checks and full clean build; package a versioned test JAR with checksum | Done locally; staging acceptance pending |
| MG-WZ-04A | spec | Specify mutually exclusive CombatLogX and Warzone combat bar ownership | Done |
| MG-WZ-04A-PROVE | prove | Cover suppression before show, failed suppression, and restoration after hide | Done locally |
| MG-WZ-04A-ENGINE | engine | Add optional CombatLogX Boss Bar handoff through the direct gateway | Done locally |
| MG-WZ-04A-ARCH | arch | Review dependency reload, player preference, and bar cleanup lifecycle | Done locally; deployed expansion check pending |
| MG-WZ-04A-REFINE | refine | Run focused and full build, then package an updated test JAR | Done locally; staging acceptance pending |

The implementation predates this SPEAR record. Existing tests are not claimed as historical red/green evidence. No PR or server deployment is authorized by this task.
