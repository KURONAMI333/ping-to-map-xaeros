# Ping to Map: Xaero's edition

Drops a temporary Xaero's Minimap waypoint the instant a teammate (or you) pings a spot with Ping-Wheel — no prompt, no edit screen, and it auto-expires after 30 seconds.

You ping "come here" with Ping-Wheel, but it never shows on the map, so on big builds people still can't find the spot. This addon puts a waypoint on Xaero's the moment a ping happens, then clears it so the map stays clean.

**Features**

- A temporary waypoint on every ping — no clicks, no edit screen
- Auto-expires after 30 s (configurable 1–600 s)
- Team-share — Ping-Wheel broadcasts pings to teammates, and each client creates the waypoint locally
- No items or blocks; it never interrupts Ping-Wheel's own behaviour, and won't crash if Xaero's is absent (it reaches Xaero's by reflection that no-ops on absence or an API change)

**Dependencies**

- [Ping-Wheel](https://modrinth.com/mod/ping-wheel) — required
- [Xaero's Minimap](https://modrinth.com/mod/xaeros-minimap) and/or [Xaero's World Map](https://modrinth.com/mod/xaeros-world-map) (client) — the waypoint target
- Fabric only: [Forge Config API Port](https://modrinth.com/mod/forge-config-api-port)

Client-side only — no server install needed. Companion mod: Compass to Map: Xaero's edition.

Free to use in any modpack. Source and issues: https://github.com/KURONAMI333/ping-to-map-xaeros
