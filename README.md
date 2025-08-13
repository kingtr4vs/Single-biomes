# SingleBiomes

A production-quality Minecraft plugin for Paper/Spigot 1.21.4-1.21.8 that generates custom worlds where the entire terrain consists of a single biome with stepped, plateau-like terrain generation.

## 🌟 Features

- **Single-Biome Worlds**: Generate entire worlds using only one biome
- **Stepped Plateau Terrain**: Unique 20-block wide stepped terrain generation creating natural staircase-like landscapes  
- **5 Supported Biomes**: SNOWY_TAIGA, BADLANDS, DESERT, PLAINS, MUSHROOM_FIELDS
- **Configurable Settings**: Customize plateau dimensions, block types, and generation parameters
- **Multiverse Integration**: Compatible with Multiverse-Core for easy world management
- **Professional Commands**: Full command system with tab completion and help
- **Error Handling**: Robust validation and crash prevention
- **Performance Optimized**: Efficient world generation with minimal server impact

## 📋 Requirements

- **Server Software**: Paper or Spigot 1.21.4 - 1.21.8
- **Java Version**: Java 21 or higher
- **Optional Dependencies**: Multiverse-Core (for enhanced world management)

## 🚀 Installation

1. **Download** the SingleBiomes.jar file
2. **Place** it in your server's `plugins/` directory
3. **Start/Restart** your server
4. **Configure** the plugin by editing `plugins/SingleBiomes/config.yml` (optional)
5. **Reload** the plugin with `/singlebiomes reload` or restart the server

## 🎮 Commands

| Command | Description | Permission | Usage |
|---------|-------------|------------|-------|
| `/singlebiomes help` | Show help message | `singlebiomes.use` | `/singlebiomes help` |
| `/singlebiomes generate <world> <biome>` | Create new SingleBiomes world | `singlebiomes.generate` | `/singlebiomes generate mydesert DESERT` |
| `/singlebiomes info` | Show plugin information | `singlebiomes.use` | `/singlebiomes info` |
| `/singlebiomes reload` | Reload configuration | `singlebiomes.admin` | `/singlebiomes reload` |

### Command Aliases
- `/sb` - Short alias for `/singlebiomes`
- `/sbiomes` - Alternative alias for `/singlebiomes`

## 🔐 Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `singlebiomes.*` | Access to all SingleBiomes commands | OP |
| `singlebiomes.use` | Use basic SingleBiomes commands | Everyone |
| `singlebiomes.generate` | Generate SingleBiomes worlds | OP |
| `singlebiomes.admin` | Access admin commands (reload) | OP |

## 🌍 Supported Biomes

- **SNOWY_TAIGA** - Snow-covered terrain with spruce decorations
- **BADLANDS** - Red sand and terracotta formations with dead bush
- **DESERT** - Sandy terrain with cactus decorations  
- **PLAINS** - Grassy terrain with oak tree decorations
- **MUSHROOM_FIELDS** - Mycelium terrain with mushroom decorations

## ⚙️ Configuration

The plugin creates a `config.yml` file in the `plugins/SingleBiomes/` directory:

```yaml
# SingleBiomes Plugin Configuration
# Version: 1.0.0

# Plugin Settings
plugin:
  show-startup-message: true
  check-updates: true

# World Generation Settings
world-generation:
  # Default plateau step height (in blocks)
  plateau-step-height: 4
  
  # Default plateau step width (in blocks) 
  plateau-step-width: 20
  
  # Base world height (Y level where generation starts)
  base-height: 64
  
  # Maximum height variation for plateaus
  max-height-variation: 80

# Supported Biomes Configuration
biomes:
  # List of enabled biomes for world generation
  enabled:
    - SNOWY_TAIGA
    - BADLANDS
    - DESERT
    - PLAINS
    - MUSHROOM_FIELDS
  
  # Block configuration for each biome
  block-types:
    SNOWY_TAIGA:
      surface: SNOW_BLOCK
      subsurface: DIRT
      stone: STONE
      decoration: SPRUCE_LOG
    BADLANDS:
      surface: RED_SAND
      subsurface: RED_SANDSTONE
      stone: TERRACOTTA
      decoration: DEAD_BUSH
    DESERT:
      surface: SAND
      subsurface: SANDSTONE
      stone: SANDSTONE
      decoration: CACTUS
    PLAINS:
      surface: GRASS_BLOCK
      subsurface: DIRT
      stone: STONE
      decoration: OAK_LOG
    MUSHROOM_FIELDS:
      surface: MYCELIUM
      subsurface: DIRT
      stone: STONE
      decoration: RED_MUSHROOM

# World Creation Settings
world-settings:
  world-type: NORMAL
  generate-structures: true
  spawn-protection: 16
  spawn-monsters: true
  spawn-animals: true

# Command Settings
commands:
  tab-completion: true
  detailed-help: true

# Debug Settings
debug:
  enabled: false
  log-generation: false
```

## 🔗 Multiverse Integration

SingleBiomes integrates seamlessly with Multiverse-Core:

```bash
# Create worlds using Multiverse commands
/mv create mydesert normal -g SingleBiomes:DESERT
/mv create snowyworld normal -g SingleBiomes:SNOWY_TAIGA
/mv create badlands normal -g SingleBiomes:BADLANDS
/mv create plains normal -g SingleBiomes:PLAINS
/mv create mushroom normal -g SingleBiomes:MUSHROOM_FIELDS

# Teleport to worlds
/mv tp mydesert
```

## 📝 Usage Examples

### Basic World Creation
```bash
# Create a desert world named "mydesert"
/singlebiomes generate mydesert DESERT

# Create a snowy world named "winter_world"  
/singlebiomes generate winter_world SNOWY_TAIGA

# Create a badlands world named "mesa"
/singlebiomes generate mesa BADLANDS
```

### With Multiverse-Core
```bash
# Create and configure with Multiverse
/mv create desert_world normal -g SingleBiomes:DESERT
/mv modify set animals false desert_world
/mv modify set monsters true desert_world
```

## 🔧 Development

### Building from Source

1. **Clone** the repository
2. **Ensure** you have Java 21 and Maven installed
3. **Run** `mvn clean package`
4. **Find** the compiled JAR in the `target/` directory

### Project Structure
```
src/main/java/com/singlebiomes/plugin/
├── SingleBiomesPlugin.java          # Main plugin class
├── ConfigManager.java               # Configuration management
├── commands/
│   └── SingleBiomesCommand.java     # Command handling
├── generator/
│   ├── SingleBiomesGenerator.java   # World generator
│   └── SingleBiomeProvider.java     # Biome provider
└── integration/
    └── MultiverseIntegration.java   # Multiverse integration
```

## 🐛 Troubleshooting

### Common Issues

**World generation fails**
- Check that the biome name is spelled correctly
- Ensure the biome is enabled in config.yml
- Verify server has sufficient memory

**Multiverse integration not working**
- Ensure Multiverse-Core is installed and enabled
- Check server console for integration messages
- Use correct syntax: `-g SingleBiomes:BIOME_NAME`

**Permission errors**
- Verify user has required permissions
- Check permission plugin configuration
- Use `/singlebiomes info` to test basic access

## 📊 Performance

SingleBiomes is optimized for performance:
- **Async World Creation**: Worlds generate without blocking the main thread
- **Efficient Algorithms**: Optimized plateau generation with minimal calculations
- **Memory Conscious**: Low memory footprint during generation
- **Chunk-based**: Generates terrain chunk by chunk for smooth performance

## 🤝 Contributing

We welcome contributions! Please:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

- **Issues**: Report bugs on GitHub Issues
- **Discord**: Join our community server
- **Wiki**: Check the GitHub Wiki for detailed guides

## 🏆 Credits

**SingleBiomes Team**
- Plugin development and maintenance
- World generation algorithms
- Multiverse integration

**Special Thanks**
- Paper/Spigot development team
- Multiverse-Core developers
- Minecraft modding community

---

**Made with ❤️ for the Minecraft community**