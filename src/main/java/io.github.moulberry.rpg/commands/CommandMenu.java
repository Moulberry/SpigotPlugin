package io.github.moulberry.rpg.commands;

import io.github.moulberry.rpg.customitems.CustomItemList;
import io.github.moulberry.rpg.guis.GuiManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandMenu implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player)) {
            return false;
        }
        if(args.length != 0) {
            return false;
        }

        Player player = (Player) commandSender;
        GuiManager.getInstance().openGui(player, GuiManager.MAIN_MENU);

        return true;
    }
}
