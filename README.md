# Apothic Enchanting Terminal

<p align="center">
  <img src="src/main/resources/logo.png" width="128" alt="Apothic Enchanting Terminal">
</p>

AE2 のネットワーク越しに [Apothic Enchanting](https://www.curseforge.com/minecraft/mc-mods/apothic-enchanting) の
**アレクサンドリア図書館**(`EnchLibraryTile`)を操作するための NeoForge MOD です。

図書館のポイントを「エンチャント本」として ME ネットワークに公開するので、
クラフティングターミナルやストレージバスから本家と同じ感覚で出し入れできます。

## できること

- **ライブラリターミナル**(AE2 パーツ)
  ストレージバスで接続された図書館を探し、ポイント・解放済み最大レベルを一覧表示。
  エンチャント本の取り出しと預け入れができます。
- **無線ライブラリターミナル**(アイテム)
  携帯から同じ画面を開けます。AE2WTLib が入っていれば WUT(Universal Terminal)にも統合されます。
  AE2WTLib が無い環境では、MOD 内蔵の単体端末として動作します。
- **ストレージバス連携**
  図書館を ME ストレージとして公開します。本はポイントに変換されて保存され、
  取り出し時はポイントから本が生成されます。優先度は AE2 の通常ルール(高いほど優先)に従います。
- **図書館モニター**(AE2 パーツ)
  接続先の図書館の状態をパネル上に表示します。

## 動作環境

| 項目 | バージョン |
| --- | --- |
| Minecraft | 1.21.1 |
| NeoForge | 21.1.244 以降(21.1.265 で動作確認) |
| Applied Energistics 2 | 19.x(19.2.19 で動作確認) |
| Apothic Enchanting | 1.6.x(1.21.1-1.6.4 で動作確認) |
| AE2WTLib(任意) | 19.5 以降 |

AE2WTLib は**任意**です。無くても MOD は読み込まれ、無線端末は内蔵実装で動きます。

## インストール

1. NeoForge 1.21.1 のクライアント/サーバーを用意する
2. [Releases](../../releases) から `apothic_enchanting_terminal-<version>.jar` を取得する
3. `mods` フォルダに入れる(AE2 と Apothic Enchanting は必須)

## 使い方

1. 図書館の隣に **ME ストレージバス**を設置する
2. ネットワークに **ライブラリターミナル**(パーツ)を付ける、または無線ライブラリターミナルを bind する
3. ターミナルを開くと、接続された図書館のエンチャント一覧が表示されます
4. 一覧から本を取り出す / 本を預け入れる

ストレージバス経由でクラフティングターミナルからも操作する場合は、
ストレージバスの優先度をストレージセルより**高く**しておくと、預け入れ先として優先されます。

## ビルド

JDK 21 が必要です。

```bash
./gradlew build
```

出力: `build/libs/apothic_enchanting_terminal-<version>.jar`

AE2WTLib 統合込みで実行時テストする場合:

```bash
./gradlew runClient -PwithWtlib
```

AE2WTLib を**入れない**状態を再現する場合は、`-PwithWtlib` を付けずに実行します。

## 対応言語

| ロケール | 言語 |
| --- | --- |
| `en_us` | English (基準) |
| `ja_jp` | 日本語 |
| `zh_cn` | 简体中文 |
| `zh_tw` | 繁體中文 |
| `ko_kr` | 한국어 |
| `de_de` | Deutsch |
| `fr_fr` | Français |
| `es_es` | Español |
| `pt_br` | Português (Brasil) |
| `ru_ru` | Русский |

翻訳を追加する場合は `src/main/resources/assets/apothic_enchanting_terminal/lang/<ロケール>.json` を
`en_us.json` と同じキーで追加するだけです（キーが欠けている場合は英語が表示されます）。

## 実装メモ

- 図書館のポイントは Apothic 本家と同じ `2^(Lv-1)` として扱い、預け入れ時に
  「解放済み最大レベル」も本家の `depositBook` と同じ規則で更新します。
- 図書館が持つ総ポイント上限(基本 16Lv 相当 / エンダー 31Lv 相当)を預け入れ上限として使います。
- AE2WTLib の型(`ItemWT` など)は `integration` パッケージに隔離し、リフレクション経由でしか触りません。
  これにより AE2WTLib 未導入環境でもクラス解決に失敗しません。

## 既知の制限

- 複数エンチャントが付いた本は AE2 の 1 キーで表現できないため、ストレージバス経由では預け入れられません
  (図書館ターミナルからは預け入れ可能)。
- 本はポイントに変換されるため、取り出すと同一の本(同じ組み合わせ)には戻りません。

## ライセンス

[MIT](LICENSE)

作者: NT-TK

---

## English

A NeoForge 1.21.1 addon that exposes the [Apothic Enchanting](https://www.curseforge.com/minecraft/mc-mods/apothic-enchanting)
library through an AE2 network.

- **Library Terminal** part: lists the enchantments stored in a library connected via a storage bus,
  and lets you deposit/extract enchanted books.
- **Wireless Library Terminal** item: portable access, optionally integrated into AE2WTLib's universal terminal.
  AE2WTLib is optional — the mod ships its own standalone terminal as a fallback.
- **Storage bus support**: the library is published as ME storage, so enchanted books can be moved in and out
  from any AE2 terminal. Enchantments are stored as the library's points.

Requires Minecraft 1.21.1, NeoForge 21.1.244+, AE2 19.x and Apothic Enchanting 1.6.x. Build with `./gradlew build` (JDK 21).
