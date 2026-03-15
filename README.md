# Thaumic Duality Interface

An add-on for the GTNH edition of Thaumic Energistics (Minecraft 1.7.10) that introduces an interface capable of outputting both items and essentia simultaneously for automated alchemy.

## 🌟 Overview

**Background:**
In the GTNH fork of Thaumic Energistics for Minecraft 1.7.10, processing patterns can encode both items and essentia. However, standard ME Interfaces cannot output essentia directly. As a result, automated alchemy typically relies on Essentia Providers to supply essentia continuously. This prevents the AE2 system from calculating the exact essentia cost per crafting operation and from triggering auto-crafting tasks when essentia is missing.

**Features:**
This mod adds the **ME Essentia Dual Interface**. Similar to the fluid-based ME Dual Interface, it allows the simultaneous output of both items and essentia during AE2 auto-crafting tasks. This integrates essentia requirements into the AE2 crafting calculation system, enabling on-demand supply and closed-loop control for automated alchemy.

## ⚙️ Requirements
To run this mod, ensure the following are installed in your `mods` folder:
- **Thaumcraft 4** (and its dependencies)
- **Applied Energistics 2** (GTNH Edition, and its dependencies)
- **Thaumic Energistics** (GTNH Edition, and its dependencies)

## 🛠️ Building from Source
This project is developed based on the GTNH **[ExampleMod1.7.10](https://github.com/GTNewHorizons/ExampleMod1.7.10)** template.

1. Clone the repository:
   ```bash
    git clone https://github.com/CaelixOrbit/ThaumicDualityInterface.git
    cd ThaumicDualityInterface
   ```

2. Setup the workspace and build:
   ```bash
   ./gradlew setupDecompWorkspace
   ./gradlew build
   ```

3. The compiled `.jar` file will be located in the `build/libs/` directory.

## 👥 Credits

* **Author:** Caelix
* **Website:** [caelixorbit.space](https://caelixorbit.space)
* **Template:** Built upon the [ExampleMod1.7.10](https://github.com/GTNewHorizons/ExampleMod1.7.10) by the GTNH Team.
* Thanks to the **Thaumic Energistics** and **Applied Energistics 2** developers for their respective APIs.

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](./LICENSE) file for details.