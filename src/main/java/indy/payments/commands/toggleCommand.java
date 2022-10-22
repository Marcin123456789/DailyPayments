package indy.payments.commands;

import indy.payments.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class toggleCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
        if(sender.hasPermission("payments.toggle")) {
            if (Utils.getConfig().getBoolean("Payment.enabled")) {
                Utils.getConfig().set("Payment.enabled", false);
                sender.sendMessage(Utils.colorFormat(Utils.getConfig().getString("Messages.payments-enable")));
            } else {
                Utils.getConfig().set("Payment.enabled", true);
                sender.sendMessage(Utils.colorFormat(Utils.getConfig().getString("Messages.payments-disable")));
            }
            Utils.plugin().saveConfig();
            Utils.plugin().reloadConfig();
        }
        return false;
    }
}
