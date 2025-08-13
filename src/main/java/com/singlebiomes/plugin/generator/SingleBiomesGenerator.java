package com.singlebiomes.plugin.generator;

import com.singlebiomes.plugin.ConfigManager;
import com.singlebiomes.plugin.SingleBiomesPlugin;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import java.util.Random;
import java.util.logging.Level;

/**
 * Custom world generator for SingleBiomes worlds
 * Creates stepped plateau terrain with a single biome
 * 
 * @author SingleBiomes Team
 * @version 1.0.0
 */
public class SingleBiomesGenerator extends ChunkGenerator {
    
    private final SingleBiomesPlugin plugin;
    private final Biome targetBiome;
    private final ConfigManager.BiomeBlockConfig blockConfig;
    private final ConfigManager configManager;
    
    // Generation settings
    private final int plateauStepHeight;
    private final int plateauStepWidth;
    private final int baseHeight;
    private final int maxHeightVariation;
    
    public SingleBiomesGenerator(SingleBiomesPlugin plugin, String biomeId) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        
        // Parse biome from ID
        this.targetBiome = parseBiome(biomeId);
        
        // Get block configuration for this biome
        this.blockConfig = configManager.getBlockConfig(targetBiome);
        
        // Load generation settings
        this.plateauStepHeight = configManager.getPlateauStepHeight();
        this.plateauStepWidth = configManager.getPlateauStepWidth();
        this.baseHeight = configManager.getBaseHeight();
        this.maxHeightVariation = configManager.getMaxHeightVariation();
        
        if (configManager.isDebugEnabled()) {
            plugin.getLogger().info("Created generator for biome: " + targetBiome + 
                " (step height: " + plateauStepHeight + ", step width: " + plateauStepWidth + ")");
        }
    }
    
    /**
     * Parse biome from string ID
     */
    private Biome parseBiome(String biomeId) {
        if (biomeId == null || biomeId.isEmpty()) {
            return Biome.PLAINS; // Default fallback
        }
        
        try {
            Biome biome = Biome.valueOf(biomeId.toUpperCase());
            
            // Validate that this biome is enabled
            if (!configManager.getEnabledBiomes().contains(biome)) {
                plugin.getLogger().warning("Biome " + biome + " is not enabled in config, using PLAINS instead");
                return Biome.PLAINS;
            }
            
            return biome;
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid biome ID: " + biomeId + ", using PLAINS instead");
            return Biome.PLAINS;
        }
    }
    
    /**
     * Generate chunk data with stepped plateau terrain
     */
    @Override
    public void generateNoise(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        try {
            if (configManager.isLogGenerationEnabled()) {
                plugin.getLogger().info("Generating chunk at " + chunkX + ", " + chunkZ + " for biome " + targetBiome);
            }
            
            // Generate terrain for each column in the chunk
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    generateColumn(chunkData, chunkX * 16 + x, chunkZ * 16 + z, x, z, random);
                }
            }
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error generating chunk at " + chunkX + ", " + chunkZ, e);
        }
    }
    
    /**
     * Generate a single column of blocks
     */
    private void generateColumn(ChunkData chunkData, int worldX, int worldZ, int chunkX, int chunkZ, Random random) {
        // Calculate height using stepped plateau algorithm
        int height = calculatePlateauHeight(worldX, worldZ, random);
        
        // Ensure height is within world limits
        height = Math.max(chunkData.getMinHeight(), Math.min(height, chunkData.getMaxHeight() - 1));
        
        // Generate bedrock layer
        chunkData.setBlock(chunkX, chunkData.getMinHeight(), chunkZ, Material.BEDROCK);
        
        // Generate stone layer
        for (int y = chunkData.getMinHeight() + 1; y < height - 3; y++) {
            chunkData.setBlock(chunkX, y, chunkZ, blockConfig.stone);
        }
        
        // Generate subsurface layer (3 blocks)
        for (int y = Math.max(chunkData.getMinHeight() + 1, height - 3); y < height; y++) {
            chunkData.setBlock(chunkX, y, chunkZ, blockConfig.subsurface);
        }
        
        // Generate surface block
        if (height >= chunkData.getMinHeight() && height < chunkData.getMaxHeight()) {
            chunkData.setBlock(chunkX, height, chunkZ, blockConfig.surface);
            
            // Add decorations randomly
            if (random.nextDouble() < 0.001 && height + 1 < chunkData.getMaxHeight()) { // 0.1% chance
                chunkData.setBlock(chunkX, height + 1, chunkZ, blockConfig.decoration);
            }
        }
    }
    
    /**
     * Calculate height using stepped plateau algorithm
     */
    private int calculatePlateauHeight(int x, int z, Random random) {
        // Create stepped plateau effect
        int stepX = x / plateauStepWidth;
        int stepZ = z / plateauStepWidth;
        
        // Use step coordinates as seed for consistent height per plateau
        Random stepRandom = new Random((long) stepX * 1000000L + stepZ);
        
        // Calculate base height for this plateau step
        int stepHeight = baseHeight + (stepRandom.nextInt(maxHeightVariation) - maxHeightVariation / 2);
        
        // Add some variation within the step (smaller scale)
        int inStepX = x % plateauStepWidth;
        int inStepZ = z % plateauStepWidth;
        
        // Smooth edges of plateaus
        double edgeDistance = Math.min(
            Math.min(inStepX, plateauStepWidth - inStepX),
            Math.min(inStepZ, plateauStepWidth - inStepZ)
        );
        
        double edgeFactor = Math.min(1.0, edgeDistance / (plateauStepWidth * 0.1));
        
        // Add small random variation
        int variation = (int) (stepRandom.nextGaussian() * plateauStepHeight * 0.5 * edgeFactor);
        
        return stepHeight + variation;
    }
    
    /**
     * Provide custom biome provider that sets the entire world to the target biome
     */
    @Override
    public BiomeProvider getDefaultBiomeProvider(WorldInfo worldInfo) {
        return new SingleBiomeProvider(targetBiome);
    }
    
    /**
     * Check if the generator should generate caves
     */
    @Override
    public boolean shouldGenerateCaves() {
        return true; // Allow vanilla cave generation
    }
    
    /**
     * Check if the generator should generate decorations
     */
    @Override
    public boolean shouldGenerateDecorations() {
        return true; // Allow vanilla decorations appropriate for the biome
    }
    
    /**
     * Check if the generator should generate mobs
     */
    @Override
    public boolean shouldGenerateMobs() {
        return true; // Allow mob spawning
    }
    
    /**
     * Check if the generator should generate structures
     */
    @Override
    public boolean shouldGenerateStructures() {
        return plugin.getConfig().getBoolean("world-settings.generate-structures", true);
    }
    
    /**
     * Get the target biome for this generator
     */
    public Biome getTargetBiome() {
        return targetBiome;
    }
    
    /**
     * Get block configuration
     */
    public ConfigManager.BiomeBlockConfig getBlockConfig() {
        return blockConfig;
    }
}