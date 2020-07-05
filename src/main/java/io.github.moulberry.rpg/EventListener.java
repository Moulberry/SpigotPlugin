package io.github.moulberry.rpg;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import io.github.moulberry.rpg.customitems.CustomItemManager;
import io.github.moulberry.rpg.guis.GuiManager;
import io.github.moulberry.rpg.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class EventListener implements Listener {

    public void addProtocolLibListeners(Plugin plugin, ProtocolManager manager) {
        //listeners
        manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGH,
                PacketType.Play.Server.BLOCK_CHANGE) {
            public void onPacketSending(PacketEvent event) {
                if(event.getPacketType() == PacketType.Play.Server.BLOCK_CHANGE) {
                    PacketContainer packet = event.getPacket();
                    BlockPosition pos = packet.getBlockPositionModifier().getValues().get(0);

                    Map<Location, FakeBlockManager.FakeBlockData> fakeBlockMap = FakeBlockManager.getInstance().getMapForPlayer(event.getPlayer());


                    for(Location loc : fakeBlockMap.keySet()) {
                        if(loc.getBlockX() == pos.getX() &&
                                loc.getBlockY() == pos.getY() &&
                                loc.getBlockZ() == pos.getZ()) {
                            packet.getBlockData().write(0, WrappedBlockData.createData(fakeBlockMap.get(loc).mat, fakeBlockMap.get(loc).data));
                            break;
                        }
                    }
                }
            }
        });

        manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGH,
                PacketType.Play.Server.WINDOW_ITEMS) {
            public void onPacketSending(PacketEvent event) {
                if(event.getPacketType() == PacketType.Play.Server.WINDOW_ITEMS) {
                    PacketContainer packet = event.getPacket();

                    if(GuiManager.getInstance().isGuiOpen(event.getPlayer())) {
                        packet.getItemArrayModifier().write(0, GuiManager.getInstance().getGuiItems(event.getPlayer()));
                    }
                }
            }
        });

        manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGH,
                PacketType.Play.Client.USE_ENTITY) {
            public void onPacketReceiving(PacketEvent event) {
                if(event.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
                    PacketContainer packet = event.getPacket();

                    System.out.println("interact->"+packet.getIntegers().read(0));
                }
            }
        });
    }

    public void onTick() {
        WorldManager.getInstance().tickUnload();
        FakeBlockManager.getInstance().onTick();

        for(Player player : Bukkit.getOnlinePlayers()) {
            ItemStack held = player.getInventory().getItemInHand();
            if(held != null) {
                CustomItemManager.getInstance().onTick(held, player, true);
            }

            for(ItemStack stack : player.getInventory().getContents()) {
                if(stack != held && stack != null) {
                    CustomItemManager.getInstance().onTick(stack, player, false);
                }
            }
            for(ItemStack stack : player.getInventory().getArmorContents()) {
                if(stack != null) {
                    CustomItemManager.getInstance().onWear(stack, player);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerClickInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if(GuiManager.getInstance().isGuiOpen(player) && event.getClickedInventory() != null
                && event.getClickedInventory().getHolder() == null) {
            event.setCancelled(true);
            player.setItemOnCursor(player.getItemOnCursor());
            GuiManager.getInstance().updateGuiItemContents(player);
            GuiManager.getInstance().sendGuiClick(player, event.getSlot(), event.getClick(), event.getHotbarButton());

            if(player.getItemOnCursor() != null || player.getItemOnCursor().getType() == Material.AIR) {
                MbRPG.getInstance().schedule(() -> {
                    GuiManager.getInstance().updateGuiItemContents(player);
                }, 1);
            }
        }
    }

    @EventHandler
    public void onPlayerClickInventory(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        if(GuiManager.getInstance().isGuiOpen(player)) {
            event.setCancelled(true);
            GuiManager.getInstance().updateGuiItemContents(player);
        }
    }

    @EventHandler
    public void onPlayerUseItem(PlayerInteractEvent event) {
        ItemStack stack = event.getItem();

        if(stack != null) {
            CustomItemManager.getInstance().onUse(event.getItem(), event.getPlayer(), event.getAction(), event.getClickedBlock(), event.getBlockFace());
        }
    }

    @EventHandler
    public void onPlayerSwitchWorld(PlayerChangedWorldEvent event) {
        FakeBlockManager.getInstance().clearAllUnsafe(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        FakeBlockManager.getInstance().clearAllUnsafe(event.getPlayer());
    }

    @EventHandler
    public void onPlayerKicked(PlayerKickEvent event) {
        FakeBlockManager.getInstance().clearAllUnsafe(event.getPlayer());
    }


}
