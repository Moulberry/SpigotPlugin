package io.github.moulberry.rpg.guis;

import io.github.moulberry.rpg.customitems.CustomItem;
import io.github.moulberry.rpg.customitems.CustomItemManager;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GuiManager {

    private static final GuiManager INSTANCE = new GuiManager();

    public static GuiManager getInstance() {
        return INSTANCE;
    }

    public static final Gui MAIN_MENU = new GuiMainMenu();

    private class GuiAndId {
        public Gui gui;
        public int id;

        public GuiAndId(Gui gui, int id) {
            this.gui = gui;
            this.id = id;
        }
    }

    private Map<Player, GuiAndId> activeGuiMap = new HashMap<>();

    public void sendGuiClick(Player player, int slot, ClickType click, int hotbarButton) {
        GuiAndId guiAndId = activeGuiMap.get(player);

        if(guiAndId != null) {
            guiAndId.gui.slotClicked(player, slot, click, hotbarButton);
        }
    }

    public int getRows(Player player) {
        GuiAndId guiAndId = activeGuiMap.get(player);

        if(guiAndId != null) {
            return guiAndId.gui.getRows();
        } else {
            return 0;
        }
    }

    public ItemStack[] getGuiItems(Player player) {
        GuiAndId guiAndId = activeGuiMap.get(player);

        if(guiAndId == null) {
            return new ItemStack[0];
        } else {
            return guiAndId.gui.getGuiAndPlayerInventoryItems(player);
        }
    }

    public void updateGuiItemContents(Player player) {
        if(isGuiOpen(player)) {
            GuiAndId guiAndId = activeGuiMap.get(player);
            if(guiAndId != null) {
                guiAndId.gui.updateInventoryContents(player, guiAndId.id);
            }
        }
    }

    public void openGui(Player player, Gui gui) {
        int id = gui.openGui(player);
        activeGuiMap.put(player, new GuiAndId(gui, id));
    }

    public boolean isGuiOpen(Player player) {
        int currId = ((CraftPlayer)player).getHandle().activeContainer.windowId;

        GuiAndId guiAndId = activeGuiMap.get(player);

        if(guiAndId == null) {
            return false;
        }

        if(guiAndId.id != currId) {
            activeGuiMap.remove(player);
            return false;
        }
        return true;
    }



}
