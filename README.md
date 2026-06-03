# Ping to Map: Xaero's Minimap & Ping Wheel Addon

> Ping-Wheel で打った ping を、Xaero's Minimap に**一時 waypoint**として副次的に立てる。プロンプトなし、Ping-Wheel のピンと同時に自動消滅、チームメイト全員に見える。

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Modrinth](https://img.shields.io/badge/Modrinth-ping--to--map--xaeros-00AF5C)](https://modrinth.com/mod/ping-to-map-xaeros)
[![CurseForge](https://img.shields.io/badge/CurseForge-ping--to--map--xaeros-F16436)](https://www.curseforge.com/minecraft/mc-mods/ping-to-map-xaeros)

---

## Supported Loaders / Versions

| Minecraft | NeoForge | Forge | Fabric |
|---|:---:|:---:|:---:|
| 1.21.1 | ✅ | ✅ | ✅ |
| 1.20.1 | — | ✅ | ✅ |

NeoForge は 1.20.1 リリース無し。

---

## なにをするやつ?

Ping-Wheel で「あそこ来て！」って ping を打っても、**地図上には載らない**から大きい施設だと結局見つけにくい。
このアドオン MOD は **ping した瞬間に Xaero's Minimap に一時 waypoint を立てる**。プロンプトも編集画面も出ない、副次的に静かに waypoint が現れる。Ping-Wheel のピン表示時間に同期して消えるから（既定）地図が散らからない。

- 📍 **Ping した瞬間に Xaero's に一時 waypoint** — プロンプト無し、UI 介入ゼロ
- 🕒 **ピンと同時に自動消滅** — 既定で Ping-Wheel の pingDuration に同期（Config で固定 1〜600 秒にも調整可）
- 🤝 **マルチプレイで自然共有** — Ping-Wheel が S2C で全クライアントに ping を配信、本 MOD は各クライアントで個別に一時 waypoint を立てる
- 🌐 **クライアント MOD のみ** — サーバ側に入れる必要なし
- 🛟 **Xaero's 不在でも crash しない** — 何も起きないだけ (silent fail)

---

## How it works

Ping-Wheel は公式 API を持たないため、**Mixin** で `nx.pingwheel.common.core.PingManager#acceptPingPacket` の HEAD に `@Inject` する。Ping-Wheel は packet を netty thread で受信して `Minecraft.execute(...)` で main thread に処理を譲るので、本 MOD も同じパターンで `Minecraft.getInstance().execute(() -> {...})` で main thread にマーシャルしてから動く。

main thread に乗ったら **Reflection** で Xaero's の internal API を直叩き:
```
xaero.hud.minimap.BuiltInHudModules.MINIMAP
  .getCurrentSession()
  .getWaypointSession()
  .getSession().getWorldManager().getCurrentWorld()
  .getCurrentWaypointSet()
  → Waypoint オブジェクトを直接構築して add
```

寿命管理は自前 (`PingWaypointTracker`)。`System.nanoTime()` ベースで寿命（既定で Ping-Wheel の pingDuration に同期）経過後に同 Reflection 経路で `WaypointSet.remove(wp)` を呼ぶ。同じプレイヤーが連続 ping した時は古い waypoint を即削除して新しいだけ残す。

Xaero's に何の参照も持たない (compileOnly すらしない)。Reflection で全例外を catch するので Xaero's 不在 / API 不一致 / セッション未起動なら silent fail で何もしない。Ping-Wheel 本来の挙動は絶対に止めない (`@Inject(at=HEAD)` で `ci.cancel()` しない)。

---

## Installation

1. **NeoForge / Forge / Fabric** いずれかの 1.21.1 or 1.20.1 を導入
2. [Ping-Wheel](https://modrinth.com/mod/ping-wheel) を導入（必須）
3. [Xaero's Minimap](https://modrinth.com/mod/xaeros-minimap) および/または [Xaero's World Map](https://modrinth.com/mod/xaeros-world-map) をクライアントに導入（推奨）
4. **Fabric** のみ: [Forge Config API Port](https://modrinth.com/mod/forge-config-api-port) を追加導入
5. リリースページから自分のローダー/MC 用 `pingtomapxaeros-<version>.jar` を `mods/` に放り込む（**クライアントのみで OK**、サーバ不要）

---

## Configuration

`config/pingtomapxaeros-client.toml` または NeoForge / Forge の Mod Settings GUI:

| キー | 既定 | 説明 |
|---|---|---|
| `feature.enabled` | true | マスタースイッチ |
| `feature.registerOwnPings` | true | 自分の ping も waypoint 化するか (false ならチームメイトの ping のみ) |
| `appearance.waypointLifetimeSec` | 30 | waypoint が地図に残る秒数 (1〜600) |

---

## Compatibility

| MOD | サポート | 備考 |
|---|---|---|
| **Ping-Wheel** | required | Mixin ターゲット、必須 |
| **Xaero's Minimap** | optional (CLIENT のみ) | waypoint 登録のターゲット |
| **Xaero's World Map** | optional (CLIENT のみ) | Minimap と同じ waypoint store を共有 |
| Voice Chat 系 (Plasmo Voice 等) | 影響なし | Ping-Wheel が両立してるので一緒に動く |
| JourneyMap | 本ビルドの対象外 | JourneyMap 版の姉妹 MOD を使ってください |

---

## FAQ

**Q. サーバ側にも MOD 入れる必要ある？**
A. いいえ、**クライアントのみ**で動作します。Ping-Wheel 自体はサーバ要だが、本 MOD はクライアントで完結。

**Q. ping したけど waypoint が出ない！**
A. 以下を確認:
1. Ping-Wheel と Xaero's Minimap (or World Map) が両方インストールされてるか
2. `feature.enabled = true` か
3. ping 距離が Ping-Wheel 設定 (`pingDistance`) の範囲内か (デフォルト 2048 ブロック)
4. 自分の ping を表示したい場合 `feature.registerOwnPings = true`

**Q. Compass to Map と一緒に使える？**
A. もちろん。役割が違うので衝突しません (compass 系 = 構造物・バイオーム発見、本 MOD = チーム ping)。

**Q. JourneyMap 版はないの？**
A. JourneyMap 版の姉妹 MOD を別途公開。本 MOD は Xaero's に特化。

**Q. waypoint が消えない / 永続化したい**
A. 仕様で「ping = 一時的な位置共有」を再現してます。寿命は 600 秒まで延ばせるが永続化は意図的に外してます (チーム coop で waypoint が溜まりすぎる問題を避けるため)。

---

## License

[MIT License](LICENSE)

---

## Credits

- Author: KURONAMI
- Assist: Claude (Anthropic)
- Built on:
  - [Ping-Wheel](https://modrinth.com/mod/ping-wheel) by LukenSkyne
  - [Xaero's Minimap](https://modrinth.com/mod/xaeros-minimap) by xaero96
  - [Xaero's World Map](https://modrinth.com/mod/xaeros-world-map) by xaero96
- Sister mod: [Compass to Map: Xaero's edition](https://github.com/KURONAMI333/compass-to-map-xaeros)
