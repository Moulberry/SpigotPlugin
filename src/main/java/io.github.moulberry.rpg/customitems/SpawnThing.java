package io.github.moulberry.rpg.customitems;

import io.github.moulberry.rpg.FakeEntityManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class SpawnThing extends CustomItem {

    public void onUse(ItemStack stack, Player player, Action action, Block clickedBlock, BlockFace clickedFace){
        if(clickedBlock != null) {
            Location loc = clickedBlock.getLocation().clone();
            loc.add(clickedFace.getModX(), clickedFace.getModY(), clickedFace.getModZ());

            FakeEntityManager.getInstance().createFakeEntity(EntityType.SKELETON, loc, true);
        }
    }

}
