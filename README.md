# Ping to Map: Xaero's Edition

Drops a temporary Xaero's Minimap waypoint the instant a teammate (or you) pings a spot with Ping-Wheel — no prompt, no edit screen, and by default it disappears together with the ping.

You ping "come here" with Ping-Wheel, but it never shows on the map, so on big builds people still can't find the spot. This addon puts a waypoint on Xaero's the moment a ping happens, then clears it so the map stays clean.

**Features**

- A temporary waypoint on every ping — no clicks, no edit screen
- By default the waypoint expires in sync with the Ping-Wheel ping — they vanish together (or set a fixed 1–600 s, or make it permanent)
- Team-share — Ping-Wheel broadcasts pings to teammates, and each client creates the waypoint locally
- No items or blocks; it never interrupts Ping-Wheel's own behaviour, and won't crash if Xaero's is absent (it reaches Xaero's by reflection that no-ops on absence or an API change)

**Config** (`config/pingtomapxaeros-client.toml`, or the Mod Config GUI)

- `appearance.syncWithPingWheel` — waypoint vanishes together with the ping, its lifetime following the ping's (default on)
- `appearance.waypointLifetimeSec` — fixed lifetime in seconds, used only when sync is off (-1 = permanent)
- `feature.registerOwnPings` — also waypoint your own pings (set false for teammates' pings only)

**Dependencies**

- [Ping-Wheel](https://modrinth.com/mod/ping-wheel) — required
- [Xaero's Minimap](https://modrinth.com/mod/xaeros-minimap) and/or [Xaero's World Map](https://modrinth.com/mod/xaeros-world-map) (client) — the waypoint target
- Fabric only: [Forge Config API Port](https://modrinth.com/mod/forge-config-api-port)

Companion mod: Compass to Map: Xaero's edition.

All Rights Reserved. Modpack inclusion is allowed without permission or credit. Source: https://github.com/KURONAMI333/ping-to-map-xaeros

## Published builds

The table lists files attached to the public GitHub release; choose the file for your Minecraft version and loader.

| Minecraft | NeoForge | Forge | Fabric |
|---|:---:|:---:|:---:|
| 1.20.1 | — | Yes | Yes |
| 1.21.1 | Yes | Yes | Yes |
| 1.21.4 | Yes | — | Yes |
| 1.21.8 | Yes | — | Yes |
| 1.21.11 | Yes | — | Yes |
| 26.1.2 | Yes | — | Yes |
| 26.2 | Yes | — | Yes |

## Downloads and support

Downloads: [CurseForge](https://www.curseforge.com/minecraft/mc-mods/ping-to-map-xaeros) · [GitHub Releases](https://github.com/KURONAMI333/ping-to-map-xaeros/releases/tag/v1.1.1).

For bugs and questions, comment on the [CurseForge page](https://www.curseforge.com/minecraft/mc-mods/ping-to-map-xaeros) or DM [@kuronami333 on X](https://x.com/kuronami333).

[Source](https://github.com/KURONAMI333/ping-to-map-xaeros) · [License](LICENSE)
