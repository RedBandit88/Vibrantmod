# Vibrant World — Fabric Mod for Minecraft 26.1.2

A client-side Fabric mod that replicates the colour-vibrancy boost of the
**Vibrant Vanilla** resource pack as a fully customisable post-process shader.
No resource pack required, no "Fabulous" graphics mode required.

---

## Features

| Setting | Default | Range | Description |
|---|---|---|---|
| **Enabled** | ✓ | toggle | Master on/off switch |
| **Saturation** | 1.6 | 0 – 4 | 1.0 = vanilla, 1.6 = Vibrant Vanilla feel |
| **Vibrance** | 0.4 | 0 – 1 | Selectively boosts less-saturated pixels more |
| **Contrast** | 1.1 | 0.5 – 2 | 1.0 = unchanged |
| **Brightness** | 0.0 | −0.5 – +0.5 | Linear brightness offset |
| **Colour Temperature** | 0.0 | −1 – +1 | Negative = cooler, positive = warmer |
| **Gamma** | 0.95 | 0.5 – 2 | Shadow lift / darken |

Settings are saved to `.minecraft/config/vibrantmod.json` automatically.

---

## Requirements

| Dependency | Version | Required? |
|---|---|---|
| Minecraft Java Edition | **26.1.2** | ✅ |
| Fabric Loader | ≥ 0.18.4 | ✅ |
| Fabric API | 0.150.0+26.1.2 | ✅ |
| Cloth Config API | 17.x (Fabric) | ⚠️ Optional (needed for in-game GUI) |
| Mod Menu | 14.x | ⚠️ Optional (needed to open config screen) |

Without Cloth Config / Mod Menu you can still edit `config/vibrantmod.json`
manually in any text editor while Minecraft is closed.

---

## Building from Source

### Prerequisites
- **Java 25** JDK (required by Minecraft 26.1.x)
- IntelliJ IDEA 2025.3+ (recommended) or any IDE
- Internet access (Gradle downloads dependencies automatically)

### Steps

```bash
# 1. Clone / extract the project
cd vibrantmod

# 2. Build
./gradlew build          # Linux / macOS
gradlew.bat build        # Windows

# 3. The compiled .jar is at:
#    build/libs/vibrantmod-1.0.0.jar
```

Copy the jar into your `.minecraft/mods/` folder alongside Fabric API
(and optionally Cloth Config + Mod Menu).

### Generating IDE sources (recommended)
```bash
./gradlew genSources
# Then open build.gradle in IntelliJ and import the Gradle project
```

---

## Configuration

### In-game (recommended)
1. Install **Mod Menu** and **Cloth Config API**
2. Open Minecraft → Mods → Vibrant World → Config

### Manual (JSON)
Edit `.minecraft/config/vibrantmod.json`:
```json
{
  "enabled": true,
  "saturation": 1.6,
  "contrast": 1.1,
  "brightness": 0.0,
  "temperature": 0.0,
  "vibrance": 0.4,
  "gamma": 0.95
}
```
Changes are applied on the next game launch (or press F3+T to reload resources
without restarting).

---

## How It Works

The mod injects a GLSL post-process shader (`assets/vibrantmod/shaders/core/vibrance.fsh`)
into the rendering pipeline via Fabric's `HudRenderCallback`. Every frame:

1. The main render target is passed through the shader
2. The shader applies (in order): gamma decode → brightness → contrast →
   colour temperature → saturation → vibrance → gamma re-encode
3. The result is written back to the main render target

No "Fabulous" graphics setting is required because the mod hooks directly
into the Fabric rendering API rather than relying on the vanilla post-process
system.

---

## Compatibility

- Works on top of any resource pack
- Compatible with OptiFine alternatives (Sodium, Iris) — though Iris users
  may prefer using a shader pack directly
- Server-side: this mod is **client-only** and does not need to be installed
  on the server

---

## License

MIT — do whatever you like, attribution appreciated.
