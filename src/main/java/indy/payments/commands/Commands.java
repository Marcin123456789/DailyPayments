package indy.payments.commands;

import indy.payments.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class Commands implements CommandExecutor {

    private Map<String, CommandExecutor> commands = new HashMap<>();

    public void registerSubCommand(String cmd, CommandExecutor command) {
        commands.put(cmd, command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Utils.colorFormat(Utils.getConfig().getString("Messages.missing-argument")));
        } else if(!commands.containsKey(args[0].toLowerCase())) {
            sender.sendMessage(Utils.colorFormat(Utils.getConfig().getString("Messages.wrong-argument")));
        } else {
            arg = args[0];
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, newArgs.length);
            commands.get(args[0]).onCommand(sender, cmd, arg, newArgs);
        }
        return false;
    }
}