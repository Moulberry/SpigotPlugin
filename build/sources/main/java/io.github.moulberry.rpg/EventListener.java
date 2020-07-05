package io.github.moulberry.rpg;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.io.File;

public class EventListener implements Listener {



    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        try {
            //delete instance
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        try {
            if(event.getFrom().getPlayers().isEmpty()) {
                Bukkit.unloadWorld(event.getFrom(), true);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


}
