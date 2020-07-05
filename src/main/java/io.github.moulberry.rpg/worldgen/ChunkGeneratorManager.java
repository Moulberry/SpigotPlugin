package io.github.moulberry.rpg.worldgen;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class ChunkGeneratorManager {

    private static final ChunkGeneratorManager INSTANCE = new ChunkGeneratorManager();

    public static ChunkGeneratorManager getInstance() {
        return INSTANCE;
    }

    public ChunkGenerator getGeneratorForString(String name) {
        if(name.equalsIgnoreCase("aether")) {
            return new ChunkGeneratorAether();
        }

        return getEmptyGenerator();
    }

    public ChunkGenerator getEmptyGenerator() {
        return new ChunkGenerator() {
            public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
                return Bukkit.createChunkData(world);
            }
        };
    }

}
