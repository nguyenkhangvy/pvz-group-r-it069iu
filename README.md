# 🌻 Plants vs. Zombies Clone

> A faithful Java recreation of PopCap's iconic tower-defense game, built from scratch using **LibGDX** with clean OOP design, data-driven architecture, and established design patterns.

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java)
![LibGDX](https://img.shields.io/badge/LibGDX-1.14-red?style=flat-square)
![Gradle](https://img.shields.io/badge/Build-Gradle-02303A?style=flat-square&logo=gradle)
![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)

**Course:** Principles of Database Management (IT069IU)  
**Instructor:** MSc. Nguyen Quang Phu  
**University:** Vietnam National University – HCMC, International University

</div>

---

## 👥 Team

| Student ID   | Name               | Role   |
|--------------|--------------------|--------|
| ITITWE24075  | Nguyễn Khang Vỹ    | Leader |
| ITITWE24071  | Hoàng Thị Cẩm Tú   | Member |
| ITITWE24060  | Nguyễn Minh Nhật   | Member |
| ITITWE24015  | Nguyễn Minh Nhân   | Member |
| ITITWE24059  | Nguyễn Hằng Ngân   | Member |

---

## 🎮 About the Project

This project reproduces the core gameplay of Plants vs. Zombies as a software engineering exercise, applying:

- **Clean OOP design** — proper inheritance hierarchies, interfaces, and Single Responsibility Principle
- **Data-driven architecture** — JSON files control all game balance (HP, damage, speed) without recompiling
- **Established design patterns** — Singleton, Factory Method, Interface-as-Context
- **Scalable entity system** — easily extendable with new plants or zombie types

### ✨ Features

- 🗺️ **8 playable levels** with progressive difficulty
- 🌱 **7 plant types** — Peashooter, Snow Pea, Repeater, Sunflower, Wall-nut, Cherry Bomb, Potato Mine, Chomper
- 🧟 **5 zombie types** — Basic, Cone-head, Bucket-head, Flag, Pole Vault
- 💾 **Save / Load** — progress and settings persisted via LibGDX Preferences
- ⚙️ **Difficulty settings** — Easy / Normal / Hard (scales zombie HP, damage, and speed)
- 🔊 **Full audio** — sound effects and looping background music
- ⚡ **2× speed toggle** — unlocked at Level 4
- 🚜 **Lawn Mower** last-chance mechanic per row
- 🌞 **Sun economy** — sky drops + Sunflower production

---

## 🛠️ Tech Stack

| Component         | Technology                        |
|-------------------|-----------------------------------|
| Language          | Java 17                           |
| Game Framework    | LibGDX 1.14                       |
| Build Tool        | Gradle (LibGDX plugin)            |
| Data Format       | JSON (LibGDX built-in parser)     |
| Audio             | LibGDX Sound + Music API (.ogg)   |
| Persistence       | LibGDX Preferences                |

---

## 🏗️ Project Structure

```
pvz/
├── core/           # GameClock, GameConfig, Difficulty
├── data/           # POJOs mirroring JSON structure
├── entity/
│   ├── plant/      # Plant subclasses (Shooter, Sunflower, Defensive, etc.)
│   ├── zombie/     # Zombie, PoleVaultZombie
│   └── projectile/ # Projectile
├── factory/        # PlantFactory, ZombieFactory, ProjectileFactory
├── manager/        # Singletons: AssetProvider, AudioManager, DataManager, SaveManager...
├── screen/         # GameScreen, StartupScreen, WinScreen, LoseScreen, etc.
├── system/         # GridSystem, WaveSystem, LawnSystem, PlantUnlockSystem
└── util/           # UI helpers

assets/
├── data/
│   ├── plants/     # peashooter.json, sunflower.json, ...
│   ├── zombies/    # basic.json, conehead.json, polevault.json, ...
│   └── levels/     # level_1_1.json ... level_1_8.json
├── images/         # PNG sprite frames ({entity}_{state}_{frame}.png)
└── audio/          # .ogg sound effects and music
```

---

## 🎨 Design Patterns

| Pattern                    | Applied To                              | Benefit                                                    |
|----------------------------|-----------------------------------------|------------------------------------------------------------|
| **Singleton**              | All 7 Manager classes                   | One source of truth; accessible anywhere; no re-init       |
| **Factory Method**         | PlantFactory, ZombieFactory, ProjectileFactory | Decouples creation from use; centralizes difficulty scaling |
| **Interface-as-Context**   | GameContext, PlantContext               | Subsystems depend on minimal abstractions, not concrete classes |
| **Composition**            | AnimationComponent                      | Reusable animation logic; JSON-driven frame lists          |
| **Data-Driven (JSON)**     | All entity stats, level wave scripts    | Balance tuning without recompilation                       |

---

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Gradle (wrapper included)

### Run the game

```bash
git clone https://github.com/nguyenkhangvy/pvz-group-r-it069iu.git
cd pvz-group-r-it069iu
./gradlew lwjgl3:run
```

### Build a distributable JAR

```bash
./gradlew lwjgl3:dist
```

The output JAR will be located in `desktop/build/libs/`.

---

## 🎯 Gameplay

1. **Place plants** on the 5×9 grid to defend the left boundary from incoming zombies.
2. **Collect Sun** — falling from the sky or produced by Sunflowers — to pay for plants.
3. **Survive all waves** defined in the level's JSON script to win.
4. Each row has one **Lawn Mower** as a last resort — it activates when a zombie reaches the home line, but only once per row.
5. The game is lost if a zombie crosses the home line on a row whose mower has already been used.

### Controls

| Action              | Input                        |
|---------------------|------------------------------|
| Select plant        | Click seed card in HUD       |
| Place plant         | Click on a green grid cell   |
| Collect sun         | Click the sun token          |
| Remove plant        | Select shovel, click plant (unlocked at Level 3) |
| Toggle 2× speed     | Click speed button (unlocked at Level 4) |
| Pause               | Click pause button or press `Esc` |

---

## 📊 Entity Overview

### Plants

| Plant        | Class            | Special Mechanic                          |
|--------------|------------------|-------------------------------------------|
| Peashooter   | ShooterPlant     | Fires 1 pea per attack interval           |
| Snow Pea     | ShooterPlant     | Fires slowing peas (0.5× speed debuff)    |
| Repeater     | ShooterPlant     | Fires 2 peas per attack interval          |
| Sunflower    | SunflowerPlant   | Produces sun periodically                 |
| Wall-nut     | DefensivePlant   | High HP blocker; sprite changes as HP drops |
| Cherry Bomb  | CherryBomb       | 1.2s fuse → 3×3 AoE explosion            |
| Potato Mine  | PotatoMine       | Arms over time; contact detonation        |
| Chomper      | Chomper          | Instant-kills one zombie; then chews      |

### Zombies

| Zombie       | Class             | Special Mechanic                          |
|--------------|-------------------|-------------------------------------------|
| Basic        | Zombie            | Standard walk → eat → die                |
| Cone-head    | Zombie            | Higher HP                                 |
| Bucket-head  | Zombie            | Even higher HP                            |
| Flag         | Zombie            | Slightly faster; signals wave start       |
| Pole Vault   | PoleVaultZombie   | Jumps over the first plant it encounters  |

---

## 📝 References

1. [Plants vs. Zombies Wiki](https://plantsvszombies.fandom.com) — plant/zombie data reference
2. [LibGDX Documentation](https://libgdx.com/wiki/) — framework setup and APIs
3. [Factory Method Design Pattern – Refactoring Guru](https://refactoring.guru/design-patterns/factory-method)
4. [SOLID Principles in Java](https://viblo.asia/p/nguyen-tac-solid-trong-java)
5. [LibGDX SpriteBatch & TextureRegions](https://libgdx.com/wiki/graphics/2d/spritebatch-textureregions-and-sprites)

---

## 📄 License

This project is for educational purposes as part of coursework at International University – VNU HCMC.
