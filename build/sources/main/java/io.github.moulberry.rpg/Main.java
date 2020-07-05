package io.github.moulberry.rpg;

import io.github.moulberry.rpg.commands.CommandCreateEmptyWorld;
import io.github.moulberry.rpg.commands.CommandGotoDynamicWorld;
import io.github.moulberry.rpg.commands.CommandGotoInstanceWorld;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public void onEnable() {
        getServer().getPluginManager().registerEvents(new EventListener(), this);

        getCommand("gotodynamicworld").setExecutor(new CommandGotoDynamicWorld());
        getCommand("gotoinstanceworld").setExecutor(new CommandGotoInstanceWorld());
        getCommand("createemptyworld").setExecutor(new CommandCreateEmptyWorld());
    }

    public void onDisable() {
        for(World world : Bukkit.getWorlds()) {
            Bukkit.unloadWorld(world, false);
        }
    }

}
