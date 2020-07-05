package io.github.moulberry.rpg.commands;

import io.github.moulberry.rpg.world.WorldManager;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCreateEmptyWorld implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length != 1) {
            return false;
        }

        if(sender instanceof Player) {
            Player player = (Player) sender;

            World instance = WorldManager.getInstance().createEmptyWorld(args[0]);

            player.teleport(instance.getSpawnLocation());

            return true;
        }

        return false;
    }
}
