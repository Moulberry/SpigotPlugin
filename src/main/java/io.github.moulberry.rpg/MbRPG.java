package io.github.moulberry.rpg;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.github.moulberry.rpg.commands.*;
import org.bukkit.plugin.java.JavaPlugin;

public class MbRPG extends JavaPlugin {

    private static MbRPG INSTANCE;

    public static MbRPG getInstance() {
        return INSTANCE;
    }

    public ProtocolManager protocolManager;

    public void onEnable() {
        INSTANCE = this;

        protocolManager = ProtocolLibrary.getProtocolManager();
        EventListener listener = new EventListener();
        listener.addProtocolLibListeners(this, protocolManager);
        getServer().getPluginManager().registerEvents(listener, this);

        getCommand("gotodynamicworld").setExecutor(new CommandGotoDynamicWorld());
        getCommand("gotoinstanceworld").setExecutor(new CommandGotoInstanceWorld());
        getCommand("createemptyworld").setExecutor(new CommandCreateEmptyWorld());
        getCommand("givecustomitem").setExecutor(new CommandGiveCustomItem());
        getCommand("menu").setExecutor(new CommandMenu());

        getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            listener.onTick();
        }, 1, 1);
    }

    public void schedule(Runnable runnable, int ticks) {
        getServer().getScheduler().scheduleSyncDelayedTask(this, runnable, ticks);
    }

}
