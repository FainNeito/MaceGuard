# Local automatic Warzone export

Authoritative base: main 38e4255cf1940c2397da5a4e2cecc6c56498c8b4 (6.1.8).
Local-only work: no PR, push, server mutation or activation.

Requirements: capture effective MaceGuard runtime rules and manual overrides on
the server thread, then send bounded public JSON off-thread to the website's
signed community ingest. Respect paused schedules and indefinite overrides.
Never publish world/region IDs, coordinates, player opponents, wagers or secrets.
UTC Monday weekly match wins use only an optional public WarzoneDuels export
method; absent/invalid exports remain unavailable, never zero or invented kills.
Signing uses a secret supplied through an environment variable, not Git or chat.
HTTPS only, no credentials/query/fragment/redirects, timeout, bounded leaders,
one pending delivery and cancellation on shutdown. Default is disabled.

Production remains on WarzoneDuels 1.0.4 while standalone canonical main/pin
describe 1.0.1. This export must not authorize downgrading that plugin. Reconcile
the exact runtime lineage and counted-team-win semantics before enabling its
optional weekly-wins adapter. The existing captured preview is not automatic.

SPEAR requirements/proof/implementation/verification are manual because no
project-local EARS/state helpers were found. Test artifacts remain local and
unmerged; canonical source/pins and live acceptance are separate requirements.
