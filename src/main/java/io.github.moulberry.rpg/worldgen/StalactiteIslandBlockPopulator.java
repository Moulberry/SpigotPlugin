package io.github.moulberry.rpg.worldgen;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class StalactiteIslandBlockPopulator extends BlockPopulator {

    public Material[] blocks;
    public int minCount = 0;
    public int maxCount = 1;

    public StalactiteIslandBlockPopulator(Material... blocks) {
        this.blocks = blocks;
    }

    public void populate(World world, Random random, Chunk source) {
        for(int x=0; x<16; x++) {
            for(int z=0; z<16; z++) {
                boolean airBelow = true;

                for(int y = 0; y < world.getMaxHeight()-1; y++) {
                    Block b = source.getBlock(x, y, z);

                    if(b.getType() == Material.AIR) {
                        airBelow = true;
                        continue;
                    } else if(contains(b.getType()) && airBelow) {
                        int height = random.nextInt(maxCount-minCount+1)+minCount;
                        createStalactite(source, b.getType(), x, y-1, z, height);
                    }

                    airBelow = false;
                }
            }
        }
    }

    private void createStalactite(Chunk chunk, Material block, int x, int y, int z, int height) {
        for(int i=y; i>=0 && i>y-height; i--) {
            if(chunk.getBlock(x, y, z).getType() != Material.AIR) return;
            chunk.getBlock(x, y, z).setType(block);
        }
    }

    private boolean contains(Material block) {
        for(Material mat : blocks) {
            if(mat == block) return true;
        }
        return false;
    }
}
