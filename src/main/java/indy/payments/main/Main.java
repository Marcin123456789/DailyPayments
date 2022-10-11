package indy.payments.main;

import indy.payments.commands.Commands;
import indy.payments.mysql.MySQL;
import indy.payments.events.Events;
import indy.payments.tabcompletion.tabCompletion;
import indy.payments.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class Main extends JavaPlugin {

    public MySQL SQL;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        getCommand("payments").setExecutor(new Commands());
        getCommand("payments").setTabCompleter(new tabCompletion());
        saveConfig();
        reloadConfig();

        this.SQL = new MySQL();
        try {
            SQL.connect();
        } catch(ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            Bukkit.getLogger().info(Utils.colorFormat(getConfig().getString("Messages.database-not-connected")));
        }

        if(SQL.isConnected()) {
            Bukkit.getLogger().info(Utils.colorFormat(getConfig().getString("Messages.database-connected")));
            SQL.createTable();
        }

        getServer().getPluginManager().registerEvents(new Events(), this);

        Events.scheduledEvent(this, new Runnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage("Scheduled event!");
                }
            }
        }, getConfig().getInt("Punishments.time.hour"), getConfig().getInt("Punishments.time.min"));
    }

    @Override
    public void onDisable() {
        SQL.disconnect();
    }

}
