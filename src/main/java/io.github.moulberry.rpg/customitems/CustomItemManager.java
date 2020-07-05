package io.github.moulberry.rpg.customitems;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CustomItemManager {

    private static final CustomItemManager INSTANCE = new CustomItemManager();

    private static Map<String, CustomItem> customItemMap = new HashMap<>();

    public static CustomItemManager getInstance() {
        return INSTANCE;
    }

    public void register(String itemId, CustomItem item) {
        customItemMap.put(itemId, item);
    }

    private CustomItem getHandlerForItem(ItemStack stack) {
        for(CustomItemList item : CustomItemList.values()) {
            item.hashCode(); //Ensure all items are loaded.
        }

        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
        if(nmsStack == null) {
            return null;
        }

        NBTTagCompound tag = nmsStack.getTag();
        if(tag == null) {
            return null;
        }

        String itemId = tag.getString("CustomItemId");
        if(itemId == null || itemId.isEmpty()) {
            return null;
        } else {
            return customItemMap.get(itemId);
        }
    }

    public void onTick(ItemStack stack, Player player, boolean held) {
        CustomItem ci = getHandlerForItem(stack);
        if(ci != null) {
            ci.onTick(stack, player, held);
        }
    }

    public void onUse(ItemStack stack, Player player, Action action, Block clickedBlock, BlockFace clickedFace) {
        CustomItem ci = getHandlerForItem(stack);
        if(ci != null) {
            ci.onUse(stack, player, action, clickedBlock, clickedFace);
        }
    }

    public void onWear(ItemStack stack, Player player) {
        CustomItem ci = getHandlerForItem(stack);
        if(ci != null) {
            ci.onWear(stack, player);
        }
    }



}
