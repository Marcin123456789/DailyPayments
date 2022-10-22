package indy.payments.commands;

import indy.payments.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class helpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String arg, String[] args) {
        for(Object element : Utils.getConfig().getList("Messages.help")) {
            sender.sendMessage(Utils.colorFormat(element.toString()));
        }
        return false;
    }
}
