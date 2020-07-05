package io.github.moulberry.rpg.customitems;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public abstract class CustomItem {

    public void onTick(ItemStack stack, Player player, boolean held){}
    public void onUse(ItemStack stack, Player player, Action action, Block clickedBlock, BlockFace clickedFace){}
    public void onWear(ItemStack stack, Player player){}

}
