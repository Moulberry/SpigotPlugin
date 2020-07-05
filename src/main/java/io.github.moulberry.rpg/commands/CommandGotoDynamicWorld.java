package io.github.moulberry.rpg.commands;

import io.github.moulberry.rpg.world.WorldManager;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class CommandGotoDynamicWorld implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length != 1) {
            return false;
        }

        try {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                UUID playerUUID = player.getUniqueId();

                String worldName = args[0];

                List<WorldManager.DynamicWorldRef> refs = WorldManager.getInstance().getDynamicWorlds(worldName, playerUUID);
                WorldManager.DynamicWorldRef ref;

                if(refs.isEmpty()) {
                    ref = new WorldManager.DynamicWorldRef(worldName, playerUUID, System.currentTimeMillis());
                    WorldManager.getInstance().createDynamicWorldFromTemplate(ref);
                } else {
                    ref = refs.get(0);
                }

                World instance = WorldManager.getInstance().createInstanceWorldFromDynamic(ref);

                player.teleport(instance.getSpawnLocation());

                return true;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
