package io.github.moulberry.rpg.guis;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import io.github.moulberry.rpg.MbRPG;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryCustom;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;

public abstract class Gui {

    Gui() { }

    abstract int getRows();
    abstract ItemStack[] getGuiInventoryItems(Player player);
    abstract void slotClicked(Player player, int slot, ClickType click, int hotbarButton);

    ItemStack[] fillInvItems(ItemStack item) {
        ItemStack[] items = new ItemStack[9*getRows()];
        for(int i=0; i<items.length; i++) {
            items[i] = item.clone();
        }
        return items;
    }

    ItemStack[] getGuiAndPlayerInventoryItems(Player player) {
        ItemStack[] guiItems = getGuiInventoryItems(player);
        ItemStack[] playerItems = player.getInventory().getContents();
        ItemStack[] items = new ItemStack[getRows()*9+playerItems.length];
        for(int i=0; i<getRows()*9; i++) {
            if(i < guiItems.length) {
                items[i] = guiItems[i];
            } else {
                items[i] = null;
            }
        }
        for(int i=0; i<playerItems.length-9; i++) {
            items[i+getRows()*9] = playerItems[i+9];
        }
        for(int i=0; i<9; i++) {
            items[i+getRows()*9+playerItems.length-9] = playerItems[i];
        }
        return items;
    }

    int openGui(Player player) {
        int id = displayEmptyInventory(player);
        updateInventoryContents(player, id);

        return id;
    }

    private int displayEmptyInventory(Player player) {
        CraftInventoryCustom inventory = new CraftInventoryCustom(null, getRows()*9, "Test");
        player.openInventory(inventory);
        return ((CraftPlayer)player).getHandle().activeContainer.windowId;
    }

    void updateInventoryContents(Player player, int windowId) {
        try {
            PacketContainer packet = MbRPG.getInstance().protocolManager.createPacket(PacketType.Play.Server.WINDOW_ITEMS);

            packet.getIntegers().write(0, windowId);
            packet.getItemArrayModifier().write(0, getGuiAndPlayerInventoryItems(player));

            MbRPG.getInstance().protocolManager.sendServerPacket(player, packet);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
