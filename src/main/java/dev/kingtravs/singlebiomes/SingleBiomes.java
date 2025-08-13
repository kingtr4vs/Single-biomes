package dev.kingtravs.singlebiomes;

import dev.kingtravs.singlebiomes.commands.SingleBiomesCommand;
import dev.kingtravs.singlebiomes.generator.SingleBiomeChunkGenerator;
import dev.kingtravs.singlebiomes.utils.ConfigManager;
import dev.kingtravs.singlebiomes.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Level;

/**
 * SingleBiomes Plugin - Generate custom worlds with single biomes and plateau-style terrain
 * 
 * @author KingTravs
 * @version 1.0.0
 * @since 1.0.0
 */
public class SingleBiomes extends JavaPlugin {
    
    private static SingleBiomes instance;
    private ConfigManager configManager;
    private MessageUtil messageUtil;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize configuration
        initializeConfig();
        
        // Initialize utilities
        this.messageUtil = new MessageUtil(this);
        
        // Register commands
        registerCommands();
        
        // Show startup message
        showStartupMessage();
        
        // Log successful initialization
        getLogger().info("SingleBiomes has been enabled successfully!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("SingleBiomes has been disabled.");
        instance = null;
    }
    
    /**
     * Gets the plugin instance
     * @return The plugin instance
     */
    public static SingleBiomes getInstance() {
        return instance;
    }
    
    /**
     * Gets the configuration manager
     * @return The configuration manager
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    /**
     * Gets the message utility
     * @return The message utility
     */
    public MessageUtil getMessageUtil() {
        return messageUtil;
    }
    
    /**
     * Gets the chunk generator for the specified biome
     * @param worldName The world name
     * @param id The generator ID (biome name)
     * @return The chunk generator
     */
    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        if (id == null || id.isEmpty()) {
            getLogger().warning("No biome specified for world generator. Using default.");
            return null;
        }
        
        // Validate biome
        List<String> enabledBiomes = configManager.getEnabledBiomes();
        if (!enabledBiomes.contains(id.toUpperCase())) {
            getLogger().severe("Invalid biome '" + id + "' specified for world '" + worldName + "'!");
            getLogger().info("Valid biomes: " + String.join(", ", enabledBiomes));
            return null;
        }
        
        getLogger().info("Creating SingleBiome world generator for biome: " + id.toUpperCase());
        return new SingleBiomeChunkGenerator(id.toUpperCase());
    }
    
    /**
     * Initializes the configuration system
     */
    private void initializeConfig() {
        try {
            // Save default config if it doesn't exist
            saveDefaultConfig();
            
            // Initialize config manager
            this.configManager = new ConfigManager(this);
            
            getLogger().info("Configuration loaded successfully.");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to initialize configuration!", e);
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }
    
    /**
     * Registers plugin commands
     */
    private void registerCommands() {
        try {
            SingleBiomesCommand commandExecutor = new SingleBiomesCommand(this);
            getCommand("singlebiomes").setExecutor(commandExecutor);
            getCommand("singlebiomes").setTabCompleter(commandExecutor);
            
            getLogger().info("Commands registered successfully.");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to register commands!", e);
        }
    }
    
    /**
     * Shows the startup message in console
     */
    private void showStartupMessage() {
        if (!configManager.showStartupMessages()) {
            return;
        }
        
        String version = getDescription().getVersion();
        String[] lines = {
            "",
            ChatColor.GREEN + "  ███████╗██╗███╗   ██╗ ██████╗ ██╗     ███████╗",
            ChatColor.GREEN + "  ██╔════╝██║████╗  ██║██╔════╝ ██║     ██╔════╝",
            ChatColor.GREEN + "  ███████╗██║██╔██╗ ██║██║  ███╗██║     █████╗  ",
            ChatColor.GREEN + "  ╚════██║██║██║╚██╗██║██║   ██║██║     ██╔══╝  ",
            ChatColor.GREEN + "  ███████║██║██║ ╚████║╚██████╔╝███████╗███████╗",
            ChatColor.GREEN + "  ╚══════╝╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚══════╝╚══════╝",
            ChatColor.GREEN + "                                                 ",
            ChatColor.GREEN + "  ██████╗ ██╗ ██████╗ ███╗   ███╗███████╗███████╗",
            ChatColor.GREEN + "  ██╔══██╗██║██╔═══██╗████╗ ████║██╔════╝██╔════╝",
            ChatColor.GREEN + "  ██████╔╝██║██║   ██║██╔████╔██║█████╗  ███████╗",
            ChatColor.GREEN + "  ██╔══██╗██║██║   ██║██║╚██╔╝██║██╔══╝  ╚════██║",
            ChatColor.GREEN + "  ██████╔╝██║╚██████╔╝██║ ╚═╝ ██║███████╗███████║",
            ChatColor.GREEN + "  ╚═════╝ ╚═╝ ╚═════╝ ╚═╝     ╚═╝╚══════╝╚══════╝",
            "",
            ChatColor.GREEN + "  Version: " + ChatColor.WHITE + version + ChatColor.GREEN + " | Author: " + ChatColor.WHITE + "KingTravs",
            ChatColor.GREEN + "  Generate worlds with single biomes and plateau terrain!",
            ""
        };
        
        for (String line : lines) {
            Bukkit.getConsoleSender().sendMessage(line);
        }
    }
}