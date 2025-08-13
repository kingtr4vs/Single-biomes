package dev.kingtravs.singlebiomes.utils;

import dev.kingtravs.singlebiomes.SingleBiomes;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration manager for SingleBiomes plugin
 * 
 * @author KingTravs
 * @version 1.0.0
 * @since 1.0.0
 */
public class ConfigManager {
    
    private final SingleBiomes plugin;
    
    public ConfigManager(SingleBiomes plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Gets whether to show startup messages
     * @return True if startup messages should be shown
     */
    public boolean showStartupMessages() {
        return plugin.getConfig().getBoolean("plugin.show-startup-messages", true);
    }
    
    /**
     * Gets whether debug mode is enabled
     * @return True if debug mode is enabled
     */
    public boolean isDebugMode() {
        return plugin.getConfig().getBoolean("plugin.debug-mode", false);
    }
    
    /**
     * Gets the plateau step height
     * @return The plateau step height in blocks
     */
    public int getPlateauStepHeight() {
        return plugin.getConfig().getInt("generation.plateau-step-height", 4);
    }
    
    /**
     * Gets the plateau step width
     * @return The plateau step width in blocks
     */
    public int getPlateauStepWidth() {
        return plugin.getConfig().getInt("generation.plateau-step-width", 20);
    }
    
    /**
     * Gets the base height for world generation
     * @return The base height Y-level
     */
    public int getBaseHeight() {
        return plugin.getConfig().getInt("generation.base-height", 64);
    }
    
    /**
     * Gets the maximum height for plateaus
     * @return The maximum height Y-level
     */
    public int getMaxHeight() {
        return plugin.getConfig().getInt("generation.max-height", 120);
    }
    
    /**
     * Gets the minimum height for plateaus
     * @return The minimum height Y-level
     */
    public int getMinHeight() {
        return plugin.getConfig().getInt("generation.min-height", 60);
    }
    
    /**
     * Gets the list of enabled biomes
     * @return List of enabled biome names
     */
    public List<String> getEnabledBiomes() {
        return plugin.getConfig().getStringList("biomes.enabled");
    }
    
    /**
     * Gets the block types for a specific biome
     * @param biome The biome name
     * @return Map of block type names (surface, subsurface, base)
     */
    public Map<String, Material> getBiomeBlocks(String biome) {
        Map<String, Material> blocks = new HashMap<>();
        
        ConfigurationSection biomeSection = plugin.getConfig().getConfigurationSection("biomes.block-types." + biome);
        if (biomeSection == null) {
            // Return default blocks if no configuration found
            return getDefaultBlocks();
        }
        
        try {
            blocks.put("surface", Material.valueOf(biomeSection.getString("surface", "GRASS_BLOCK")));
            blocks.put("subsurface", Material.valueOf(biomeSection.getString("subsurface", "DIRT")));
            blocks.put("base", Material.valueOf(biomeSection.getString("base", "STONE")));
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid material in config for biome " + biome + ": " + e.getMessage());
            return getDefaultBlocks();
        }
        
        return blocks;
    }
    
    /**
     * Gets default block types
     * @return Default block type map
     */
    private Map<String, Material> getDefaultBlocks() {
        Map<String, Material> blocks = new HashMap<>();
        blocks.put("surface", Material.GRASS_BLOCK);
        blocks.put("subsurface", Material.DIRT);
        blocks.put("base", Material.STONE);
        return blocks;
    }
    
    /**
     * Gets whether to generate structures
     * @return True if structures should be generated
     */
    public boolean generateStructures() {
        return plugin.getConfig().getBoolean("world-settings.generate-structures", true);
    }
    
    /**
     * Gets whether to generate decorations
     * @return True if decorations should be generated
     */
    public boolean generateDecorations() {
        return plugin.getConfig().getBoolean("world-settings.generate-decorations", true);
    }
    
    /**
     * Gets the default seed for world generation
     * @return The default seed (0 for random)
     */
    public long getDefaultSeed() {
        return plugin.getConfig().getLong("world-settings.default-seed", 0L);
    }
    
    /**
     * Gets a message from the configuration
     * @param key The message key
     * @return The formatted message
     */
    public String getMessage(String key) {
        return plugin.getConfig().getString("messages." + key, "&cMessage not found: " + key);
    }
    
    /**
     * Gets the message prefix
     * @return The message prefix
     */
    public String getPrefix() {
        return getMessage("prefix");
    }
    
    /**
     * Validates the configuration
     * @return True if configuration is valid
     */
    public boolean validateConfig() {
        List<String> errors = new ArrayList<>();
        
        // Validate biomes
        List<String> enabledBiomes = getEnabledBiomes();
        if (enabledBiomes.isEmpty()) {
            errors.add("No biomes enabled in configuration!");
        }
        
        // Validate height settings
        if (getMinHeight() >= getMaxHeight()) {
            errors.add("min-height must be less than max-height!");
        }
        
        if (getBaseHeight() < getMinHeight() || getBaseHeight() > getMaxHeight()) {
            errors.add("base-height must be between min-height and max-height!");
        }
        
        // Validate step settings
        if (getPlateauStepHeight() <= 0) {
            errors.add("plateau-step-height must be greater than 0!");
        }
        
        if (getPlateauStepWidth() <= 0) {
            errors.add("plateau-step-width must be greater than 0!");
        }
        
        // Log errors
        if (!errors.isEmpty()) {
            plugin.getLogger().severe("Configuration validation failed:");
            for (String error : errors) {
                plugin.getLogger().severe("  - " + error);
            }
            return false;
        }
        
        return true;
    }
}