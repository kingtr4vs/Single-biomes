package com.kingtravs.singlebiomes;

import org.bukkit.Bukkit;
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
import java.util.stream.Collectors;

/**
 * Command handler for SingleBiomes plugin
 * Handles all plugin commands and tab completion
 * 
 * @author KingTravs
 * @version 1.0.0
 */
public class SingleBiomesCommand implements CommandExecutor, TabCompleter {
    
    private final SingleBiomes plugin;
    private final List<String> supportedBiomes;
    
    public SingleBiomesCommand(SingleBiomes plugin) {
        this.plugin = plugin;
        this.supportedBiomes = Arrays.asList(
            "SNOWY_TAIGA", "BADLANDS", "DESERT", "PLAINS", "MUSHROOM_FIELDS"
        );
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelpMessage(sender);
            return true;
        }
        
        if (args[0].equalsIgnoreCase("generate")) {
            return handleGenerateCommand(sender, args);
        }
        
        sender.sendMessage(ChatColor.RED + "Unknown command. Use /singlebiomes help for available commands.");
        return true;
    }
    
    /**
     * Handle the generate command
     */
    private boolean handleGenerateCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /singlebiomes generate <worldName> <biome>");
            sender.sendMessage(ChatColor.YELLOW + "Supported biomes: " + String.join(", ", supportedBiomes));
            return true;
        }
        
        String worldName = args[1];
        String biome = args[2].toUpperCase();
        
        // Validate biome
        if (!supportedBiomes.contains(biome)) {
            sender.sendMessage(ChatColor.RED + "Unsupported biome: " + biome);
            sender.sendMessage(ChatColor.YELLOW + "Supported biomes: " + String.join(", ", supportedBiomes));
            return true;
        }
        
        // Check if world already exists
        if (Bukkit.getWorld(worldName) != null) {
            sender.sendMessage(ChatColor.RED + "World '" + worldName + "' already exists!");
            return true;
        }
        
        // Validate world name
        if (!isValidWorldName(worldName)) {
            sender.sendMessage(ChatColor.RED + "Invalid world name. Use only letters, numbers, and underscores.");
            return true;
        }
        
        // Generate world
        sender.sendMessage(ChatColor.YELLOW + "Generating world '" + worldName + "' with biome " + biome + "...");
        
        try {
            WorldCreator creator = new WorldCreator(worldName);
            creator.generator(new BiomeWorldGenerator(plugin, biome));
            
            World world = creator.createWorld();
            
            if (world != null) {
                sender.sendMessage(ChatColor.GREEN + "✓ World '" + worldName + "' generated successfully!");
                sender.sendMessage(ChatColor.AQUA + "Use /mv tp " + worldName + " to teleport (if Multiverse is installed)");
                sender.sendMessage(ChatColor.AQUA + "Or use /tp @s " + world.getSpawnLocation().getBlockX() + " " + 
                                 world.getSpawnLocation().getBlockY() + " " + world.getSpawnLocation().getBlockZ() + 
                                 " " + worldName);
                
                plugin.getLogger().info("World '" + worldName + "' generated with biome " + biome + " by " + sender.getName());
            } else {
                sender.sendMessage(ChatColor.RED + "✗ Failed to generate world '" + worldName + "'");
            }
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "✗ Error generating world: " + e.getMessage());
            plugin.getLogger().severe("Error generating world '" + worldName + "': " + e.getMessage());
            e.printStackTrace();
        }
        
        return true;
    }
    
    /**
     * Send help message to command sender
     */
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "╔═══════════════════════════════════════╗");
        sender.sendMessage(ChatColor.GREEN + "║         " + ChatColor.BOLD + "SingleBiomes Commands" + ChatColor.RESET + ChatColor.GREEN + "         ║");
        sender.sendMessage(ChatColor.GREEN + "╠═══════════════════════════════════════╣");
        sender.sendMessage(ChatColor.GREEN + "║" + ChatColor.YELLOW + " /singlebiomes help                   " + ChatColor.GREEN + "║");
        sender.sendMessage(ChatColor.GREEN + "║" + ChatColor.WHITE + "   Shows this help message            " + ChatColor.GREEN + "║");
        sender.sendMessage(ChatColor.GREEN + "║                                       ║");
        sender.sendMessage(ChatColor.GREEN + "║" + ChatColor.YELLOW + " /singlebiomes generate <world> <biome>" + ChatColor.GREEN + "║");
        sender.sendMessage(ChatColor.GREEN + "║" + ChatColor.WHITE + "   Creates a new SingleBiomes world    " + ChatColor.GREEN + "║");
        sender.sendMessage(ChatColor.GREEN + "╠═══════════════════════════════════════╣");
        sender.sendMessage(ChatColor.GREEN + "║" + ChatColor.BOLD + "Supported Biomes:" + ChatColor.RESET + "                 " + ChatColor.GREEN + "║");
        
        for (String biome : supportedBiomes) {
            String displayName = biome.toLowerCase().replace("_", " ");
            displayName = displayName.substring(0, 1).toUpperCase() + displayName.substring(1);
            sender.sendMessage(ChatColor.GREEN + "║" + ChatColor.AQUA + " • " + displayName + 
                             " ".repeat(Math.max(1, 32 - displayName.length())) + ChatColor.GREEN + "║");
        }
        
        sender.sendMessage(ChatColor.GREEN + "╚═══════════════════════════════════════╝");
        
        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.GRAY + "Multiverse integration: /mv create <world> normal -g SingleBiomes:<BIOME>");
        }
    }
    
    /**
     * Validate world name
     */
    private boolean isValidWorldName(String name) {
        return name.matches("^[a-zA-Z0-9_]+$") && name.length() > 0 && name.length() <= 16;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // First argument - subcommands
            List<String> subcommands = Arrays.asList("help", "generate");
            return subcommands.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("generate")) {
            // Second argument for generate - world name (no completions, user types their own)
            return new ArrayList<>();
        } else if (args.length == 3 && args[0].equalsIgnoreCase("generate")) {
            // Third argument for generate - biome names
            return supportedBiomes.stream()
                    .filter(biome -> biome.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        return completions;
    }
}