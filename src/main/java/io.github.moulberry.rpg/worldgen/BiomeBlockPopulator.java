package io.github.moulberry.rpg.worldgen;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class BiomeBlockPopulator extends BlockPopulator {

    private Biome biome;

    public BiomeBlockPopulator(Biome biome) {
        this.biome = biome;
    }

    @Override
    public void populate(World world, Random random, Chunk source) {
        for(int x=0; x<16; x++) {
            for (int z = 0; z < 16; z++) {
                source.getBlock(x, 80, z).setBiome(biome);
            }
        }
    }
}
