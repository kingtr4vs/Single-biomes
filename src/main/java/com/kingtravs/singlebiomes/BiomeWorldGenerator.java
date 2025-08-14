package com.kingtravs.singlebiomes;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Custom world generator for SingleBiomes plugin
 * Generates worlds with single biomes and stepped terrain
 * 
 * @author KingTravs
 * @version 1.0.0
 */
public class BiomeWorldGenerator extends ChunkGenerator {
    
    private final SingleBiomes plugin;
    private final String targetBiome;
    private final Map<String, Material> biomeBlocks;
    private final int stepWidth;
    private final int baseHeight;
    
    public BiomeWorldGenerator(SingleBiomes plugin) {
        this(plugin, "PLAINS");
    }
    
    public BiomeWorldGenerator(SingleBiomes plugin, String biome) {
        this.plugin = plugin;
        this.targetBiome = biome.toUpperCase();
        this.stepWidth = plugin.getConfig().getInt("step-width", 20);
        this.baseHeight = plugin.getConfig().getInt("base-height", 64);
        
        // Initialize biome-specific blocks
        this.biomeBlocks = new HashMap<>();
        initializeBiomeBlocks();
    }
    
    /**
     * Initialize block types for each supported biome
     */
    private void initializeBiomeBlocks() {
        biomeBlocks.put("SNOWY_TAIGA", Material.SNOW_BLOCK);
        biomeBlocks.put("BADLANDS", Material.TERRACOTTA);
        biomeBlocks.put("DESERT", Material.SAND);
        biomeBlocks.put("PLAINS", Material.GRASS_BLOCK);
        biomeBlocks.put("MUSHROOM_FIELDS", Material.MYCELIUM);
    }
    
    @Override
    public void generateNoise(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        Material surfaceBlock = biomeBlocks.getOrDefault(targetBiome, Material.GRASS_BLOCK);
        Material undergroundBlock = getUndergroundBlock(surfaceBlock);
        
        // Generate stepped terrain with exactly 1 block high steps
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                // Calculate world coordinates
                int worldX = chunkX * 16 + x;
                int worldZ = chunkZ * 16 + z;
                
                // Calculate step height - EXACTLY 1 block per step
                int stepX = worldX / stepWidth;
                int stepZ = worldZ / stepWidth;
                
                // Each step is exactly 1 block higher than the previous
                int stepHeight = stepX + stepZ;
                int surfaceY = baseHeight + stepHeight;
                
                // Ensure we don't exceed world height limits
                surfaceY = Math.min(surfaceY, worldInfo.getMaxHeight() - 10);
                
                // Generate terrain layers
                generateTerrainColumn(chunkData, x, z, surfaceY, surfaceBlock, undergroundBlock);
            }
        }
    }
    
    /**
     * Generate a single terrain column
     */
    private void generateTerrainColumn(ChunkData chunkData, int x, int z, int surfaceY, Material surfaceBlock, Material undergroundBlock) {
        // Bedrock layer
        chunkData.setBlock(x, 0, z, Material.BEDROCK);
        
        // Underground layers (stone/material specific)
        for (int y = 1; y < surfaceY - 3; y++) {
            chunkData.setBlock(x, y, z, Material.STONE);
        }
        
        // Sub-surface layers (3 blocks of underground material)
        for (int y = Math.max(1, surfaceY - 3); y < surfaceY; y++) {
            chunkData.setBlock(x, y, z, undergroundBlock);
        }
        
        // Surface block
        if (surfaceY > 0) {
            chunkData.setBlock(x, surfaceY, z, surfaceBlock);
        }
    }
    
    /**
     * Get appropriate underground block for surface material
     */
    private Material getUndergroundBlock(Material surfaceBlock) {
        switch (surfaceBlock) {
            case GRASS_BLOCK:
                return Material.DIRT;
            case SAND:
                return Material.SANDSTONE;
            case SNOW_BLOCK:
                return Material.DIRT;
            case MYCELIUM:
                return Material.DIRT;
            case TERRACOTTA:
                return Material.RED_SANDSTONE;
            default:
                return Material.DIRT;
        }
    }
    
    @Override
    public BiomeProvider getDefaultBiomeProvider(WorldInfo worldInfo) {
        return new SingleBiomeProvider();
    }
    
    /**
     * Custom biome provider that sets the entire world to a single biome
     */
    private class SingleBiomeProvider extends BiomeProvider {
        
        @Override
        public Biome getBiome(WorldInfo worldInfo, int x, int y, int z) {
            try {
                return Biome.valueOf(targetBiome);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid biome: " + targetBiome + ", defaulting to PLAINS");
                return Biome.PLAINS;
            }
        }
        
        @Override
        public java.util.List<Biome> getBiomes(WorldInfo worldInfo) {
            try {
                return java.util.List.of(Biome.valueOf(targetBiome));
            } catch (IllegalArgumentException e) {
                return java.util.List.of(Biome.PLAINS);
            }
        }
    }
}