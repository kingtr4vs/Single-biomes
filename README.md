# SingleBiomes Plugin

![Plugin Version](https://img.shields.io/badge/version-1.0.0-brightgreen)
![Minecraft Version](https://img.shields.io/badge/minecraft-1.21.4--1.21.8-blue)
![Server Software](https://img.shields.io/badge/server-Paper%2FSpigot-orange)

A professional Minecraft plugin for generating custom worlds with single biomes and beautiful stepped terrain.

## 📋 Description

SingleBiomes allows you to create stunning custom worlds where the entire terrain consists of a single biome with stepped, plateau-like terrain. Each step is exactly **1 block high** and **20 blocks wide** (configurable), creating natural staircase-like formations across your world.

## ✨ Features

- **Single Biome Worlds**: Generate entire worlds with just one biome type
- **Stepped Terrain**: Beautiful plateau-like terrain with consistent 1-block high steps
- **5 Supported Biomes**: Snowy Taiga, Badlands, Desert, Plains, and Mushroom Fields
- **Configurable Settings**: Customize step width and base height
- **Multiverse Integration**: Full compatibility with Multiverse-Core
- **Tab Completion**: Smart command completion for better user experience
- **Professional UI**: Beautiful in-game messages and help system
- **Error Handling**: Comprehensive validation and error prevention

## 🎮 Supported Biomes

| Biome | Surface Block | Underground Block | Description |
|-------|---------------|-------------------|-------------|
| **Snowy Taiga** | Snow Block | Dirt | Snowy terrain with snow blocks |
| **Badlands** | Terracotta | Red Sandstone | Desert mesa with terracotta formations |
| **Desert** | Sand | Sandstone | Sandy desert terrain |
| **Plains** | Grass Block | Dirt | Grassy plains with dirt underground |
| **Mushroom Fields** | Mycelium | Dirt | Mushroom island terrain with mycelium |

## 📦 Installation

1. **Download** the SingleBiomes.jar file
2. **Place** it in your server's `plugins/` folder
3. **Restart** your server
4. **Configure** the plugin (optional) by editing `plugins/SingleBiomes/config.yml`
5. **Start generating** worlds with `/singlebiomes generate <worldname> <biome>`

### Requirements
- **Server Software**: Paper or Spigot 1.21.4-1.21.8
- **Java Version**: Java 21 or higher
- **Optional**: Multiverse-Core (for enhanced world management)

## 🎯 Commands & Usage

### Basic Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/singlebiomes help` | Shows all available commands | `singlebiomes.help` |
| `/singlebiomes generate <world> <biome>` | Creates a new SingleBiomes world | `singlebiomes.generate` |

### Command Aliases
- `/sb` - Short alias for `/singlebiomes`
- `/sbiomes` - Alternative alias

### Examples

```bash
# Generate a desert world named "MyDesert"
/singlebiomes generate MyDesert DESERT

# Generate a snowy world named "WinterLand"  
/singlebiomes generate WinterLand SNOWY_TAIGA

# Generate a badlands world named "MesaWorld"
/singlebiomes generate MesaWorld BADLANDS
```

## 🔧 Multiverse Integration

SingleBiomes fully supports Multiverse-Core for advanced world management:

```bash
# Create a world using Multiverse with SingleBiomes generator
/mv create <worldname> normal -g SingleBiomes:<BIOME>

# Examples:
/mv create DesertWorld normal -g SingleBiomes:DESERT
/mv create SnowyWorld normal -g SingleBiomes:SNOWY_TAIGA
```

## 🛡️ Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `singlebiomes.use` | Basic plugin access | OP |
| `singlebiomes.help` | View help information | Everyone |
| `singlebiomes.generate` | Generate new worlds | OP |
| `singlebiomes.admin` | Full administrative access | OP |
| `singlebiomes.reload` | Reload plugin configuration | OP |

## ⚙️ Configuration

The plugin creates a `config.yml` file in `plugins/SingleBiomes/` with the following settings:

```yaml
# World Generation Settings
world-generation:
  # Base height for terrain generation (Y-coordinate where steps start)
  base-height: 64
  
  # Step width in blocks (how wide each plateau step is)
  step-width: 20
  
  # Step height is FIXED at 1 block (cannot be changed)
  step-height: 1

# Supported Biomes Configuration
supported-biomes:
  - "SNOWY_TAIGA"
  - "BADLANDS" 
  - "DESERT"
  - "PLAINS"
  - "MUSHROOM_FIELDS"

# Plugin Settings
plugin:
  debug: false
  startup-branding: true
  max-world-name-length: 16

# World Generation Limits
limits:
  max-surface-height: 310
  min-surface-height: 5
```

### Configuration Options

- **base-height**: Starting Y-coordinate for terrain generation (default: 64)
- **step-width**: Width of each plateau step in blocks (default: 20)
- **step-height**: Height of each step - **FIXED at 1 block**
- **debug**: Enable debug messages in console
- **startup-branding**: Show plugin branding on startup

## 🏗️ How It Works

### Stepped Terrain Generation

The plugin generates terrain using a precise mathematical algorithm:

1. **Base Height**: Starts at the configured base height (default Y=64)
2. **Step Calculation**: Each coordinate is divided by step width to determine step level
3. **Height Increment**: Each step is exactly **1 block higher** than the previous
4. **Consistent Pattern**: Creates uniform stepped terrain across the entire world

### Generation Formula
```
stepX = worldX / stepWidth
stepZ = worldZ / stepWidth  
surfaceY = baseHeight + stepX + stepZ
```

This ensures that every step is exactly 1 block high, creating beautiful natural staircases.

## 🐛 Troubleshooting

### Common Issues

**World generation fails**
- Ensure you have sufficient server permissions
- Check that the world name doesn't already exist
- Verify the biome name is spelled correctly

**Plugin won't load**
- Confirm you're using Paper/Spigot 1.21.4-1.21.8
- Check Java version (requires Java 21+)
- Look for error messages in server console

**Commands not working**
- Verify you have the correct permissions
- Make sure you're using the exact biome names (case-sensitive)
- Check if the plugin loaded successfully

### Getting Help

If you encounter issues:
1. Check the server console for error messages
2. Verify your server version compatibility
3. Ensure proper permissions are set
4. Contact support with detailed error information

## 📄 License

```
SingleBiomes Plugin License
Copyright (c) 2025 KingTravs
All rights reserved.

This plugin and its source code are the intellectual property of KingTravs.

You are NOT allowed to:
- Redistribute this plugin in any form, free or paid
- Re-upload this plugin or source code anywhere
- Modify and distribute as your own
- Resell this plugin or derivative works

You ARE allowed to:
- Use on your own Minecraft servers
- Modify source code for personal use only (do not distribute)
- Share your server using this plugin

Any violation may result in legal action.
By using or modifying this plugin, you agree to these terms.
For permissions beyond this license, contact: KingTravs
```

## 📊 Plugin Statistics

- **Lines of Code**: ~500+
- **Classes**: 3 main classes
- **Supported Biomes**: 5
- **Commands**: 2 main commands
- **Permissions**: 5 permission nodes
- **Configuration Options**: 10+ settings

## 🚀 Future Updates

Planned features for future versions:
- Additional biome support
- Custom block configurations per biome
- Advanced terrain patterns
- World templates
- GUI-based world creation

---

**Author**: KingTravs  
**Version**: 1.0.0  
**Last Updated**: 2025  
**Minecraft Compatibility**: 1.21.4-1.21.8