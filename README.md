# SingleBiomes Plugin

**Version:** 1.0.0  
**Author:** KingTravs  
**Compatible with:** Paper/Spigot 1.21.4 - 1.21.8

Generate custom Minecraft worlds where the entire terrain consists of a single biome with stepped, plateau-like terrain featuring 20-block wide "steps" that create natural staircase formations.

## 🌍 Features

- **Single Biome Worlds:** Generate entire worlds with just one biome
- **Plateau Terrain:** Unique stepped, staircase-like terrain generation
- **Configurable Settings:** Customize step height, width, and block types
- **Multiple Biomes Supported:**
  - SNOWY_TAIGA
  - BADLANDS
  - DESERT
  - PLAINS
  - MUSHROOM_FIELDS
- **Multiverse Integration:** Full compatibility with Multiverse-Core
- **Professional Commands:** Easy-to-use commands with tab completion
- **Error Handling:** Robust validation and clear error messages

## 📦 Installation

1. **Download** the SingleBiomes.jar file
2. **Place** it in your server's `plugins/` folder
3. **Restart** your server
4. **Configure** the plugin by editing `plugins/SingleBiomes/config.yml` (optional)
5. **Reload** or restart your server to apply configuration changes

### Requirements

- **Server Software:** Paper or Spigot
- **Minecraft Version:** 1.21.4 - 1.21.8
- **Java Version:** 21 or higher
- **Optional:** Multiverse-Core (for enhanced world management)

## 🎮 Commands

### Main Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/singlebiomes help` | Show help message and available biomes | `singlebiomes.use` |
| `/singlebiomes generate <worldName> <biome>` | Create a new SingleBiomes world | `singlebiomes.generate` |

### Command Aliases

- `/sb` - Short alias for `/singlebiomes`
- `/singlebiome` - Alternative alias

### Examples

```
/singlebiomes help
/singlebiomes generate desert_world DESERT
/sb generate snowy_world SNOWY_TAIGA
/singlebiomes generate badlands_test BADLANDS
```

## 🔧 Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `singlebiomes.use` | Basic plugin usage and help command | OP |
| `singlebiomes.generate` | Create new SingleBiomes worlds | OP |
| `singlebiomes.admin` | Access to all plugin features | OP |

## ⚙️ Configuration

The plugin creates a `config.yml` file with the following structure:

```yaml
# Plugin Settings
plugin:
  show-startup-messages: true    # Show ASCII art on startup
  debug-mode: false             # Enable debug logging

# World Generation Settings
generation:
  plateau-step-height: 4        # Height of each step (blocks)
  plateau-step-width: 20        # Width of each step (blocks)
  base-height: 64              # Base Y-level for plateaus
  max-height: 120              # Maximum plateau height
  min-height: 60               # Minimum plateau height

# Supported Biomes
biomes:
  enabled:
    - SNOWY_TAIGA
    - BADLANDS
    - DESERT
    - PLAINS
    - MUSHROOM_FIELDS
  
  # Custom block types for each biome
  block-types:
    DESERT:
      surface: SAND
      subsurface: SANDSTONE
      base: STONE
    # ... (other biomes)

# World Creation Settings
world-settings:
  generate-structures: true     # Generate villages, dungeons, etc.
  generate-decorations: true    # Generate trees, flowers, etc.
  default-seed: 0              # Default seed (0 = random)
```

### Customization Options

- **Plateau Dimensions:** Adjust `plateau-step-height` and `plateau-step-width` to change terrain characteristics
- **Height Limits:** Modify `min-height` and `max-height` to control elevation range
- **Block Types:** Customize surface, subsurface, and base blocks for each biome
- **World Features:** Toggle structures and decorations on/off

## 🌐 Multiverse Integration

SingleBiomes is fully compatible with Multiverse-Core:

### Automatic Registration
When you create a world using `/singlebiomes generate`, it will automatically be registered with Multiverse if the plugin is installed.

### Manual Creation via Multiverse
```
/mv create <worldName> normal -g SingleBiomes:<BIOME_NAME>
```

### Examples
```
/mv create desert_world normal -g SingleBiomes:DESERT
/mv create snowy_world normal -g SingleBiomes:SNOWY_TAIGA
/mv create mushroom_world normal -g SingleBiomes:MUSHROOM_FIELDS
```

## 🏗️ How It Works

### Terrain Generation
1. **Stepped Plateaus:** The generator creates terrain in 20-block wide steps
2. **Height Variation:** Each step has a different height based on coordinates
3. **Natural Stairs:** Results in natural staircase-like formations
4. **Biome Consistency:** Entire world uses the same biome throughout

### Block Layering
- **Surface Layer:** Biome-appropriate surface blocks (grass, sand, mycelium, etc.)
- **Subsurface Layer:** 2-3 blocks of subsurface material (dirt, sandstone, etc.)
- **Base Layer:** Stone foundation with bedrock at Y=0
- **Water Features:** Low areas automatically fill with water up to sea level

## 🛠️ Building from Source

If you want to compile the plugin yourself:

### Prerequisites
- Java 21 or higher
- Maven 3.6 or higher
- Git

### Steps
```bash
git clone https://github.com/KingTravs/SingleBiomes.git
cd SingleBiomes
mvn clean package
```

The compiled JAR will be in the `target/` directory.

## 🐛 Troubleshooting

### Common Issues

**World Creation Fails**
- Check that the biome name is spelled correctly and is supported
- Ensure you have sufficient disk space
- Verify server has proper permissions to create files

**Generator Not Working**
- Make sure you're using the correct generator syntax
- Check console for error messages during world creation
- Verify plugin is properly loaded (`/plugins` command)

**Multiverse Issues**
- Ensure Multiverse-Core is up to date
- Check Multiverse configuration for conflicts
- Try manual import: `/mv import <worldName> normal`

### Getting Help

1. Check the console for error messages
2. Enable debug mode in config.yml
3. Verify all permissions are set correctly
4. Ensure server version compatibility

## 📝 License

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

## 🤝 Support

For support, bug reports, or feature requests, please contact **KingTravs**.

---

**Enjoy creating unique single-biome worlds with stepped plateau terrain!** 🏔️
