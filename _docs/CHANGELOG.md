# Changelog

All notable changes to Ping to Map: Xaero's edition will be documented in this file.
Format: [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) — [Semver](https://semver.org/)

## [1.1.0] - 2026-06-03

### Changed
- 全 loader (NeoForge / Forge / Fabric × 1.21.1 / 1.20.1) で waypoint 寿命を **Ping-Wheel の pingDuration に同期** (`appearance.syncWithPingWheel`, 既定 ON)。ワールド内の ping と Xaero's マップ上の waypoint が同時に消える。pingDuration ≥ 60 の永続ピンにも追従。OFF にすると固定 `appearance.waypointLifetimeSec` を使用。

## [0.1.0] - YYYY-MM-DD (未公開 / MVP)

### Added
- **Xaero's Minimap 連携**: Ping-Wheel の ping 受信時に、Xaero internal API を Reflection 直叩きで一時 waypoint を構築・追加
- **Ping-Wheel フック**: `nx.pingwheel.common.core.PingManager#acceptPingPacket` を `@Inject(at = @At("HEAD"))` で Mixin
- **Threading 安全**: Ping-Wheel の packet 処理が netty thread 起点なので、本 Mixin も `Minecraft.getInstance().execute(...)` で main thread にマーシャル
- **寿命管理**: `PingWaypointTracker` が `System.nanoTime()` ベースで 30 秒後に自動 remove (時計ジャンプ耐性)
- **同一プレイヤー重複防止**: 連続 ping で古い waypoint を即削除して新規だけ残す (UUID 単位)
- **logout / dim 切替で全削除**: リーク防止
- **waypoint 表示名**: `<playerName>'s Ping` (`mc.player.getGameProfile().getName()` 統一)
- **ブランド紫の固定色** (Xaero {@code WaypointColor.PURPLE})
- **Config**: `feature.enabled` / `feature.registerOwnPings` / `appearance.waypointLifetimeSec` (1〜600 秒)
- **マルチローダー対応**: NeoForge 1.21.1 / Forge 1.21.1 / Forge 1.20.1 / Fabric 1.21.1 / Fabric 1.20.1

### Architecture notes
- Ping-Wheel は公式 API を持たないため Mixin での実装
- Xaero's は closed-source で公式 Java API なし。本 MOD は **Reflection で internal API を直叩き** (compileOnly すら使わない)
- 全 Reflection paths で `catch (Throwable)` → null/false。Xaero's 不在 / API 不一致 / セッション未起動なら silent fail で何もしない
- マルチプレイの自然な共有: Ping-Wheel が S2C で全クライアントに ping を配信、各クライアントで本 MOD が個別に一時 waypoint を立てる
- Ping-Wheel 本来の動作を絶対に止めない (`ci.cancel()` しない、try-catch で全例外握りつぶし)

### Compatibility
- Minecraft 1.21.1 (NeoForge / Forge / Fabric) または 1.20.1 (Forge / Fabric)
- **CLIENT 専用 MOD** (サーバ側に入れる必要なし)
- Required: Ping-Wheel by LukenSkyne (Mixin ターゲット)
- Optional (client): Xaero's Minimap / Xaero's World Map (未導入でも crash しない)
- Fabric は Forge Config API Port (FCAP) 経由で Config を扱う

Sister mod: Compass to Map: Xaero's edition (Explorer's Compass / Nature's Compass × Xaero's addon)
