# Screenshots / GIF 撮影シナリオ

Modrinth / CurseForge / README に貼る画像の撮影ガイド。

## 撮影前準備

- 解像度: **1920x1080** で撮影、必要なら後で 1280x720 にリサイズ
- HUD: 必要な部分（Ping-Wheel リング、Xaero's ミニマップ）を残して F1 で他は消す
- 周囲の景観を整える（草原・森等の見栄え重視）
- ShareX または Windows Game Bar (`Win+G`) でスクショ/録画
- マルチプレイ環境（2 アカウント or 友人協力）で撮ると「ping を打つ／受ける」両視点が撮れる

## 必須スクショ (公開時に最低限欲しい)

### 1. ヒーロー画像（Modrinth ページバナー、1920×300 程度）
- 場面: Ping-Wheel のリング + Xaero's ミニマップ + Xaero's の「Add to waypoints?」プロンプトが同フレームに収まる構図

### 2. ping → waypoint 提案 GIF
- 中キーで Ping-Wheel リング → 方向選択 → 確定 → Xaero's に「Add to waypoints?」プロンプト → クリックで waypoint 確定 (3〜5秒)
- 「ping した瞬間にプロンプトが出る」が伝わるテンポ

### 3. Xaero's の Waypoints 画面
- `M` キーでフルスクリーンマップ → 確定済みの ping waypoint が紫で並ぶ

### 4. 自分の ping を受け取らない設定
- `feature.registerOwnPings = false` の状態で自分が ping → プロンプト出ない、別プレイヤーが ping → プロンプト出る、の比較

## 任意 (あると尖る)

### 5. 設定画面
- NeoForge mod settings GUI を開いて Ping to Map の項目を見せる

### 6. Compass to Map: Xaero's edition との同時利用
- 両 MOD を入れた状態で「コンパスで構造物発見 → プロンプト」「ping で位置共有 → プロンプト」が両方動くデモ

## ファイル整理

- `screenshots/` フォルダにまとめる
- ファイル名: `01_hero.png` / `02_ping_to_waypoint.gif` / `03_waypoints.png` / 等
- README から相対リンクで参照
