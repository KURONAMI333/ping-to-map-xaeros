# Ping to Map: Xaero's Minimap & Ping Wheel Addon

> Drop a temporary Xaero's Minimap waypoint the instant a teammate (or you) pings a spot with Ping-Wheel. No prompt, no edit screen — the waypoint just appears, then auto-expires after 30 seconds.

You ping "come here!" with Ping-Wheel, but it never shows on the **map** — so for big builds people still can't find the spot. This addon makes every ping silently create a temporary waypoint on Xaero's Minimap, with zero UI interruption. The waypoint auto-expires after 30 seconds so the map stays clean.

- 📍 **Temporary waypoint on every ping** — no prompt, no edit screen, no clicks
- 🕒 **Auto-expires in 30 s** (configurable 1–600 s)
- 🤝 **Multiplayer team-share** — Ping-Wheel broadcasts pings to all teammates server-side; this addon runs on each client independently and creates the waypoint locally
- 🌐 **Client-side only** — no server install needed
- 💡 **Pure addon** — Ping-Wheel + Xaero's Minimap (or World Map) you already have; no items, no blocks
- 🛟 **Safe without Xaero's** — silent fail if Xaero's isn't installed; no crash

## How it works

Ping-Wheel has no public API, so this mod uses a Mixin (`@Inject(at = @At("HEAD"))`) on `nx.pingwheel.common.core.PingManager.acceptPingPacket`. Ping-Wheel itself dispatches its packet work through `Minecraft.execute(...)`, so we do the same: marshal everything onto the main client thread, then **reflect into Xaero's internal API** to construct and add a `Waypoint` directly to the current `WaypointSet`. After the configured lifetime elapses, a self-managed `System.nanoTime()`-based tracker removes it via the same reflection chain.

Ping-Wheel's own behaviour is never interrupted — `@Inject` at HEAD only, never `cancel`. All reflection paths swallow exceptions silently, so an Xaero update / API rename / Xaero absence just yields a no-op (no crash, no console spam).

## Supported loaders / versions

| Minecraft | NeoForge | Forge | Fabric |
|---|:---:|:---:|:---:|
| 1.21.1 | ✅ | ✅ | ✅ |
| 1.20.1 | — | ✅ | ✅ |

NeoForge has no 1.20.1 release.

## Compatibility

| Mod | Support | Note |
|---|---|---|
| **Ping-Wheel** | required | Mixin target |
| **Xaero's Minimap** | optional (CLIENT only) | Waypoint target |
| **Xaero's World Map** | optional (CLIENT only) | Shares the same waypoint store with Minimap |
| Voice Chat mods (Plasmo, etc.) | unaffected | Ping-Wheel coexists with them, so does this |
| JourneyMap | not targeted by this build | Use the JourneyMap sister mod instead |

## Install

1. Install **NeoForge / Forge / Fabric** for 1.21.1 or 1.20.1
2. Install [Ping-Wheel](https://modrinth.com/mod/ping-wheel) (required)
3. Install [Xaero's Minimap](https://modrinth.com/mod/xaeros-minimap) and/or [Xaero's World Map](https://modrinth.com/mod/xaeros-world-map) on the **client** (recommended)
4. **Fabric only:** add [Forge Config API Port](https://modrinth.com/mod/forge-config-api-port)
5. Drop `pingtomapxaeros-<version>.jar` for your loader/MC from the releases page into `mods/` (**client only** — no server install needed)

## License

MIT — modpack use, modification and redistribution OK, credit not required (welcome).

Author: KURONAMI · Built on Ping-Wheel / Xaero's Minimap / Xaero's World Map. Sister mod: Compass to Map: Xaero's edition.
