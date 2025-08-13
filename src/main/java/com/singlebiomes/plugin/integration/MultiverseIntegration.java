package com.singlebiomes.plugin.integration;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.singlebiomes.plugin.SingleBiomesPlugin;
import org.bukkit.block.Biome;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

/**
 * Integration with Multiverse-Core plugin
 * Allows SingleBiomes worlds to be created using Multiverse commands
 * 
 * @author SingleBiomes Team
 * @version 1.0.0
 */
public class MultiverseIntegration {
    
    private final SingleBiomesPlugin plugin;
    private MultiverseCore multiverseCore;
    private boolean enabled = false;
    
    public MultiverseIntegration(SingleBiomesPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Initialize Multiverse integration
     * 
     * @return true if integration was successful
     */
    public boolean initialize() {
        try {
            Plugin mvPlugin = plugin.getServer().getPluginManager().getPlugin("Multiverse-Core");
            
            if (mvPlugin == null || !mvPlugin.isEnabled()) {
                plugin.getLogger().info("Multiverse-Core not found or not enabled");
                return false;
            }
            
            if (!(mvPlugin instanceof MultiverseCore)) {
                plugin.getLogger().warning("Found Multiverse-Core but it's not the expected type");
                return false;
            }
            
            multiverseCore = (MultiverseCore) mvPlugin;
            enabled = true;
            
            plugin.getLogger().info("Successfully integrated with Multiverse-Core v" + mvPlugin.getDescription().getVersion());
            
            // Log usage instructions
            logUsageInstructions();
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to initialize Multiverse integration", e);
            return false;
        }
    }
    
    /**
     * Disable integration
     */
    public void disable() {
        enabled = false;
        multiverseCore = null;
    }
    
    /**
     * Check if integration is enabled
     * 
     * @return true if enabled
     */
    public boolean isEnabled() {
        return enabled && multiverseCore != null && multiverseCore.isEnabled();
    }
    
    /**
     * Get MultiverseCore instance
     * 
     * @return MultiverseCore instance or null if not available
     */
    public MultiverseCore getMultiverseCore() {
        return multiverseCore;
    }
    
    /**
     * Check if a biome is valid for SingleBiomes generation
     * 
     * @param biomeId The biome identifier
     * @return true if valid
     */
    public boolean isValidBiome(String biomeId) {
        if (biomeId == null || biomeId.isEmpty()) {
            return false;
        }
        
        try {
            Biome biome = Biome.valueOf(biomeId.toUpperCase());
            return plugin.getConfigManager().getEnabledBiomes().contains(biome);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Get world information for a SingleBiomes world
     * 
     * @param worldName The world name
     * @return World information or null if not a SingleBiomes world
     */
    public SingleBiomesWorldInfo getWorldInfo(String worldName) {
        if (!isEnabled()) {
            return null;
        }
        
        try {
            MultiverseWorld mvWorld = multiverseCore.getMVWorldManager().getMVWorld(worldName);
            
            if (mvWorld == null) {
                return null;
            }
            
            // Check if this world uses SingleBiomes generator
            String generator = mvWorld.getGenerator();
            if (generator == null || !generator.startsWith("SingleBiomes")) {
                return null;
            }
            
            // Parse biome from generator string
            String biomeId = null;
            if (generator.contains(":")) {
                biomeId = generator.split(":", 2)[1];
            }
            
            if (biomeId != null && isValidBiome(biomeId)) {
                return new SingleBiomesWorldInfo(worldName, biomeId);
            }
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error getting world info for " + worldName, e);
        }
        
        return null;
    }
    
    /**
     * Log usage instructions for Multiverse integration
     */
    private void logUsageInstructions() {
        plugin.getLogger().info("=== Multiverse Integration Usage ===");
        plugin.getLogger().info("Create SingleBiomes worlds using:");
        plugin.getLogger().info("  /mv create <worldname> normal -g SingleBiomes:BIOME_NAME");
        plugin.getLogger().info("Available biomes:");
        
        for (Biome biome : plugin.getConfigManager().getEnabledBiomes()) {
            plugin.getLogger().info("  - " + biome.name());
        }
        
        plugin.getLogger().info("Example: /mv create mydesert normal -g SingleBiomes:DESERT");
        plugin.getLogger().info("=====================================");
    }
    
    /**
     * Information about a SingleBiomes world
     */
    public static class SingleBiomesWorldInfo {
        private final String worldName;
        private final String biomeId;
        
        public SingleBiomesWorldInfo(String worldName, String biomeId) {
            this.worldName = worldName;
            this.biomeId = biomeId;
        }
        
        public String getWorldName() {
            return worldName;
        }
        
        public String getBiomeId() {
            return biomeId;
        }
        
        public Biome getBiome() {
            try {
                return Biome.valueOf(biomeId.toUpperCase());
            } catch (IllegalArgumentException e) {
                return Biome.PLAINS;
            }
        }
    }
}