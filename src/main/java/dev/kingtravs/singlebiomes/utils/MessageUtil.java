package dev.kingtravs.singlebiomes.utils;

import dev.kingtravs.singlebiomes.SingleBiomes;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Message utility for SingleBiomes plugin
 * 
 * @author KingTravs
 * @version 1.0.0
 * @since 1.0.0
 */
public class MessageUtil {
    
    private final SingleBiomes plugin;
    
    public MessageUtil(SingleBiomes plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Sends a formatted message to a command sender
     * @param sender The command sender
     * @param messageKey The message key from config
     * @param placeholders Placeholders to replace in the message
     */
    public void sendMessage(CommandSender sender, String messageKey, String... placeholders) {
        String message = plugin.getConfigManager().getMessage(messageKey);
        message = formatMessage(message, placeholders);
        sender.sendMessage(message);
    }
    
    /**
     * Sends a prefixed message to a command sender
     * @param sender The command sender
     * @param messageKey The message key from config
     * @param placeholders Placeholders to replace in the message
     */
    public void sendPrefixedMessage(CommandSender sender, String messageKey, String... placeholders) {
        String prefix = plugin.getConfigManager().getPrefix();
        String message = plugin.getConfigManager().getMessage(messageKey);
        message = formatMessage(prefix + message, placeholders);
        sender.sendMessage(message);
    }
    
    /**
     * Sends a raw message with color codes
     * @param sender The command sender
     * @param message The raw message
     */
    public void sendRawMessage(CommandSender sender, String message) {
        sender.sendMessage(formatColors(message));
    }
    
    /**
     * Formats a message with placeholders and color codes
     * @param message The message to format
     * @param placeholders Placeholders in format {key}, value, {key2}, value2, etc.
     * @return The formatted message
     */
    public String formatMessage(String message, String... placeholders) {
        if (placeholders.length % 2 != 0) {
            plugin.getLogger().warning("Invalid placeholder count in formatMessage. Must be even.");
            return formatColors(message);
        }
        
        for (int i = 0; i < placeholders.length; i += 2) {
            String placeholder = placeholders[i];
            String value = placeholders[i + 1];
            message = message.replace("{" + placeholder + "}", value);
        }
        
        return formatColors(message);
    }
    
    /**
     * Formats color codes in a message
     * @param message The message to format
     * @return The message with formatted colors
     */
    public String formatColors(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    /**
     * Sends help messages to a command sender
     * @param sender The command sender
     * @param enabledBiomes List of enabled biomes
     */
    public void sendHelpMessage(CommandSender sender, java.util.List<String> enabledBiomes) {
        sendMessage(sender, "help-header");
        sendMessage(sender, "help-generate");
        sendMessage(sender, "help-footer", "biomes", String.join(", ", enabledBiomes));
    }
    
    /**
     * Logs a debug message if debug mode is enabled
     * @param message The debug message
     */
    public void debug(String message) {
        if (plugin.getConfigManager().isDebugMode()) {
            plugin.getLogger().info("[DEBUG] " + message);
        }
    }
    
    /**
     * Logs an info message
     * @param message The info message
     */
    public void info(String message) {
        plugin.getLogger().info(message);
    }
    
    /**
     * Logs a warning message
     * @param message The warning message
     */
    public void warning(String message) {
        plugin.getLogger().warning(message);
    }
    
    /**
     * Logs an error message
     * @param message The error message
     */
    public void error(String message) {
        plugin.getLogger().severe(message);
    }
}