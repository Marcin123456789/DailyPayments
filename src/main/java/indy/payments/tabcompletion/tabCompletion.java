package indy.payments.tabcompletion;

import indy.payments.utils.Utils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class tabCompletion implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String arg, String[] args) {
        List<String> completion = new ArrayList<>();
        if(args.length == 1) {
            completion.add("help");
            completion.add("info");
            completion.add("reload");
            completion.add("enable");
            completion.add("disable");
            completion.add("config");
            completion.add("execute_punishments");
        } if(args.length == 2 && args[0].equalsIgnoreCase("config")) {
            completion.add("set");
            completion.add("get");
        } if(args.length == 3 && args[1].equalsIgnoreCase("set")) {
            completion.add("block");
            completion.add("world");
            completion.add("firstPosition");
            completion.add("secondPosition");
        } if(args.length == 4) {
            Player p = (Player) sender;
            switch(args[2]) {
                case "block":
                    for (Material m : Material.values()) {
                        if (m.isBlock()) {
                            completion.add(m.toString().toUpperCase());
                        }
                    }
                    break;
                case "world":
                    for (World w : Utils.plugin().getServer().getWorlds()) {
                        completion.add(w.getName().toUpperCase());
                    }
                    break;
            }
        } if(6 >= args.length && args.length >= 4 && (args[2].equalsIgnoreCase("firstPosition") || args[2].equalsIgnoreCase("secondPosition"))) {
            Player p = (Player) sender;
            switch(args.length) {
                case 4:
                    completion.add(String.valueOf((int) p.getLocation().getX()));
                    break;
                case 5:
                    completion.add(String.valueOf((int) p.getLocation().getY()));
                    break;
                case 6:
                    completion.add(String.valueOf((int) p.getLocation().getZ()));
                    break;
            }
        }
        return completion;
    }
}
