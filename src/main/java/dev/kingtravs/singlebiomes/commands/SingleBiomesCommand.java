package dev.kingtravs.singlebiomes.commands;

import dev.kingtravs.singlebiomes.SingleBiomes;
import dev.kingtravs.singlebiomes.utils.ConfigManager;
import dev.kingtravs.singlebiomes.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Command handler for SingleBiomes plugin
 * Handles /singlebiomes commands including help and world generation
 * 
 * @author KingTravs
 * @version 1.0.0
 * @since 1.0.0
 */
public class SingleBiomesCommand implements CommandExecutor, TabCompleter {
    
    private final SingleBiomes plugin;
    private final ConfigManager config;
    private final MessageUtil messageUtil;
    
    public SingleBiomesCommand(SingleBiomes plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.messageUtil = plugin.getMessageUtil();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check permission
        if (!sender.hasPermission("singlebiomes.use")) {
            messageUtil.sendPrefixedMessage(sender, "no-permission");
            return true;
        }
        
        // Handle no arguments or help
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelpMessage(sender);
            return true;
        }
        
        // Handle generate command
        if (args[0].equalsIgnoreCase("generate")) {
            return handleGenerateCommand(sender, args);
        }
        
        // Unknown command
        sendHelpMessage(sender);
        return true;
    }
    
    /**
     * Handles the generate command
     * @param sender The command sender
     * @param args Command arguments
     * @return True if command was handled
     */
    private boolean handleGenerateCommand(CommandSender sender, String[] args) {
        // Check permission
        if (!sender.hasPermission("singlebiomes.generate")) {
            messageUtil.sendPrefixedMessage(sender, "no-permission");
            return true;
        }
        
        // Validate arguments
        if (args.length < 3) {
            messageUtil.sendRawMessage(sender, "&cUsage: /singlebiomes generate <worldName> <biome>");
            return true;
        }
        
        String worldName = args[1];
        String biome = args[2].toUpperCase();
        
        // Validate world name
        if (!isValidWorldName(worldName)) {
            messageUtil.sendPrefixedMessage(sender, "invalid-world-name");
            return true;
        }
        
        // Check if world already exists
        if (worldExists(worldName)) {
            messageUtil.sendPrefixedMessage(sender, "world-exists", "world", worldName);
            return true;
        }
        
        // Validate biome
        List<String> enabledBiomes = config.getEnabledBiomes();
        if (!enabledBiomes.contains(biome)) {
            messageUtil.sendPrefixedMessage(sender, "invalid-biome", "biome", biome);
            return true;
        }
        
        // Generate world asynchronously to prevent server lag
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                createSingleBiomeWorld(sender, worldName, biome);
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to create world '" + worldName + "': " + e.getMessage());
                messageUtil.sendPrefixedMessage(sender, "world-creation-failed", "world", worldName);
            }
        });
        
        messageUtil.sendRawMessage(sender, "&aCreating world '" + worldName + "' with biome '" + biome + "'...");
        return true;
    }
    
    /**
     * Creates a new SingleBiomes world
     * @param sender The command sender
     * @param worldName The world name
     * @param biome The biome name
     */
    private void createSingleBiomeWorld(CommandSender sender, String worldName, String biome) {
        try {
            // Create world creator
            WorldCreator creator = new WorldCreator(worldName);
            creator.type(WorldType.NORMAL);
            creator.generateStructures(config.generateStructures());
            
            // Set custom generator
            creator.generator(plugin.getName() + ":" + biome);
            
            // Set seed if configured
            long seed = config.getDefaultSeed();
            if (seed != 0) {
                creator.seed(seed);
            }
            
            // Create the world on the main thread
            Bukkit.getScheduler().runTask(plugin, () -> {
                try {
                    World world = creator.createWorld();
                    
                    if (world != null) {
                        messageUtil.sendPrefixedMessage(sender, "world-created", "world", worldName, "biome", biome);
                        
                        // Teleport player to new world if they're a player
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            player.teleport(world.getSpawnLocation());
                        }
                        
                        // Try to register with Multiverse if available
                        registerWithMultiverse(worldName, biome);
                        
                    } else {
                        messageUtil.sendPrefixedMessage(sender, "world-creation-failed", "world", worldName);
                    }
                } catch (Exception e) {
                    plugin.getLogger().severe("Failed to create world on main thread: " + e.getMessage());
                    messageUtil.sendPrefixedMessage(sender, "world-creation-failed", "world", worldName);
                }
            });
            
        } catch (Exception e) {
            plugin.getLogger().severe("Error in createSingleBiomeWorld: " + e.getMessage());
            Bukkit.getScheduler().runTask(plugin, () -> 
                messageUtil.sendPrefixedMessage(sender, "world-creation-failed", "world", worldName)
            );
        }
    }
    
    /**
     * Registers the world with Multiverse-Core if available
     * @param worldName The world name
     * @param biome The biome name
     */
    private void registerWithMultiverse(String worldName, String biome) {
        Plugin multiverse = Bukkit.getPluginManager().getPlugin("Multiverse-Core");
        if (multiverse != null && multiverse.isEnabled()) {
            try {
                // Run Multiverse import command
                String command = "mv import " + worldName + " normal -g " + plugin.getName() + ":" + biome;
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                plugin.getLogger().info("Registered world '" + worldName + "' with Multiverse-Core");
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to register world with Multiverse: " + e.getMessage());
            }
        }
    }
    
    /**
     * Checks if a world name is valid
     * @param worldName The world name to check
     * @return True if valid
     */
    private boolean isValidWorldName(String worldName) {
        if (worldName == null || worldName.trim().isEmpty()) {
            return false;
        }
        
        // Check for invalid characters
        return worldName.matches("^[a-zA-Z0-9_-]+$") && worldName.length() <= 32;
    }
    
    /**
     * Checks if a world already exists
     * @param worldName The world name to check
     * @return True if world exists
     */
    private boolean worldExists(String worldName) {
        // Check if world is loaded
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            return true;
        }
        
        // Check if world folder exists
        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
        return worldFolder.exists() && worldFolder.isDirectory();
    }
    
    /**
     * Sends help message to command sender
     * @param sender The command sender
     */
    private void sendHelpMessage(CommandSender sender) {
        List<String> enabledBiomes = config.getEnabledBiomes();
        messageUtil.sendHelpMessage(sender, enabledBiomes);
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (!sender.hasPermission("singlebiomes.use")) {
            return completions;
        }
        
        if (args.length == 1) {
            // First argument - subcommands
            List<String> subcommands = Arrays.asList("help", "generate");
            return subcommands.stream()
                    .filter(cmd -> cmd.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("generate")) {
            // Second argument for generate - world name (no suggestions, user types their own)
            return new ArrayList<>();
        }
        
        if (args.length == 3 && args[0].equalsIgnoreCase("generate")) {
            // Third argument for generate - biome names
            List<String> enabledBiomes = config.getEnabledBiomes();
            return enabledBiomes.stream()
                    .filter(biome -> biome.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        return completions;
    }
}