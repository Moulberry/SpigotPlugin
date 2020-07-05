package io.github.moulberry.rpg.guis;

import io.github.moulberry.rpg.ItemstackBuilder;
import io.github.moulberry.rpg.Util;
import io.github.moulberry.rpg.world.WorldManager;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class GuiMainMenu extends Gui {

    ItemStack[] getGuiInventoryItems(Player player) {
        ItemStack filler = new ItemStack(Material.STAINED_GLASS_PANE, 1);
        MaterialData data = filler.getData();
        data.setData((byte)15);
        filler.setData(data);
        ItemStack[] items = fillInvItems(filler);

        items[22] = ItemstackBuilder.create(Material.GRASS, 1)
                .withName("&r&dWarp to: &aPrivate Island")
                .withLore("&eClick to warp.", "")
                .getItemstack();

        items[23] = ItemstackBuilder.create(Material.GLOWSTONE, 1)
                .withName("&r&dWarp to: &aAether dim (temporary)")
                .withLore("&eClick to warp.", "")
                .getItemstack();

        return items;
    }

    void slotClicked(Player player, int slot, ClickType click, int hotbarButton) {
        if(slot == 22) {
            WorldManager.DynamicWorldRef ref = WorldManager.getInstance().getFirstDynamicWorld("player_island", player.getUniqueId());
            World instance = WorldManager.getInstance().createInstanceWorldFromDynamic(ref);

            player.teleport(Util.addHalfToLoc(instance.getSpawnLocation()));
        } else if(slot == 23) {
            WorldManager.DynamicWorldRef ref = WorldManager.getInstance().getFirstDynamicWorld("aether", player.getUniqueId());
            World instance = WorldManager.getInstance().createInstanceWorldFromDynamic(ref);

            player.teleport(Util.addHalfToLoc(instance.getSpawnLocation()));
        }
    }

    int getRows() {
        return 4;
    }
}
