package io.github.moulberry.rpg.worldgen;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class PoleBlockPopulator extends BlockPopulator {

    public void populate(World world, Random random, Chunk source) {
        if(random.nextFloat() < 0.05) {
            int baseX = random.nextInt(16);
            int baseZ = random.nextInt(16);
            int baseY;
            for (baseY = world.getMaxHeight() - 1; source.getBlock(baseX, baseY, baseZ).getType() == Material.AIR && baseY > 0; baseY--);
            if (baseY == 0) return;
            if (!source.getBlock(baseX, baseY, baseZ).getType().isOccluding()) return;
            baseY++;

            for(int y=0; y<5; y++) {
                source.getBlock(baseX, baseY+y, baseZ).setType(random.nextBoolean() ? Material.COBBLESTONE : Material.MOSSY_COBBLESTONE);
            }
        }
    }
}
