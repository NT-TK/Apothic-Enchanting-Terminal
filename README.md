# Apothic Enchanting Terminal

A NeoForge 1.21.1 addon that lets you use the [Apothic Enchanting](https://www.curseforge.com/minecraft/mc-mods/apothic-enchanting)
**Library of Alexandria** (`EnchLibraryTile`) from an AE2 network.

The library's enchantment points are published on the ME network as enchanted books, so books can be
deposited and extracted from any AE2 terminal, storage bus or storage cell setup.

A Japanese version of this document is available in [README.ja.md](README.ja.md).

## Features

- **Library Terminal** (AE2 part)
  Finds a library connected through a Storage Bus and lists its points and unlocked maximum levels.
  Enchanted books can be extracted from and deposited into the library.
- **Wireless Library Terminal** (item)
  Opens the same screen on the go. If AE2WTLib is installed it is also registered in the
  WUT (Universal Terminal); without AE2WTLib the mod falls back to its own standalone terminal.
- **Storage bus integration**
  The library is exposed as ME storage. Books are converted into the library's points and books are
  created from points when extracted. Priorities follow the normal AE2 rules (higher priority wins).
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
| `ja_jp` | Japanese |
| `zh_cn` | Simplified Chinese |
| `zh_tw` | Traditional Chinese |
| `ko_kr` | Korean |
| `de_de` | German |
| `fr_fr` | French |
| `es_es` | Spanish |
| `pt_br` | Portuguese (Brazil) |
| `ru_ru` | Russian |

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

- Books with multiple enchantments cannot be represented by a single AE key, so they are not
  accepted through the storage bus (depositing them in the Library Terminal still works).
- Books are converted into points, so extraction does not reproduce the original book.

## License

[MIT](LICENSE)

Author: NT-TK
