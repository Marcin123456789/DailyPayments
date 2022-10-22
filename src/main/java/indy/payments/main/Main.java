package indy.payments.main;

import indy.payments.commands.*;
import indy.payments.mysql.MySQL;
import indy.payments.events.Events;
import indy.payments.tabcompletion.tabCompletion;
import indy.payments.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Main extends JavaPlugin {

    public MySQL SQL;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);

        Commands commands = new Commands();
        commands.registerSubCommand("help", new helpCommand());
        commands.registerSubCommand("info", new infoCommand());
        commands.registerSubCommand("reload", new reloadCommand());
        commands.registerSubCommand("toggle", new toggleCommand());
        commands.registerSubCommand("executePunishments", new executePunishmentsCommand());
        commands.registerSubCommand("config", new configCommand());

        getCommand("payments").setExecutor(commands);
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
                try {
                    if(getConfig().getBoolean("Punishments.enabled")) {
                        ResultSet joinsQuery = SQL.getJoins(new Date(System.currentTimeMillis()));
                        ResultSet paymentsQuery;
                        ResultSet punishmentsQuery;
                        ArrayList<String> joins = new ArrayList<>();
                        ArrayList<String> payments = new ArrayList<>();
                        ArrayList<String> punishments = new ArrayList<>();

                        while (joinsQuery.next()) {
                            joins.add(joinsQuery.getString(1));
                        }

                        for (String players_joins : joins) {
                            paymentsQuery = SQL.getPayments(Bukkit.getOfflinePlayer(UUID.fromString(players_joins)));
                            punishmentsQuery = SQL.getPunishments(Bukkit.getOfflinePlayer(UUID.fromString(players_joins)));

                            while (paymentsQuery.next()) {
                                payments.add(paymentsQuery.getString(1));
                            }
                            while (punishmentsQuery.next()) {
                                punishments.add(punishmentsQuery.getString(1));
                            }

                            if (!payments.contains(Utils.formatDate(new Date(System.currentTimeMillis()))) && !punishments.contains(players_joins)) {
                                SQL.registerPunishment(Bukkit.getOfflinePlayer(UUID.fromString(players_joins)));
                            }
                        }
                        if(getConfig().getBoolean("Punishments.auto-punishment")) {
                            int amount = SQL.executePunishments();
                            if(getConfig().getBoolean("Punishments.announce-to-chat")) {
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    if(player.hasPermission("payments.announce")) {
                                        player.sendMessage(Utils.getMessage("Punishments.auto-punishment-message")
                                                .replace("%amount%", String.valueOf(amount)));
                                    }
                                }
                            }
                            if(getConfig().getBoolean("Punishments.announce-to-console")) {
                                Bukkit.getConsoleSender().sendMessage(Utils.getMessage("Punishments.auto-punishment-message")
                                        .replace("%amount%", String.valueOf(amount)));
                            }
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, getConfig().getInt("Punishments.time.hour"), getConfig().getInt("Punishments.time.min"));
    }

    @Override
    public void onDisable() {
        SQL.disconnect();
    }

}
