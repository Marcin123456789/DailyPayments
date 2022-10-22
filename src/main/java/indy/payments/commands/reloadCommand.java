package indy.payments.commands;

import indy.payments.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class reloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String arg, String[] args) {
        if(sender.hasPermission("payments.reload")) {
            Utils.plugin().reloadConfig();
            sender.sendMessage(Utils.colorFormat(Utils.getConfig().getString("Messages.reload")));
        }
        return false;
    }
}
