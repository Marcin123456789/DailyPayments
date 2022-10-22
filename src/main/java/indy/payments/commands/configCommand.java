package indy.payments.commands;

import indy.payments.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class configCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String arg, String[] args) {
        if(sender.hasPermission("payments.config")) {
            if (args.length == 1) {
                sender.sendMessage(Utils.colorFormat(Utils.getConfig().getString("Messages.config-missing-arguments")));
            } else {
                switch (args[1]) {
                    case "set":
                        if (args.length == 2) {
                            sender.sendMessage(Utils.colorFormat(Utils.getConfig().getString("Messages.config-set-missing-arguments")));
                        } else {
                            switch (args[2]) {
                                case "block":
                                    setBlockType(sender, args[3]);
                                    break;
                                case "world":
                                    setWorld(sender, args[3]);
                                    break;
                                case "firstPosition":
                                    if (args[3].matches("[0-9]+") && args[4].matches("[0-9]+") && args[5].matches("[0-9]+")) {
                                        String[] location = {args[3], args[4], args[5]};
                                        setPosition(sender, 1, location);
                                    } else {
                                        sender.sendMessage(Utils.getMessage("Messages.not-integer-input"));
                                    }
                                    setPosition(sender, 1);
                                    break;
                                case "secondPosition":
                                    if (args[3].matches("[0-9]+") && args[4].matches("[0-9]+") && args[5].matches("[0-9]+")) {
                                        String[] location = {args[3], args[4], args[5]};
                                        setPosition(sender, 2, location);
                                    } else {
                                        sender.sendMessage(Utils.getMessage("Messages.not-integer-input"));
                                    }
                                    setPosition(sender, 2);
                                    break;
                                default:
                                    sender.sendMessage(Utils.colorFormat(Utils.getConfig().getString("Messages.config-set-missing-arguments")));
                            }
                        }
                        break;
                    case "get":
                        for (Object element : Utils.getConfig().getList("Messages.config-get")) {
                            String message = element.toString()
                                    .replace("%block%", Utils.getConfig().getString("Payment.block-type"))
                                    .replace("%world%", Utils.getConfig().getString("Payment.area.world"))
                                    .replace("%x1%", String.valueOf(Utils.getConfig().getInt("Payment.area.pos1.x")))
                                    .replace("%y1%", String.valueOf(Utils.getConfig().getInt("Payment.area.pos1.y")))
                                    .replace("%z1%", String.valueOf(Utils.getConfig().getInt("Payment.area.pos1.z")))
                                    .replace("%x2%", String.valueOf(Utils.getConfig().getInt("Payment.area.pos2.x")))
                                    .replace("%y2%", String.valueOf(Utils.getConfig().getInt("Payment.area.pos2.y")))
                                    .replace("%z2%", String.valueOf(Utils.getConfig().getInt("Payment.area.pos2.z")));
                            sender.sendMessage(Utils.colorFormat(message));
                        }
                        break;
                    default:
                        sender.sendMessage(Utils.colorFormat(Utils.getConfig().getString("Messages.config-missing-arguments")));
                }
            }
        }
        return false;
    }

    public void setBlockType(CommandSender sender, String... material) {
        if(material == null) {
            if (sender instanceof Player) {
                Player p = (Player) sender;

                Utils.getConfig().set("Payment.block-type", p.getInventory().getItemInMainHand().getType().toString());
                Utils.plugin().saveConfig();
                Utils.plugin().reloadConfig();

                sender.sendMessage(Utils.colorFormat(Utils.getConfig().getString("Messages.block-change")
                        .replace("%block%", p.getInventory().getItemInMainHand().getType().toString())));
            } else {
                sender.sendMessage(Utils.colorFormat(Utils.getConfig().getString("Messages.non-player-executor")));
            }
        } else {
            Utils.getConfig().set("Payment.block-type", material);
            Utils.plugin().saveConfig();
            Utils.plugin().reloadConfig();

            sender.sendMessage(Utils.colorFormat(Utils.getConfig().getString("Messages.block-change")
                    .replace("%block%", material.toString())));
        }
    }

    public void setWorld(CommandSender sender, String... world) {
        if(world == null) {
            if (sender instanceof Player) {
                Player p = (Player) sender;

                Utils.getConfig().set("Payment.area.world", p.getLocation().getWorld().getName());
                Utils.plugin().saveConfig();
                Utils.plugin().reloadConfig();

                sender.sendMessage(Utils.colorFormat(Utils.getConfig().getString("Messages.world-change")
                        .replace("%world%", p.getLocation().getWorld().getName())));
            } else {
                sender.sendMessage(Utils.colorFormat(Utils.getConfig().getString("Messages.non-player-executor")));
            }
        } else {
            Utils.getConfig().set("Payment.area.world", world);
            Utils.plugin().saveConfig();
            Utils.plugin().reloadConfig();

            sender.sendMessage(Utils.colorFormat(Utils.getConfig().getString("Messages.world-change")
                    .replace("%world%", world.toString())));
        }
    }

    public void setPosition(CommandSender sender, int index, String[]... location) {
        if(location == null) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                Utils.getConfig().set("Payment.area.pos" + index + ".x", (int) p.getLocation().getX());
                Utils.getConfig().set("Payment.area.pos" + index + ".y", (int) p.getLocation().getY());
                Utils.getConfig().set("Payment.area.pos" + index + ".z", (int) p.getLocation().getZ());
                Utils.plugin().saveConfig();
                Utils.plugin().reloadConfig();

                sender.sendMessage(Utils.colorFormat(Utils.getConfig().getString("Messages.first-position-change")
                        .replace("%x%", String.valueOf((int) p.getLocation().getX()))
                        .replace("%y%", String.valueOf((int) p.getLocation().getY()))
                        .replace("%z%", String.valueOf((int) p.getLocation().getZ()))));
            } else {
                sender.sendMessage(Utils.colorFormat(Utils.getConfig().getString("Messages.non-player-executor")));
            }
        } else {
                Utils.getConfig().set("Payment.area.pos" + index + ".x", Integer.valueOf(location[3].toString()));
                Utils.getConfig().set("Payment.area.pos" + index + ".y", Integer.valueOf(location[4].toString()));
                Utils.getConfig().set("Payment.area.pos" + index + ".z", Integer.valueOf(location[5].toString()));
                Utils.plugin().saveConfig();
                Utils.plugin().reloadConfig();

                sender.sendMessage(Utils.colorFormat(Utils.getConfig().getString("Messages.first-position-change")
                        .replace("%x%", location[3].toString())
                        .replace("%y%", location[4].toString())
                        .replace("%z%", location[5].toString())));
        }
    }
}
