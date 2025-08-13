package dev.kingtravs.singlebiomes.generator;

import dev.kingtravs.singlebiomes.SingleBiomes;
import dev.kingtravs.singlebiomes.utils.ConfigManager;
import org.bukkit.Material;
import org.bukkit.World;

import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.Map;
import java.util.Random;

/**
 * Custom chunk generator for SingleBiomes worlds
 * Generates plateau-style terrain with stepped, staircase-like formations
 * 
 * @author KingTravs
 * @version 1.0.0
 * @since 1.0.0
 */
public class SingleBiomeChunkGenerator extends ChunkGenerator {
    
    private final String biome;
    private final ConfigManager config;
    private SimplexOctaveGenerator noiseGenerator;
    
    public SingleBiomeChunkGenerator(String biome) {
        this.biome = biome;
        this.config = SingleBiomes.getInstance().getConfigManager();
    }
    
    @Override
    public void generateNoise(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        // Initialize noise generator if not already done
        if (noiseGenerator == null) {
            noiseGenerator = new SimplexOctaveGenerator(new Random(worldInfo.getSeed()), 8);
            noiseGenerator.setScale(0.005D);
        }
        
        // Get configuration values
        int stepWidth = config.getPlateauStepWidth();
        int stepHeight = config.getPlateauStepHeight();
        int baseHeight = config.getBaseHeight();
        int minHeight = config.getMinHeight();
        int maxHeight = config.getMaxHeight();
        
        // Get block types for this biome
        Map<String, Material> blocks = config.getBiomeBlocks(biome);
        Material surface = blocks.get("surface");
        Material subsurface = blocks.get("subsurface");
        Material base = blocks.get("base");
        
        // Generate terrain for each column in the chunk
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                // Calculate world coordinates
                int worldX = chunkX * 16 + x;
                int worldZ = chunkZ * 16 + z;
                
                // Generate plateau height using stepped function
                int plateauHeight = generatePlateauHeight(worldX, worldZ, stepWidth, stepHeight, baseHeight, minHeight, maxHeight);
                
                // Add some noise variation
                double noise = noiseGenerator.noise(worldX, worldZ, 0.5, 0.5) * 3;
                int finalHeight = Math.max(minHeight, Math.min(maxHeight, plateauHeight + (int) noise));
                
                // Generate column
                generateColumn(chunkData, x, z, finalHeight, surface, subsurface, base);
            }
        }
    }
    
    /**
     * Generates the plateau height for given coordinates
     * @param x World X coordinate
     * @param z World Z coordinate
     * @param stepWidth Width of each step
     * @param stepHeight Height of each step
     * @param baseHeight Base height level
     * @param minHeight Minimum height
     * @param maxHeight Maximum height
     * @return The calculated plateau height
     */
    private int generatePlateauHeight(int x, int z, int stepWidth, int stepHeight, int baseHeight, int minHeight, int maxHeight) {
        // Create stepped terrain using modulo operations
        int stepX = x / stepWidth;
        int stepZ = z / stepWidth;
        
        // Create a pattern that varies height based on step coordinates
        int heightVariation = ((stepX + stepZ) % 8) * stepHeight;
        
        // Add some larger scale variation
        int largeVariation = ((stepX / 3 + stepZ / 3) % 4) * stepHeight * 2;
        
        // Calculate final height
        int height = baseHeight + heightVariation + largeVariation;
        
        // Ensure height is within bounds
        return Math.max(minHeight, Math.min(maxHeight, height));
    }
    
    /**
     * Generates a single column of blocks
     * @param chunkData The chunk data to modify
     * @param x Local X coordinate (0-15)
     * @param z Local Z coordinate (0-15)
     * @param height The height to generate to
     * @param surface Surface block material
     * @param subsurface Subsurface block material
     * @param base Base block material
     */
    private void generateColumn(ChunkData chunkData, int x, int z, int height, Material surface, Material subsurface, Material base) {
        // Generate bedrock layer
        chunkData.setBlock(x, 0, z, Material.BEDROCK);
        
        // Generate base layer (stone)
        for (int y = 1; y < Math.min(height - 3, chunkData.getMaxHeight()); y++) {
            chunkData.setBlock(x, y, z, base);
        }
        
        // Generate subsurface layer (dirt/sandstone)
        for (int y = Math.max(1, height - 3); y < Math.min(height, chunkData.getMaxHeight()); y++) {
            chunkData.setBlock(x, y, z, subsurface);
        }
        
        // Generate surface layer (grass/sand)
        if (height < chunkData.getMaxHeight()) {
            chunkData.setBlock(x, height, z, surface);
        }
        
        // Add water for low areas
        if (height < 63) {
            for (int y = height + 1; y <= 63 && y < chunkData.getMaxHeight(); y++) {
                chunkData.setBlock(x, y, z, Material.WATER);
            }
        }
    }
    
    @Override
    public void generateBedrock(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        // Bedrock is already handled in generateNoise method
    }
    
    @Override
    public void generateCaves(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        // Simple cave generation can be added here if desired
        // For now, we'll keep it simple without caves
    }
    
    @Override
    public void generateSurface(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        // Surface generation is handled in generateNoise method
    }
    
    @Override
    public boolean shouldGenerateNoise() {
        return true;
    }
    
    @Override
    public boolean shouldGenerateSurface() {
        return false; // We handle surface in generateNoise
    }
    
    @Override
    public boolean shouldGenerateBedrock() {
        return false; // We handle bedrock in generateNoise
    }
    
    @Override
    public boolean shouldGenerateCaves() {
        return false; // We don't generate caves for now
    }
    
    @Override
    public boolean shouldGenerateDecorations() {
        return config.generateDecorations();
    }
    
    @Override
    public boolean shouldGenerateMobs() {
        return true; // Allow mob spawning
    }
    
    @Override
    public boolean shouldGenerateStructures() {
        return config.generateStructures();
    }
    
    @Override
    public BiomeProvider getDefaultBiomeProvider(WorldInfo worldInfo) {
        return new SingleBiomePopulator(biome);
    }
}