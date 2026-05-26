# Ping to Map: Xaero's edition — ROADMAP

## ✅ Phase 1: コア機能 (v0.1 MVP)

- [x] Ping-Wheel の `nx.pingwheel.common.core.PingManager#acceptPingPacket` を Mixin で `@Inject(at = @At("HEAD"))`
- [x] netty thread から `Minecraft.getInstance().execute(...)` で main thread にマーシャル (threading safe)
- [x] Reflection で Xaero internal API (`BuiltInHudModules.MINIMAP` → `getWaypointSession` → `WaypointSet`) を直叩き
- [x] Waypoint オブジェクトを直接構築して `WaypointSet.add(wp, true)` で先頭挿入
- [x] `System.nanoTime()` ベースの寿命管理 (時計ジャンプ耐性)、30 秒後に Reflection 経由で remove
- [x] 同一プレイヤー連続 ping で古い waypoint を即削除
- [x] logout / dim 切替で全 waypoint クリア (リーク防止)
- [x] **try-catch で Ping-Wheel 本来の処理を絶対に止めない**
- [x] **silent fail**: Xaero 不在 / API 不一致は全 catch で吸収、crash しない
- [x] Config: `feature.enabled` / `feature.registerOwnPings` / `appearance.waypointLifetimeSec`
- [x] LICENSE (MIT) を jar 同梱
- [x] **マルチローダー対応**: NeoForge 1.21.1 / Forge 1.21.1 / Forge 1.20.1 / Fabric 1.21.1 / Fabric 1.20.1

## 🔮 Phase 2: UX 改善

- [ ] **waypoint 名フォーマットのカスタム化** (Config で `%player%` `%coords%` 等のテンプレ)
- [ ] **チームカラーの反映** (vanilla scoreboard team の色を Xaero color index にマップ)
- [ ] **ping 距離フィルタ** (一定距離以上の ping は waypoint 化しない)
- [ ] **発信元プレイヤーごとの ON/OFF** (チームメイト全員は ON、特定プレイヤーだけ無視 等)
- [ ] **Reflection cache** (毎 ping ごとに `Class.forName` を呼ばない、起動時 lazy init)
- [x] ~~**silent fail の初回 1 回 LOGGER.warn** (公開後の issue triage コスト削減)~~ → **v0.1.1 で前倒し実装済** (`XaeroReflect#warnApiDriftOnce`、`isXaeroPresent` で Xaero 不在時は黙る、`AtomicBoolean` で once-only、5 catch 全てで呼出)

## 🚀 Phase 3: 公開・コミュニティ

- [ ] スクリーンショット / GIF（Ping → 即 waypoint シーン）
- [ ] Modrinth / CurseForge 公開
- [ ] Discord / Wiki

---

## 設計判断の記録

| 判断 | 理由 |
|---|---|
| Mixin (`@Inject(at=HEAD)`) | Ping-Wheel に公式 API なし。bytecode 注入が唯一の連携手段。HEAD で先頭割り込み、`ci.cancel()` しないので本来の処理を止めない |
| `Minecraft.execute` で main thread に再ディスパッチ | Ping-Wheel の `acceptPingPacket` は netty I/O thread で呼ばれる (Ping-Wheel 自身も末尾で `Minecraft.execute` してる)。Xaero state を触る前に main thread に乗せる必要あり |
| 全例外を握りつぶす | Ping-Wheel の処理を絶対に止めないため。エラーは silent fail (Xaero's への連携が失敗しても ping そのものは正常動作) |
| Xaero internal API を Reflection で直叩き | Xaero に公式 Java API なし、chat-share 経路は「プロンプト → ワンクリック」UX なので「副次的に立つ一時 waypoint」仕様には合わない。Reflection で内部 API を呼ぶしか手段がない |
| `System.nanoTime()` で寿命管理 | `System.currentTimeMillis` は NTP 時計ジャンプで影響を受ける。`nanoTime()` は monotonic で安全 |
| 単色 (Xaero PURPLE) | Xaero's の既存色と被らない識別マーカー。Phase 2 でチームカラー対応予定 |
| CLIENT 専用 (`dist = CLIENT`) | Ping-Wheel のクライアント側 packet handler をフックするのみ。サーバ側で動かす必要なし |
| MIT ライセンス | modpack 採用しやすい |
