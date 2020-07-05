package io.github.moulberry.rpg.commands;

import io.github.moulberry.rpg.customitems.CustomItemList;
import io.github.moulberry.rpg.guis.Gui;
import io.github.moulberry.rpg.guis.GuiManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGiveCustomItem implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player)) {
            return false;
        }
        if(args.length != 1) {
            return false;
        }

        Player player = (Player) commandSender;

        if(args[0].equalsIgnoreCase("lazy")) {
            GuiManager.getInstance().openGui(player, GuiManager.MAIN_MENU);
        } else {
            player.getInventory().addItem(CustomItemList.getById(args[0]).createItemstack(1));
        }

        return true;
    }
}
