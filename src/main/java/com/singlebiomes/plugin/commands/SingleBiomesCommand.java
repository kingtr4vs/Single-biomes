package com.singlebiomes.plugin.commands;

import com.singlebiomes.plugin.SingleBiomesPlugin;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Main command handler for SingleBiomes plugin
 * Handles /singlebiomes commands with help, generate, and tab completion
 * 
 * @author SingleBiomes Team
 * @version 1.0.0
 */
public class SingleBiomesCommand implements CommandExecutor, TabCompleter {
    
    private final SingleBiomesPlugin plugin;
    
    public SingleBiomesCommand(SingleBiomesPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check basic permission
        if (!sender.hasPermission("singlebiomes.use")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
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
        
        // Handle reload command (admin only)
        if (args[0].equalsIgnoreCase("reload")) {
            return handleReloadCommand(sender);
        }
        
        // Handle info command
        if (args[0].equalsIgnoreCase("info")) {
            return handleInfoCommand(sender);
        }
        
        // Unknown subcommand
        sender.sendMessage(ChatColor.RED + "Unknown command. Use " + ChatColor.YELLOW + "/" + label + " help" + ChatColor.RED + " for help.");
        return true;
    }
    
    /**
     * Handle world generation command
     */
    private boolean handleGenerateCommand(CommandSender sender, String[] args) {
        // Check permission
        if (!sender.hasPermission("singlebiomes.generate")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to generate worlds.");
            return true;
        }
        
        // Validate arguments
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/singlebiomes generate <worldName> <biome>");
            sender.sendMessage(ChatColor.GRAY + "Available biomes: " + getAvailableBiomesString());
            return true;
        }
        
        String worldName = args[1];
        String biomeName = args[2].toUpperCase();
        
        // Validate world name
        if (!isValidWorldName(worldName)) {
            sender.sendMessage(ChatColor.RED + "Invalid world name. World names can only contain letters, numbers, hyphens, and underscores.");
            return true;
        }
        
        // Check if world already exists
        if (plugin.getServer().getWorld(worldName) != null) {
            sender.sendMessage(ChatColor.RED + "A world with the name '" + worldName + "' already exists.");
            return true;
        }
        
        // Validate biome
        Biome biome;
        try {
            biome = Biome.valueOf(biomeName);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Invalid biome: " + biomeName);
            sender.sendMessage(ChatColor.GRAY + "Available biomes: " + getAvailableBiomesString());
            return true;
        }
        
        // Check if biome is enabled
        if (!plugin.getConfigManager().getEnabledBiomes().contains(biome)) {
            sender.sendMessage(ChatColor.RED + "Biome " + biome + " is not enabled in the configuration.");
            sender.sendMessage(ChatColor.GRAY + "Available biomes: " + getAvailableBiomesString());
            return true;
        }
        
        // Create world asynchronously to prevent server lag
        sender.sendMessage(ChatColor.GREEN + "Creating world '" + worldName + "' with biome " + biome + "...");
        
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                // Create world with SingleBiomes generator
                WorldCreator creator = new WorldCreator(worldName);
                creator.generator(plugin, biome.name());
                creator.generateStructures(plugin.getConfig().getBoolean("world-settings.generate-structures", true));
                
                World world = creator.createWorld();
                
                if (world != null) {
                    // Configure world settings
                    configureWorld(world);
                    
                    // Send success message on main thread
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        sender.sendMessage(ChatColor.GREEN + "Successfully created world '" + worldName + "' with biome " + biome + "!");
                        
                        if (sender instanceof Player) {
                            sender.sendMessage(ChatColor.YELLOW + "Use " + ChatColor.WHITE + "/mv tp " + worldName + 
                                ChatColor.YELLOW + " to teleport to the world (if Multiverse is installed).");
                        }
                    });
                    
                    plugin.getLogger().info("Created SingleBiomes world: " + worldName + " with biome: " + biome);
                } else {
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        sender.sendMessage(ChatColor.RED + "Failed to create world '" + worldName + "'. Check server console for details.");
                    });
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to create world " + worldName + ": " + e.getMessage());
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    sender.sendMessage(ChatColor.RED + "Failed to create world '" + worldName + "'. Check server console for details.");
                });
            }
        });
        
        return true;
    }
    
    /**
     * Handle reload command
     */
    private boolean handleReloadCommand(CommandSender sender) {
        if (!sender.hasPermission("singlebiomes.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to reload the configuration.");
            return true;
        }
        
        try {
            plugin.reloadPluginConfig();
            sender.sendMessage(ChatColor.GREEN + "SingleBiomes configuration reloaded successfully!");
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Failed to reload configuration. Check console for details.");
            plugin.getLogger().severe("Failed to reload configuration: " + e.getMessage());
        }
        
        return true;
    }
    
    /**
     * Handle info command
     */
    private boolean handleInfoCommand(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "=== " + ChatColor.BOLD + "SingleBiomes Info" + ChatColor.RESET + ChatColor.GREEN + " ===");
        sender.sendMessage(ChatColor.YELLOW + "Version: " + ChatColor.WHITE + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.YELLOW + "Author: " + ChatColor.WHITE + plugin.getDescription().getAuthors().get(0));
        sender.sendMessage(ChatColor.YELLOW + "Enabled Biomes: " + ChatColor.WHITE + plugin.getConfigManager().getEnabledBiomes().size());
        sender.sendMessage(ChatColor.YELLOW + "Plateau Step Width: " + ChatColor.WHITE + plugin.getConfigManager().getPlateauStepWidth());
        sender.sendMessage(ChatColor.YELLOW + "Plateau Step Height: " + ChatColor.WHITE + plugin.getConfigManager().getPlateauStepHeight());
        
        if (plugin.getMultiverseIntegration() != null && plugin.getMultiverseIntegration().isEnabled()) {
            sender.sendMessage(ChatColor.YELLOW + "Multiverse Integration: " + ChatColor.GREEN + "Enabled");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Multiverse Integration: " + ChatColor.RED + "Disabled");
        }
        
        return true;
    }
    
    /**
     * Send help message to sender
     */
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "=== " + ChatColor.BOLD + "SingleBiomes Help" + ChatColor.RESET + ChatColor.GREEN + " ===");
        sender.sendMessage(ChatColor.YELLOW + "/singlebiomes help" + ChatColor.WHITE + " - Show this help message");
        sender.sendMessage(ChatColor.YELLOW + "/singlebiomes generate <world> <biome>" + ChatColor.WHITE + " - Create a new SingleBiomes world");
        sender.sendMessage(ChatColor.YELLOW + "/singlebiomes info" + ChatColor.WHITE + " - Show plugin information");
        
        if (sender.hasPermission("singlebiomes.admin")) {
            sender.sendMessage(ChatColor.YELLOW + "/singlebiomes reload" + ChatColor.WHITE + " - Reload plugin configuration");
        }
        
        if (plugin.getConfigManager().isDetailedHelpEnabled()) {
            sender.sendMessage("");
            sender.sendMessage(ChatColor.GREEN + "Available Biomes:");
            sender.sendMessage(ChatColor.GRAY + getAvailableBiomesString());
            sender.sendMessage("");
            sender.sendMessage(ChatColor.GREEN + "Examples:");
            sender.sendMessage(ChatColor.WHITE + "  /singlebiomes generate mydesert DESERT");
            sender.sendMessage(ChatColor.WHITE + "  /singlebiomes generate snowy_world SNOWY_TAIGA");
            
            if (plugin.getMultiverseIntegration() != null && plugin.getMultiverseIntegration().isEnabled()) {
                sender.sendMessage("");
                sender.sendMessage(ChatColor.GREEN + "Multiverse Integration:");
                sender.sendMessage(ChatColor.WHITE + "  /mv create <world> normal -g SingleBiomes:BIOME_NAME");
            }
        }
    }
    
    /**
     * Configure world settings after creation
     */
    private void configureWorld(World world) {
        world.setSpawnFlags(
            plugin.getConfig().getBoolean("world-settings.spawn-monsters", true),
            plugin.getConfig().getBoolean("world-settings.spawn-animals", true)
        );
    }
    
    /**
     * Check if world name is valid
     */
    private boolean isValidWorldName(String name) {
        return name != null && name.matches("^[a-zA-Z0-9_-]+$") && name.length() <= 50 && !name.isEmpty();
    }
    
    /**
     * Get available biomes as a formatted string
     */
    private String getAvailableBiomesString() {
        return plugin.getConfigManager().getEnabledBiomes().stream()
            .map(Biome::name)
            .collect(Collectors.joining(", "));
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!plugin.getConfigManager().isTabCompletionEnabled()) {
            return null;
        }
        
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // First argument: subcommands
            List<String> subcommands = Arrays.asList("help", "generate", "info");
            if (sender.hasPermission("singlebiomes.admin")) {
                subcommands = new ArrayList<>(subcommands);
                subcommands.add("reload");
            }
            
            return subcommands.stream()
                .filter(sub -> sub.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("generate")) {
            // Second argument for generate: world name (no completions, user types their own)
            return completions;
        }
        
        if (args.length == 3 && args[0].equalsIgnoreCase("generate")) {
            // Third argument for generate: biome names
            Set<Biome> enabledBiomes = plugin.getConfigManager().getEnabledBiomes();
            return enabledBiomes.stream()
                .map(Biome::name)
                .filter(biome -> biome.toLowerCase().startsWith(args[2].toLowerCase()))
                .collect(Collectors.toList());
        }
        
        return completions;
    }
}