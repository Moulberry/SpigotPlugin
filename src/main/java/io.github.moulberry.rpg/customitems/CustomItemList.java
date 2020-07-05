package io.github.moulberry.rpg.customitems;

import net.minecraft.server.v1_8_R3.EnumChatFormat;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum CustomItemList {

    EARTH_CHARM(new EarthCharm(), Material.CLAY_BALL,
            EnumChatFormat.RESET.toString() + EnumChatFormat.DARK_GRAY + "Earth Charm",
            "earth_charm"),
    SPAWN_THING(new SpawnThing(), Material.DIAMOND,
            EnumChatFormat.RESET.toString() + EnumChatFormat.GOLD + "Spawn Tester",
            "spawn_thing");

    private Material material;
    private String customName;
    private String customItemId;

    CustomItemList(CustomItem customItem, Material material, String customName, String customItemId) {
        CustomItemManager.getInstance().register(customItemId, customItem);

        this.material = material;
        this.customName = customName;
        this.customItemId = customItemId;
    }

    public ItemStack createItemstack(int count) {
        ItemStack stack = new ItemStack(material, 1);

        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound tag = nmsStack.getTag();
        if(tag == null) {
            tag = new NBTTagCompound();
        }
        tag.set("CustomItemId", new NBTTagString(customItemId));
        nmsStack.setTag(tag);

        stack = CraftItemStack.asBukkitCopy(nmsStack);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(customName);
        stack.setItemMeta(meta);

        return stack;
    }

    public static CustomItemList getById(String id) {
        for(CustomItemList ci : values()) {
            if(ci.customItemId.equalsIgnoreCase(id)) {
                return ci;
            }
        }
        return null;
    }

}
