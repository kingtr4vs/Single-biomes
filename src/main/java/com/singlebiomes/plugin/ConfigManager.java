package com.singlebiomes.plugin;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.logging.Level;

/**
 * Manages plugin configuration and provides easy access to config values
 * 
 * @author SingleBiomes Team
 * @version 1.0.0
 */
public class ConfigManager {
    
    private final SingleBiomesPlugin plugin;
    private final Set<Biome> enabledBiomes = new HashSet<>();
    private final Map<Biome, BiomeBlockConfig> biomeBlockConfigs = new HashMap<>();
    
    // World generation settings
    private int plateauStepHeight;
    private int plateauStepWidth;
    private int baseHeight;
    private int maxHeightVariation;
    
    // Other settings
    private boolean tabCompletion;
    private boolean detailedHelp;
    private boolean debugEnabled;
    private boolean logGeneration;
    
    public ConfigManager(SingleBiomesPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Load configuration from config.yml
     */
    public void loadConfig() {
        try {
            // Load world generation settings
            loadWorldGenerationSettings();
            
            // Load biome settings
            loadBiomeSettings();
            
            // Load command settings
            loadCommandSettings();
            
            // Load debug settings
            loadDebugSettings();
            
            plugin.getLogger().info("Configuration loaded: " + enabledBiomes.size() + " biomes enabled");
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load configuration", e);
        }
    }
    
    /**
     * Load world generation settings
     */
    private void loadWorldGenerationSettings() {
        ConfigurationSection worldGen = plugin.getConfig().getConfigurationSection("world-generation");
        if (worldGen != null) {
            plateauStepHeight = worldGen.getInt("plateau-step-height", 4);
            plateauStepWidth = worldGen.getInt("plateau-step-width", 20);
            baseHeight = worldGen.getInt("base-height", 64);
            maxHeightVariation = worldGen.getInt("max-height-variation", 80);
        } else {
            // Default values
            plateauStepHeight = 4;
            plateauStepWidth = 20;
            baseHeight = 64;
            maxHeightVariation = 80;
        }
        
        // Validate values
        plateauStepHeight = Math.max(1, Math.min(plateauStepHeight, 20));
        plateauStepWidth = Math.max(5, Math.min(plateauStepWidth, 100));
        baseHeight = Math.max(1, Math.min(baseHeight, 250));
        maxHeightVariation = Math.max(10, Math.min(maxHeightVariation, 200));
    }
    
    /**
     * Load biome settings
     */
    private void loadBiomeSettings() {
        ConfigurationSection biomes = plugin.getConfig().getConfigurationSection("biomes");
        if (biomes == null) {
            plugin.getLogger().warning("No biomes section found in config.yml, using defaults");
            loadDefaultBiomes();
            return;
        }
        
        // Load enabled biomes
        List<String> enabledBiomeNames = biomes.getStringList("enabled");
        enabledBiomes.clear();
        
        for (String biomeName : enabledBiomeNames) {
            try {
                Biome biome = Biome.valueOf(biomeName.toUpperCase());
                enabledBiomes.add(biome);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid biome name in config: " + biomeName);
            }
        }
        
        // Load block configurations
        ConfigurationSection blockTypes = biomes.getConfigurationSection("block-types");
        if (blockTypes != null) {
            loadBlockConfigurations(blockTypes);
        } else {
            loadDefaultBlockConfigurations();
        }
        
        // Ensure we have at least one enabled biome
        if (enabledBiomes.isEmpty()) {
            plugin.getLogger().warning("No valid biomes enabled, loading defaults");
            loadDefaultBiomes();
        }
    }
    
    /**
     * Load block configurations for biomes
     */
    private void loadBlockConfigurations(ConfigurationSection blockTypes) {
        biomeBlockConfigs.clear();
        
        for (Biome biome : enabledBiomes) {
            String biomeName = biome.name();
            ConfigurationSection biomeConfig = blockTypes.getConfigurationSection(biomeName);
            
            if (biomeConfig != null) {
                BiomeBlockConfig blockConfig = new BiomeBlockConfig();
                
                blockConfig.surface = getMaterial(biomeConfig.getString("surface"), getDefaultSurface(biome));
                blockConfig.subsurface = getMaterial(biomeConfig.getString("subsurface"), Material.DIRT);
                blockConfig.stone = getMaterial(biomeConfig.getString("stone"), Material.STONE);
                blockConfig.decoration = getMaterial(biomeConfig.getString("decoration"), getDefaultDecoration(biome));
                
                biomeBlockConfigs.put(biome, blockConfig);
            } else {
                // Use default configuration
                biomeBlockConfigs.put(biome, getDefaultBlockConfig(biome));
            }
        }
    }
    
    /**
     * Load command settings
     */
    private void loadCommandSettings() {
        ConfigurationSection commands = plugin.getConfig().getConfigurationSection("commands");
        if (commands != null) {
            tabCompletion = commands.getBoolean("tab-completion", true);
            detailedHelp = commands.getBoolean("detailed-help", true);
        } else {
            tabCompletion = true;
            detailedHelp = true;
        }
    }
    
    /**
     * Load debug settings
     */
    private void loadDebugSettings() {
        ConfigurationSection debug = plugin.getConfig().getConfigurationSection("debug");
        if (debug != null) {
            debugEnabled = debug.getBoolean("enabled", false);
            logGeneration = debug.getBoolean("log-generation", false);
        } else {
            debugEnabled = false;
            logGeneration = false;
        }
    }
    
    /**
     * Load default biomes if config is missing or invalid
     */
    private void loadDefaultBiomes() {
        enabledBiomes.clear();
        enabledBiomes.add(Biome.SNOWY_TAIGA);
        enabledBiomes.add(Biome.BADLANDS);
        enabledBiomes.add(Biome.DESERT);
        enabledBiomes.add(Biome.PLAINS);
        enabledBiomes.add(Biome.MUSHROOM_FIELDS);
        
        loadDefaultBlockConfigurations();
    }
    
    /**
     * Load default block configurations
     */
    private void loadDefaultBlockConfigurations() {
        biomeBlockConfigs.clear();
        for (Biome biome : enabledBiomes) {
            biomeBlockConfigs.put(biome, getDefaultBlockConfig(biome));
        }
    }
    
    /**
     * Get default block configuration for a biome
     */
    private BiomeBlockConfig getDefaultBlockConfig(Biome biome) {
        BiomeBlockConfig config = new BiomeBlockConfig();
        
        config.surface = getDefaultSurface(biome);
        config.subsurface = Material.DIRT;
        config.stone = Material.STONE;
        config.decoration = getDefaultDecoration(biome);
        
        // Override defaults for specific biomes
        switch (biome) {
            case BADLANDS:
                config.subsurface = Material.RED_SANDSTONE;
                config.stone = Material.TERRACOTTA;
                break;
            case DESERT:
                config.subsurface = Material.SANDSTONE;
                config.stone = Material.SANDSTONE;
                break;
            case MUSHROOM_FIELDS:
                config.surface = Material.MYCELIUM;
                break;
        }
        
        return config;
    }
    
    /**
     * Get default surface material for a biome
     */
    private Material getDefaultSurface(Biome biome) {
        return switch (biome) {
            case SNOWY_TAIGA -> Material.SNOW_BLOCK;
            case BADLANDS -> Material.RED_SAND;
            case DESERT -> Material.SAND;
            case PLAINS -> Material.GRASS_BLOCK;
            case MUSHROOM_FIELDS -> Material.MYCELIUM;
            default -> Material.GRASS_BLOCK;
        };
    }
    
    /**
     * Get default decoration material for a biome
     */
    private Material getDefaultDecoration(Biome biome) {
        return switch (biome) {
            case SNOWY_TAIGA -> Material.SPRUCE_LOG;
            case BADLANDS -> Material.DEAD_BUSH;
            case DESERT -> Material.CACTUS;
            case PLAINS -> Material.OAK_LOG;
            case MUSHROOM_FIELDS -> Material.RED_MUSHROOM;
            default -> Material.OAK_LOG;
        };
    }
    
    /**
     * Get material from string with fallback
     */
    private Material getMaterial(String materialName, Material fallback) {
        if (materialName == null || materialName.isEmpty()) {
            return fallback;
        }
        
        try {
            return Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid material name: " + materialName + ", using fallback: " + fallback);
            return fallback;
        }
    }
    
    // Getters
    public Set<Biome> getEnabledBiomes() { return new HashSet<>(enabledBiomes); }
    public BiomeBlockConfig getBlockConfig(Biome biome) { return biomeBlockConfigs.get(biome); }
    public int getPlateauStepHeight() { return plateauStepHeight; }
    public int getPlateauStepWidth() { return plateauStepWidth; }
    public int getBaseHeight() { return baseHeight; }
    public int getMaxHeightVariation() { return maxHeightVariation; }
    public boolean isTabCompletionEnabled() { return tabCompletion; }
    public boolean isDetailedHelpEnabled() { return detailedHelp; }
    public boolean isDebugEnabled() { return debugEnabled; }
    public boolean isLogGenerationEnabled() { return logGeneration; }
    
    /**
     * Block configuration for a biome
     */
    public static class BiomeBlockConfig {
        public Material surface;
        public Material subsurface;
        public Material stone;
        public Material decoration;
    }
}