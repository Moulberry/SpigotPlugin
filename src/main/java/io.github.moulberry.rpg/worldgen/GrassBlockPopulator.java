package io.github.moulberry.rpg.worldgen;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class GrassBlockPopulator extends BlockPopulator {

    public void populate(World world, Random random, Chunk source) {
        int count = 16+random.nextInt(16);

        for(int i=0; i<count; i++) {
            int baseX = random.nextInt(16);
            int baseZ = random.nextInt(16);
            int baseY;
            for (baseY = world.getMaxHeight() - 1; source.getBlock(baseX, baseY, baseZ).getType() == Material.AIR && baseY > 0; baseY--) ;
            if (baseY == 0) continue;
            if (!source.getBlock(baseX, baseY, baseZ).getType().isOccluding()) continue;
            baseY++;

            Material mat;
            int damage = 0;
            float rand = random.nextFloat();
            if(rand < 0.1) {
                mat = Material.YELLOW_FLOWER;
            } else if(rand < 0.3) {
                mat = Material.RED_ROSE;
            } else if(rand < 0.6) {
                mat = Material.RED_ROSE;
                damage = random.nextInt(9);
            } else {
                mat = Material.LONG_GRASS;
                damage = 1+random.nextInt(2);
            }
            source.getBlock(baseX, baseY, baseZ).setType(mat);
            source.getBlock(baseX, baseY, baseZ).setData((byte)damage);
        }
    }
}
