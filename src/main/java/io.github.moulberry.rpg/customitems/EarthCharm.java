package io.github.moulberry.rpg.customitems;

import io.github.moulberry.rpg.FakeBlockManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class EarthCharm extends CustomItem {

    private final int MAX_RADIUS = 5;

    public void onTick(ItemStack stack, Player player, boolean held) {
        if(held) {
            for(int x=-MAX_RADIUS; x<=MAX_RADIUS; x++) {
                for(int y=-MAX_RADIUS; y<=MAX_RADIUS; y++) {
                    for(int z=-MAX_RADIUS; z<=MAX_RADIUS; z++) {
                        int distanceSq = x*x + y*y + z*z;
                        if(distanceSq <= MAX_RADIUS*MAX_RADIUS) {
                            Location loc = player.getLocation();
                            Location offsetLoc = new Location(loc.getWorld(), loc.getBlockX()+x, loc.getBlockY()+y,
                                    loc.getBlockZ()+z);

                            if(player.getWorld().getBlockAt(offsetLoc).getType() == Material.STONE) {
                                FakeBlockManager.getInstance().putFakeBlock(player, offsetLoc, Material.STAINED_GLASS, (byte)8, 100);
                            }
                        }
                    }
                }
            }
        }
    }
}
