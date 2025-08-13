package dev.kingtravs.singlebiomes.generator;

import dev.kingtravs.singlebiomes.SingleBiomes;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;

import java.util.Collections;
import java.util.List;

/**
 * Biome provider for SingleBiomes worlds
 * Sets the entire world to use a single specified biome
 * 
 * @author KingTravs
 * @version 1.0.0
 * @since 1.0.0
 */
public class SingleBiomePopulator extends BiomeProvider {
    
    private final Biome targetBiome;
    
    public SingleBiomePopulator(String biomeName) {
        try {
            this.targetBiome = Biome.valueOf(biomeName.toUpperCase());
            SingleBiomes.getInstance().getLogger().info("Initialized biome provider for: " + targetBiome.name());
        } catch (IllegalArgumentException e) {
            SingleBiomes.getInstance().getLogger().severe("Invalid biome name: " + biomeName);
            throw new RuntimeException("Invalid biome: " + biomeName, e);
        }
    }
    
    @Override
    public Biome getBiome(WorldInfo worldInfo, int x, int y, int z) {
        return targetBiome;
    }
    
    @Override
    public List<Biome> getBiomes(WorldInfo worldInfo) {
        return Collections.singletonList(targetBiome);
    }
    
    /**
     * Gets the target biome for this world
     * @return The target biome
     */
    public Biome getTargetBiome() {
        return targetBiome;
    }
}