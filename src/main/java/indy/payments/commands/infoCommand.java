package indy.payments.commands;

import indy.payments.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class infoCommand implements CommandExecutor {

    private final String author = Utils.plugin().getDescription().getAuthors().get(0);
    private final String version = Utils.plugin().getDescription().getVersion();
    private final String mc_version = Utils.plugin().getServer().getVersion();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String arg, String[] args) {
        for(Object element : Utils.getConfig().getList("Messages.info")) {
            String message = element.toString()
                    .replace("%author%", author)
                    .replace("%version%", version)
                    .replace("%mc-version%", mc_version);
            sender.sendMessage(Utils.colorFormat(message));
        }
        return false;
    }
}
