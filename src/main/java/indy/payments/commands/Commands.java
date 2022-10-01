package indy.payments.commands;

import indy.payments.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Commands implements CommandExecutor {

    private Player p;
    private final String author = plugin().getDescription().getAuthors().get(0);
    private final String version = plugin().getDescription().getVersion();
    private final String mc_version = plugin().getServer().getVersion();

    public Plugin plugin() {
        return Bukkit.getServer().getPluginManager().getPlugin("DailyPayments");
    }

    public FileConfiguration getConfig() {
        return Bukkit.getServer().getPluginManager().getPlugin("DailyPayments").getConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
        if(sender instanceof Player) {
            p = (Player) sender;
        }

        if(args.length == 0) {
            sender.sendMessage(Utils.colorFormat(getConfig().getString("Messages.missing-argument")));
        }
        else {
            switch(args[0]) {
                case "help":
                    for(Object element : getConfig().getList("Messages.help")) {
                        sender.sendMessage(Utils.colorFormat(element.toString()));
                    }
                    break;
                case "info":
                    for(Object element : getConfig().getList("Messages.info")) {
                        String message = element.toString()
                                .replace("%author%", author)
                                .replace("%version%", version)
                                .replace("%mc-version%", mc_version);
                        sender.sendMessage(Utils.colorFormat(message));
                    }
                    break;
                case "reload":
                    plugin().reloadConfig();
                    sender.sendMessage(Utils.colorFormat(getConfig().getString("Messages.reload")));
                    break;
                case "enable":
                    if(!getConfig().getBoolean("Payment.enabled")) {
                        getConfig().set("Payment.enabled", true);
                        plugin().saveConfig();
                        plugin().reloadConfig();
                        sender.sendMessage(Utils.colorFormat(getConfig().getString("Messages.payments-enable")));
                    }
                    else {
                        sender.sendMessage(Utils.colorFormat(getConfig().getString("Messages.payments-enabled")));
                    }
                    break;
                case "disable":
                    if(getConfig().getBoolean("Payment.enabled")) {
                        getConfig().set("Payment.enabled", false);
                        plugin().saveConfig();
                        plugin().reloadConfig();
                        sender.sendMessage(Utils.colorFormat(getConfig().getString("Messages.payments-disable")));
                    }
                    else {
                        sender.sendMessage(Utils.colorFormat(getConfig().getString("Messages.payments-disabled")));
                    }
                    break;
                case "config":
                    if(args.length == 1) {
                        sender.sendMessage(Utils.colorFormat(getConfig().getString("Messages.config-missing-arguments")));
                        break;
                    }
                    else {
                        switch(args[1]) {
                            case "set":
                                if(args.length == 2) {
                                    sender.sendMessage(Utils.colorFormat(getConfig().getString("Messages.config-set-missing-arguments")));
                                }
                                else {
                                    switch (args[2]) {
                                        case "block":
                                            if (args.length == 3) {
                                                if (sender instanceof Player) {
                                                    getConfig().set("Payment.block-type", p.getInventory().getItemInMainHand().getType().toString());
                                                    plugin().saveConfig();
                                                    plugin().reloadConfig();

                                                    sender.sendMessage(Utils.colorFormat(getConfig().getString("Messages.block-change")
                                                            .replace("%block%", p.getInventory().getItemInMainHand().getType().toString())));
                                                } else {
                                                    sender.sendMessage(Utils.colorFormat(getConfig().getString("Messages.non-player-executor")));
                                                }
                                            } else {
                                                getConfig().set("Payment.block-type", args[3]);
                                                plugin().saveConfig();
                                                plugin().reloadConfig();

                                                sender.sendMessage(Utils.colorFormat(getConfig().getString("Messages.block-change")
                                                        .replace("%block%", args[3])));
                                            }
                                            break;
                                        case "world":
                                            if (args.length == 3) {
                                                if (sender instanceof Player) {
                                                    getConfig().set("Payment.area.world", p.getLocation().getWorld().getName());
                                                    plugin().saveConfig();
                                                    plugin().reloadConfig();

                                                    sender.sendMessage(Utils.colorFormat(getConfig().getString("Messages.world-change")
                                                            .replace("%world%", p.getLocation().getWorld().getName())));
                                                } else {
                                                    sender.sendMessage(Utils.colorFormat(getConfig().getString("Messages.non-player-executor")));
                                                }
                                            } else {
                                                getConfig().set("Payment.area.world", args[3]);
                                                plugin().saveConfig();
                                                plugin().reloadConfig();

                                                sender.sendMessage(Utils.colorFormat(getConfig().getString("Messages.world-change")
                                                        .replace("%world%", args[3])));
                                            }
                                            break;
                                        case "firstPosition":
                                            if (args.length == 3) {
                                                if (sender instanceof Player) {
                                                    getConfig().set("Payment.area.pos1.x", (int) p.getLocation().getX());
                                                    getConfig().set("Payment.area.pos1.y", (int) p.getLocation().getY());
                                                    getConfig().set("Payment.area.pos1.z", (int) p.getLocation().getZ());
                                                    plugin().saveConfig();
                                                    plugin().reloadConfig();

                                                    sender.sendMessage(Utils.colorFormat(getConfig().getString("Messages.first-position-change")
                                                            .replace("%x%", String.valueOf((int) p.getLocation().getX()))
                                                            .replace("%y%", String.valueOf((int) p.getLocation().getY()))
                                                            .replace("%z%", String.valueOf((int) p.getLocation().getZ()))));
                                                } else {
                                                    sender.sendMessage(Utils.colorFormat(getConfig().getString("Messages.non-player-executor")));
                                                }
                                            } else {
                                                if (args[3].matches("[0-9]+") && args[4].matches("[0-9]+") && args[5].matches("[0-9]+")) {
                                                    getConfig().set("Payment.area.pos1.x", Integer.valueOf(args[3]));
                                                    getConfig().set("Payment.area.pos1.y", Integer.valueOf(args[4]));
                                                    getConfig().set("Payment.area.pos1.z", Integer.valueOf(args[5]));
                                                    plugin().saveConfig();
                                                    plugin().reloadConfig();

                                                    sender.sendMessage(Utils.colorFormat(getConfig().getString("Messages.first-position-change")
                                                            .replace("%x%", args[3])
                                                            .replace("%y%", args[4])
                                                            .replace("%z%", args[5])));
                                                }
                                            }
                                            break;
                                        case "secondPosition":
                                            if (args.length == 3) {
                                                if (sender instanceof Player) {
                                                    getConfig().set("Payment.area.pos2.x", (int) p.getLocation().getX());
                                                    getConfig().set("Payment.area.pos2.y", (int) p.getLocation().getY());
                                                    getConfig().set("Payment.area.pos2.z", (int) p.getLocation().getZ());
                                                    plugin().saveConfig();
                                                    plugin().reloadConfig();

                                                    sender.sendMessage(Utils.colorFormat(getConfig().getString("Messages.second-position-change")
                                                            .replace("%x%", String.valueOf((int) p.getLocation().getX()))
                                                            .replace("%y%", String.valueOf((int) p.getLocation().getY()))
                                                            .replace("%z%", String.valueOf((int) p.getLocation().getZ()))));
                                                } else {
                                                    sender.sendMessage(Utils.colorFormat(getConfig().getString("Messages.non-player-executor")));
                                                }
                                            } else {
                                                if (args[3].matches("[0-9]+") && args[4].matches("[0-9]+") && args[5].matches("[0-9]+")) {
                                                    getConfig().set("Payment.area.pos2.x", Integer.valueOf(args[3]));
                                                    getConfig().set("Payment.area.pos2.y", Integer.valueOf(args[4]));
                                                    getConfig().set("Payment.area.pos2.z", Integer.valueOf(args[5]));
                                                    plugin().saveConfig();
                                                    plugin().reloadConfig();

                                                    sender.sendMessage(Utils.colorFormat(getConfig().getString("Messages.second-position-change")
                                                            .replace("%x%", args[3])
                                                            .replace("%y%", args[4])
                                                            .replace("%z%", args[5])));
                                                }
                                            }
                                            break;
                                        default:
                                            sender.sendMessage(Utils.colorFormat(getConfig().getString("Messages.config-set-missing-arguments")));
                                    }
                                }
                                break;
                            case "get":
                                for (Object element : getConfig().getList("Messages.config-get")) {
                                    String message = element.toString()
                                            .replace("%block%", getConfig().getString("Payment.block-type"))
                                            .replace("%world%", getConfig().getString("Payment.area.world"))
                                            .replace("%x1%", String.valueOf(getConfig().getInt("Payment.area.pos1.x")))
                                            .replace("%y1%", String.valueOf(getConfig().getInt("Payment.area.pos1.y")))
                                            .replace("%z1%", String.valueOf(getConfig().getInt("Payment.area.pos1.z")))
                                            .replace("%x2%", String.valueOf(getConfig().getInt("Payment.area.pos2.x")))
                                            .replace("%y2%", String.valueOf(getConfig().getInt("Payment.area.pos2.y")))
                                            .replace("%z2%", String.valueOf(getConfig().getInt("Payment.area.pos2.z")));
                                    sender.sendMessage(Utils.colorFormat(message));
                                }
                                break;
                            default:
                                sender.sendMessage(Utils.colorFormat(getConfig().getString("Messages.config-missing-arguments")));
                        }
                    }
                default:
                    if(args.length == 1) {
                        sender.sendMessage(Utils.colorFormat(getConfig().getString("Messages.wrong-argument")));
                    }
            }
        }
        return false;
    }
}
