package io.github.moulberry.rpg.worldgen;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class OreGenBlockPopulator extends BlockPopulator {

    int minY = 0;
    int maxY = 100;
    Material replace = Material.STONE;
    Material ore;
    int spawnsPerChunk = 1;
    float spawnChance = 1f;
    int minOreCount = 4;
    int maxOreCount = 10;
    float extraOreChance = 0.5f;

    public OreGenBlockPopulator(Material ore) {
        this.ore = ore;
    }

    public void populate(World world, Random random, Chunk source) {
        minY = Math.max(0, minY);
        maxY = Math.min(world.getMaxHeight()-1, maxY);

        for(int i=0; i<spawnsPerChunk; i++) {
            if(random.nextFloat() <= spawnChance) {
                int baseX = random.nextInt(16);
                int baseZ = random.nextInt(16);

                int minYWorld;
                for(minYWorld = minY; source.getBlock(baseX, minYWorld, baseZ).getType() == Material.AIR && minYWorld < maxY; minYWorld++);


                int maxYWorld;
                for(maxYWorld = maxY; source.getBlock(baseX, maxYWorld, baseZ).getType() == Material.AIR && maxYWorld > minY; maxYWorld--);

                if(minYWorld >= maxYWorld) {
                    return;
                }

                int baseY = random.nextInt(maxYWorld-minYWorld)+minYWorld;

                int oreCount;
                for(oreCount=minOreCount; oreCount <= maxOreCount && random.nextFloat() < extraOreChance; oreCount++);

                for(int j=0; j<oreCount; j++) {
                    Block b = world.getBlockAt(source.getX()*16+baseX, baseY, source.getZ()*16+baseZ);
                    if(b.getType() != replace && b.getType() != ore) break;
                    b.setType(ore);
                    switch(random.nextInt(6)) {
                        case 0: baseX++; break;
                        case 1: baseX--; break;
                        case 2: baseZ++; break;
                        case 3: baseZ--; break;
                        case 4: baseY++; break;
                        case 5: baseY--; break;
                    }
                }
            }
        }
    }

    public void replaceBlock(World world, Chunk chunk, int x, int y, int z, Material from, Material to) {
        if(world.getBlockAt(chunk.getX()*16+x, y, chunk.getZ()*16+z).getType() == from) {
            world.getBlockAt(chunk.getX()*16+x, y, chunk.getZ()*16+z).setType(to);
        }
    }
}
