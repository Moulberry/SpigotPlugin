package io.github.moulberry.rpg;

import net.minecraft.server.v1_8_R3.EnumChatFormat;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

public class ItemstackBuilder {

    private ItemStack stack;

    private ItemstackBuilder(Material mat, int count) {
        stack = new ItemStack(mat, count);
    }

    public static ItemstackBuilder create(Material mat, int count) {
        return new ItemstackBuilder(mat, count);
    }

    public ItemstackBuilder withName(String name) {
        name = name.replaceAll("&", EnumChatFormat.AQUA.toString().substring(0, 1));
        name = name.replaceAll("\\{amp\\}", "&");

        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        stack.setItemMeta(meta);

        return this;
    }

    public ItemstackBuilder withData(byte data) {
        MaterialData mdata = stack.getData();
        mdata.setData(data);
        stack.setData(mdata);

        return this;
    }

    public ItemstackBuilder withLore(String... lore) {
        List<String> loreReplaced = new ArrayList<>();
        for(String str : lore) {
            str = str.replaceAll("&", EnumChatFormat.AQUA.toString().substring(0, 1));
            str = str.replaceAll("\\{amp\\}", "&");
            loreReplaced.add(str);
        }
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(loreReplaced);
        stack.setItemMeta(meta);

        return this;
    }

    public ItemStack getItemstack() {
        return stack;
    }

}
