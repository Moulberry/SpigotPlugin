package io.github.moulberry.rpg.worldgen;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.PerlinNoiseGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChunkGeneratorAether extends ChunkGenerator {

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
        ChunkData data = createChunkData(world);

        if(Math.abs(chunkX*16) > 120 || Math.abs(chunkZ*16) > 120) {
            return data;
        }

        SimplexNoiseGenerator generator = new SimplexNoiseGenerator(world.getSeed());

        //48->112
        for(int x=0; x<16; x++) {
            for(int z=0; z<16; z++) {
                int airHeight = 100;
                for(int y=64; y>0; y--) {
                    double worldX = chunkX*16+x;
                    double worldZ = chunkZ*16+z;

                    if(Math.abs(worldX) > 100 || Math.abs(worldZ) > 100) {
                        continue;
                    }

                    double density = generator.noise((worldX)/80d, y/30d, (worldZ)/80d, 3, 1.5, 0.8, true);

                    double factor = Math.abs(y-32)/32d;
                    factor *= factor;
                    factor = 1-factor;

                    double factorBounds = 1-Math.max(0, (Math.max(Math.abs(worldX), Math.abs(worldZ))-80)/20d);
                    factorBounds *= factorBounds;

                    if(density*factor*factorBounds > 0.2) {
                        Material material = Material.STONE;

                        if(airHeight == y+1) {
                            material = Material.GRASS;
                        } else if(airHeight-y <= 4+random.nextInt(3)) {
                            material = Material.DIRT;
                        }

                        data.setBlock(x, 48+y, z, material);
                    } else {
                        airHeight = y;
                    }
                }
            }


        }

        return data;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        world.getPopulators().clear(); //for some reason populators run twice?

        List<BlockPopulator> populators = new ArrayList<>();
        populators.add(new BiomeBlockPopulator(Biome.JUNGLE));
        populators.add(new StalactiteIslandBlockPopulator(Material.STONE, Material.DIRT));
        populators.add(new SpawnLocationBlockPopulator(Material.GLOWSTONE, 100, 20, 120, 60));
        populators.add(new TreeBlockPopulator(0, 4));
        populators.add(new PoleBlockPopulator());
        populators.add(new GrassBlockPopulator());

        OreGenBlockPopulator oreGenBlockPopulatorGO = new OreGenBlockPopulator(Material.GOLD_ORE);
        oreGenBlockPopulatorGO.spawnsPerChunk = 20;
        oreGenBlockPopulatorGO.spawnChance = 0.75f;
        oreGenBlockPopulatorGO.minOreCount = 8;
        oreGenBlockPopulatorGO.maxOreCount = 16;
        oreGenBlockPopulatorGO.extraOreChance = 0.9f;
        oreGenBlockPopulatorGO.minY = 48;
        oreGenBlockPopulatorGO.maxY = 100;

        OreGenBlockPopulator oreGenBlockPopulatorGB = new OreGenBlockPopulator(Material.GOLD_BLOCK);
        oreGenBlockPopulatorGB.spawnsPerChunk = 6;
        oreGenBlockPopulatorGB.spawnChance = 0.67f;
        oreGenBlockPopulatorGB.minOreCount = 1;
        oreGenBlockPopulatorGB.maxOreCount = 3;
        oreGenBlockPopulatorGB.extraOreChance = 0.4f;
        oreGenBlockPopulatorGB.minY = 48;
        oreGenBlockPopulatorGB.maxY = 100;

        OreGenBlockPopulator oreGenBlockPopulatorEO = new OreGenBlockPopulator(Material.EMERALD_ORE);
        oreGenBlockPopulatorEO.spawnsPerChunk = 4;
        oreGenBlockPopulatorEO.minOreCount = 3;
        oreGenBlockPopulatorEO.maxOreCount = 6;
        oreGenBlockPopulatorEO.extraOreChance = 0.7f;
        oreGenBlockPopulatorEO.minY = 48;
        oreGenBlockPopulatorEO.maxY = 100;

        populators.add(oreGenBlockPopulatorGO);
        populators.add(oreGenBlockPopulatorGB);
        populators.add(oreGenBlockPopulatorEO);

        return populators;
    }
}
