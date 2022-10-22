package indy.payments.commands;

import indy.payments.mysql.MySQL;
import indy.payments.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;

public class executePunishmentsCommand implements CommandExecutor {

    public MySQL SQL;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String arg, String[] args) {
        this.SQL = new MySQL();
        if(sender.hasPermission("payments.execute_punishments")) {
            try {
                SQL.connect();

                int amount = SQL.executePunishments();
                sender.sendMessage(Utils.getMessage("Punishments.manual-punishment-message").replace("%amount%", String.valueOf(amount)));
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
