package io.github.moulberry.rpg.worldgen;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class SpawnLocationBlockPopulator extends BlockPopulator {

    private Material failBlock;
    private int failHeight;
    private int radius;
    private int maxSearch;
    private int minSearch;

    public SpawnLocationBlockPopulator(Material failBlock, int failHeight, int radius, int maxSearch, int minSearch) {
        this.failBlock = failBlock;
        this.failHeight = failHeight;
        this.radius = radius;
        this.maxSearch = maxSearch;
        this.minSearch = minSearch;
    }

    public void populate(World world, Random random, Chunk chunk) {
        if(chunk.getX() != 0 || chunk.getZ() != 0) {
            return;
        }

        int x = 0, z = 0;
        int dx = 1;
        int dz = 0;

        //stolen from https://stackoverflow.com/questions/398299/looping-in-a-spiral
        for(int i=0; i<radius*radius; i++) {
            int y;
            for(y = maxSearch-1; !world.getBlockAt(x, y, z).getType().isOccluding() && y > 0; y--);
            if(y != 0) {
                world.setSpawnLocation(x, y+1, z);
                return;
            }

            x += dx;
            z += dz;

            if(x == z || (x < 0 && x == -z) || (x > 0 && x == 1-z)) {
                int t = dx;
                dx = -dz;
                dz = t;
            }
        }

        world.getBlockAt(0, failHeight-1, 0).setType(failBlock);
        world.setSpawnLocation(0, failHeight, 0);

    }

}