package com.singlebiomes.plugin;

import com.singlebiomes.plugin.commands.SingleBiomesCommand;
import com.singlebiomes.plugin.generator.SingleBiomesGenerator;
import com.singlebiomes.plugin.integration.MultiverseIntegration;
import org.bukkit.ChatColor;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * Main plugin class for SingleBiomes
 * Generates custom worlds with single biomes and stepped plateau terrain
 * 
 * @author SingleBiomes Team
 * @version 1.0.0
 */
public class SingleBiomesPlugin extends JavaPlugin {
    
    private static SingleBiomesPlugin instance;
    private ConfigManager configManager;
    private MultiverseIntegration multiverseIntegration;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize configuration
        initializeConfig();
        
        // Show startup message
        showStartupMessage();
        
        // Register commands
        registerCommands();
        
        // Initialize integrations
        initializeIntegrations();
        
        // Log successful startup
        getLogger().info("SingleBiomes has been enabled successfully!");
    }
    
    @Override
    public void onDisable() {
        // Clean shutdown
        if (multiverseIntegration != null) {
            multiverseIntegration.disable();
        }
        
        getLogger().info("SingleBiomes has been disabled.");
        instance = null;
    }
    
    /**
     * Initialize plugin configuration
     */
    private void initializeConfig() {
        // Save default config if it doesn't exist
        saveDefaultConfig();
        
        // Initialize config manager
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        
        getLogger().info("Configuration loaded successfully.");
    }
    
    /**
     * Show startup message with green branding
     */
    private void showStartupMessage() {
        if (!getConfig().getBoolean("plugin.show-startup-message", true)) {
            return;
        }
        
        // Send colored message to console
        getServer().getConsoleSender().sendMessage("");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "╔════════════════════════════════════╗");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "║          " + ChatColor.BOLD + "SingleBiomes" + ChatColor.RESET + ChatColor.GREEN + "             ║");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "║        " + ChatColor.WHITE + "Version " + getDescription().getVersion() + ChatColor.GREEN + "              ║");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "║                                    ║");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "║  " + ChatColor.WHITE + "Single-biome world generation" + ChatColor.GREEN + "     ║");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "║  " + ChatColor.WHITE + "with stepped plateau terrain" + ChatColor.GREEN + "      ║");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "╚════════════════════════════════════╝");
        getServer().getConsoleSender().sendMessage("");
    }
    
    /**
     * Register plugin commands
     */
    private void registerCommands() {
        SingleBiomesCommand commandExecutor = new SingleBiomesCommand(this);
        getCommand("singlebiomes").setExecutor(commandExecutor);
        getCommand("singlebiomes").setTabCompleter(commandExecutor);
        
        getLogger().info("Commands registered successfully.");
    }
    
    /**
     * Initialize plugin integrations
     */
    private void initializeIntegrations() {
        // Initialize Multiverse integration if available
        if (getServer().getPluginManager().getPlugin("Multiverse-Core") != null) {
            multiverseIntegration = new MultiverseIntegration(this);
            if (multiverseIntegration.initialize()) {
                getLogger().info("Multiverse-Core integration enabled.");
            } else {
                getLogger().warning("Failed to initialize Multiverse-Core integration.");
            }
        } else {
            getLogger().info("Multiverse-Core not found. Integration disabled.");
        }
    }
    
    /**
     * Get the world generator for SingleBiomes worlds
     * 
     * @param worldName The name of the world
     * @param id The generator ID (biome name)
     * @return ChunkGenerator instance
     */
    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        try {
            return new SingleBiomesGenerator(this, id);
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to create world generator for world: " + worldName + " with biome: " + id, e);
            return null;
        }
    }
    
    /**
     * Get plugin instance
     * 
     * @return Plugin instance
     */
    public static SingleBiomesPlugin getInstance() {
        return instance;
    }
    
    /**
     * Get configuration manager
     * 
     * @return ConfigManager instance
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    /**
     * Get Multiverse integration
     * 
     * @return MultiverseIntegration instance or null if not available
     */
    public MultiverseIntegration getMultiverseIntegration() {
        return multiverseIntegration;
    }
    
    /**
     * Reload plugin configuration
     */
    public void reloadPluginConfig() {
        reloadConfig();
        configManager.loadConfig();
        getLogger().info("Configuration reloaded successfully.");
    }
}