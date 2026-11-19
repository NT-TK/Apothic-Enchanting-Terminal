# Apothic Enchanting Terminal

<p align="center">
  <img src="src/main/resources/logo.png" width="128" alt="Apothic Enchanting Terminal">
</p>

A NeoForge 1.21.1 addon that lets you use the [Apothic Enchanting](https://www.curseforge.com/minecraft/mc-mods/apothic-enchanting)
**Library of Alexandria** (`EnchLibraryTile`) from an AE2 network.

The library's enchantment points are published on the ME network as enchanted books, so books can be
deposited and extracted from any AE2 terminal, storage bus or storage cell setup.

## Features

- **Library Terminal** (AE2 part)
  Finds a library connected through a Storage Bus and lists its points and unlocked maximum levels.
  Enchanted books can be extracted from and deposited into the library.
- **Wireless Library Terminal** (item)
  Opens the same screen on the go. If AE2WTLib is installed it is also registered in the
  WUT (Universal Terminal); without AE2WTLib the mod falls back to its own standalone terminal.
- **Storage bus integration**
  The library is exposed as ME storage. Books are converted into the library's points and books are
  created from points when extracted. A book with several enchantments is split into points for every
  enchantment it carries. Priorities follow the normal AE2 rules (higher priority wins).
- **Library Monitor** (AE2 part)
  Shows the state of the connected library on the part itself.

## Requirements

| | |
| --- | --- |
| Minecraft | 1.21.1 |
| NeoForge | 21.1.244 or newer (tested with 21.1.265) |
| Applied Energistics 2 | 19.x (tested with 19.2.19) |
| Apothic Enchanting | 1.6.x (tested with 1.21.1-1.6.4) |
| AE2WTLib (optional) | 19.5 or newer |

AE2WTLib is **optional**. The mod loads without it and the wireless terminal works through its
built-in implementation.

## Installation

1. Set up a NeoForge 1.21.1 client or server.
2. Download `apothic_enchanting_terminal-<version>.jar` from [Releases](../../releases).
3. Put it into the `mods` folder (AE2 and Apothic Enchanting are required).

## Usage

1. Place an **ME Storage Bus** next to the library.
2. Add a **Library Terminal** part to the network, or bind a Wireless Library Terminal.
3. Open the terminal to see the enchantments stored in the connected library.
4. Extract books from the list, or deposit books into it.

To insert books from a Crafting Terminal through the storage bus, give the storage bus a
**higher priority** than your storage cells so it is picked as the destination first.

## Languages

| Locale | Language |
| --- | --- |
| `en_us` | English (base) |
| `ja_jp` | 日本語 |
| `zh_cn` | 简体中文 |
| `zh_tw` | 繁體中文 |
| `ko_kr` | 한국어 |
| `de_de` | Deutsch |
| `fr_fr` | Français |
| `es_es` | Español |
| `pt_br` | Português (Brasil) |
| `ru_ru` | Русский |

To add a translation, copy `en_us.json` to `src/main/resources/assets/apothic_enchanting_terminal/lang/<locale>.json`
and translate the values. Missing keys fall back to English.

## Building

JDK 21 is required.

```bash
./gradlew build
```

Output: `build/libs/apothic_enchanting_terminal-<version>.jar`

To run the game with AE2WTLib present:

```bash
./gradlew runClient -PwithWtlib
```

Omit `-PwithWtlib` to test the setup without AE2WTLib installed.

## Implementation notes

- Points follow Apothic Enchanting's own `2^(level-1)` scale, and depositing a book updates the
  unlocked maximum level with the same rules as the library's `depositBook`.
- The library's total point budget (16 levels for the basic library, 31 for the ender library) is
  used as the insertion limit.
- AE2WTLib types (`ItemWT` and friends) are isolated in the `integration` package and only touched
  through reflection, so class resolution never fails when AE2WTLib is absent.

## Known limitations

- Books are converted into points, so extraction does not reproduce the original book.
- When a multi-enchantment book is inserted, enchantments that are already at their point cap gain
  nothing (the excess is discarded, exactly like the library's own deposit).

## License

[MIT](LICENSE)

Author: NT-TK

---

# Apothic Enchanting Terminal (日本語)

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
  取り出し時はポイントから本が生成されます。複数エンチャントが付いた本は、
  含まれる全エンチャントへポイントを分解して預け入れます。
  優先度は AE2 の通常ルール(高いほど優先)に従います。
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

## 実装メモ

- 図書館のポイントは Apothic 本家と同じ `2^(Lv-1)` として扱い、預け入れ時に
  「解放済み最大レベル」も本家の `depositBook` と同じ規則で更新します。
- 図書館が持つ総ポイント上限(基本 16Lv 相当 / エンダー 31Lv 相当)を預け入れ上限として使います。
- AE2WTLib の型(`ItemWT` など)は `integration` パッケージに隔離し、リフレクション経由でしか触りません。
  これにより AE2WTLib 未導入環境でもクラス解決に失敗しません。

## 既知の制限

- 本はポイントに変換されるため、取り出すと同一の本(同じ組み合わせ)には戻りません。
- 複数エンチャント本を預け入れる際、すでにポイント上限に達しているエンチャントには
  ポイントが入りません(超過分は切り捨て。図書館本体の預け入れと同じ挙動)。

## ライセンス

[MIT](LICENSE)

作者: NT-TK
