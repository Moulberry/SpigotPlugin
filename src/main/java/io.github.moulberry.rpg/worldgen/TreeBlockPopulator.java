package io.github.moulberry.rpg.worldgen;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class TreeBlockPopulator extends BlockPopulator {

    int treeType;
    int count;

    public TreeBlockPopulator(int treeType, int count) {
        this.treeType = treeType;
        this.count = count;
    }

    public void populate(World world, Random random, Chunk source) {
        int trees = random.nextInt(count);

        for(int i=0; i<trees; i++) {
            int baseX = random.nextInt(16);
            int baseZ = random.nextInt(16);
            int baseY;
            for(baseY = world.getMaxHeight()-1; source.getBlock(baseX, baseY, baseZ).getType() == Material.AIR && baseY > 0; baseY--);
            if(baseY==0)continue;
            if(!source.getBlock(baseX, baseY, baseZ).getType().isOccluding())continue;
            baseY++;

            if(treeType==0) {
                boolean moved = false;
                for(int y=0; y<3+random.nextInt(2); y++) {
                    if(y > 1 && y < 4 && !moved) {
                        if(random.nextFloat() < 0.3) {
                            moved = true;
                            switch(random.nextInt(4)) {
                                case 0: baseX++; break;
                                case 1: baseX--; break;
                                case 2: baseZ++; break;
                                case 3: baseZ--; break;
                            }
                        }
                    }

                    setBlockIfNotAir(world, source, baseX, baseY, baseZ, Material.LOG);
                    baseY++;
                }

                for(int x=-3; x<=3; x++) {
                    for (int z = -3; z <= 3; z++) {
                        for (int y = -3; y <= 3; y++) {
                            if(x*x+y*y+z*z < 5+random.nextFloat()*5) {
                                setBlockIfNotAir(world, source, baseX+x, baseY+y, baseZ+z, Material.LEAVES);
                            }
                        }
                    }
                }
            }
        }
    }

    public void setBlockIfNotAir(World world, Chunk chunk, int x, int y, int z, Material block) {
        if(world.getBlockAt(chunk.getX()*16+x, y, chunk.getZ()*16+z).getType() == Material.AIR) {
            world.getBlockAt(chunk.getX()*16+x, y, chunk.getZ()*16+z).setType(block);
        }
    }
}
