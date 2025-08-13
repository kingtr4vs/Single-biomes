package com.singlebiomes.plugin.generator;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;

import java.util.Collections;
import java.util.List;

/**
 * Biome provider that provides a single biome for the entire world
 * 
 * @author SingleBiomes Team
 * @version 1.0.0
 */
public class SingleBiomeProvider extends BiomeProvider {
    
    private final Biome targetBiome;
    private final List<Biome> biomeList;
    
    public SingleBiomeProvider(Biome targetBiome) {
        this.targetBiome = targetBiome;
        this.biomeList = Collections.singletonList(targetBiome);
    }
    
    /**
     * Get the biome at specific coordinates
     * Always returns the target biome regardless of coordinates
     */
    @Override
    public Biome getBiome(WorldInfo worldInfo, int x, int y, int z) {
        return targetBiome;
    }
    
    /**
     * Get list of all biomes that can be generated
     * Returns only the target biome
     */
    @Override
    public List<Biome> getBiomes(WorldInfo worldInfo) {
        return biomeList;
    }
    
    /**
     * Get the target biome
     */
    public Biome getTargetBiome() {
        return targetBiome;
    }
}