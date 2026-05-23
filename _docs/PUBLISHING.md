# Publishing Checklist — Ping to Map: Xaero's edition

Modrinth / CurseForge への公開・更新手順。

現状: **v0.1.0 MVP**。NeoForge 1.21.1 / Forge 1.21.1 / Forge 1.20.1 / Fabric 1.21.1 / Fabric 1.20.1 の 5 jar 構成。
姉妹 MOD `ping-to-map` (JourneyMap 版) とは別プロジェクトとして公開する。

---

## 公開前チェックリスト

### コード・ビルド

- [ ] `mod_version` を全 5 サブプロジェクトの `gradle.properties` で更新（semver）
- [ ] 全 5 サブプロジェクトで `./gradlew clean build`
- [ ] 全 5 `build/libs/pingtomapxaeros-x.y.z.jar` のサイズ確認（compileOnly が混入してないこと）
- [ ] 全 5 jar に `LICENSE_pingtomapxaeros` が同梱されてること (`unzip -l <jar> | grep LICENSE`)
- [ ] `./gradlew runClient` で各 loader/MC で実機確認
  - [ ] Ping-Wheel で ping → Xaero's に紫の一時 waypoint が**プロンプト無しで即座に立つ**
  - [ ] 30 秒経過で自動消滅
  - [ ] 同一プレイヤー連続 ping → 古い waypoint が即消えて新規だけ残る
  - [ ] `feature.registerOwnPings = false` で自分の ping は無視される
  - [ ] `feature.enabled = false` で完全 OFF
  - [ ] Xaero's を入れずに起動 → 何も起きない (silent fail、crash なし)

### ドキュメント

- [ ] `README.md` の機能 / 設定 / 互換 / インストールが最新
- [ ] `LICENSE` 存在 (jar 同梱も build.gradle で処理済み)
- [ ] `_docs/CHANGELOG.md` 更新 (日付プレースホルダ確定)
- [ ] バージョン番号が各所一致（全 5 `gradle.properties`・mods.toml/fabric.mod.json・README）

### メタデータ

- [ ] `displayURL` / `issueTrackerURL` が `github.com/KURONAMI333/ping-to-map-xaeros`
- [ ] `description` が実装と整合 (古い "chat-share" 記述が残ってないこと)
- [ ] `authors=KURONAMI` / `license=MIT`

---

## ストア事実（説明文・タグの基準）

| 項目 | 値 |
|---|---|
| Project name | `Ping to Map: Xaero's Minimap & Ping Wheel Addon` |
| Slug | `ping-to-map-xaeros` |
| Mod ID | `pingtomapxaeros` / package `com.kuronami.pingtomapxaeros` |
| Loaders × MC | NeoForge 1.21.1 / Forge 1.21.1 / Forge 1.20.1 / Fabric 1.21.1 / Fabric 1.20.1 |
| Environment | **Client only**（サーバ不要） |
| 依存 | **Ping-Wheel（必須）** / Xaero's Minimap or World Map（任意・推奨、CLIENT のみ） / Fabric は FCAP も |
| 既知制限 | NeoForge 1.20.1 は存在しない (NeoForge 自体が 1.21+) |
| License | MIT |

> Description 本文は `_docs/STORE_BODY_EN.md` をそのままコピペ（Modrinth は Markdown 可、CurseForge は WYSIWYG → Markdown モードに切り替えてからペースト）。jar 実ファイル名は `pingtomapxaeros-<version>.jar`（ローダー/MC はストアのタグで区別、ファイル名に接尾辞は付かない）。

---

## 更新リリース手順

1. 全 5 `gradle.properties` の `mod_version` を semver で更新
2. `_docs/CHANGELOG.md` 更新
3. 全 5 サブプロジェクト `./gradlew clean build`
4. 実機で動作確認（最低限 NeoForge 1.21.1 + Xaero's 入りで 1 パターン、Xaero's 抜きで 1 パターン）
5. Modrinth: Versions タブから各 loader/MC の jar をアップロード（タグ付与）
6. CurseForge: Files タブから同上
7. GitHub: `vX.Y.Z` タグ付きリリース作成
