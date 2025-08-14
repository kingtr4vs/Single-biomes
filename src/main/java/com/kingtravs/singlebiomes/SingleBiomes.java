package com.kingtravs.singlebiomes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * SingleBiomes Plugin - Main class
 * Generates custom worlds with single biomes and stepped terrain
 * 
 * @author KingTravs
 * @version 1.0.0
 */
public class SingleBiomes extends JavaPlugin {
    
    private static SingleBiomes instance;
    private BiomeWorldGenerator worldGenerator;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Display startup branding
        getLogger().info(ChatColor.GREEN + "╔══════════════════════════════════════╗");
        getLogger().info(ChatColor.GREEN + "║          SingleBiomes v1.0.0         ║");
        getLogger().info(ChatColor.GREEN + "║      by KingTravs - Now Loading      ║");
        getLogger().info(ChatColor.GREEN + "╚══════════════════════════════════════╝");
        
        // Load configuration
        saveDefaultConfig();
        
        // Initialize world generator
        worldGenerator = new BiomeWorldGenerator(this);
        
        // Register commands
        SingleBiomesCommand commandExecutor = new SingleBiomesCommand(this);
        getCommand("singlebiomes").setExecutor(commandExecutor);
        getCommand("singlebiomes").setTabCompleter(commandExecutor);
        
        getLogger().info(ChatColor.GREEN + "SingleBiomes plugin has been enabled successfully!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info(ChatColor.YELLOW + "SingleBiomes plugin has been disabled.");
    }
    
    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        // Support for Multiverse-Core integration
        // Format: SingleBiomes:BIOME_NAME
        String biome = "PLAINS"; // Default biome
        
        if (id != null && !id.isEmpty()) {
            biome = id.toUpperCase();
        }
        
        return new BiomeWorldGenerator(this, biome);
    }
    
    /**
     * Get the plugin instance
     * @return SingleBiomes instance
     */
    public static SingleBiomes getInstance() {
        return instance;
    }
    
    /**
     * Get the world generator
     * @return BiomeWorldGenerator instance
     */
    public BiomeWorldGenerator getWorldGenerator() {
        return worldGenerator;
    }
}