package indy.payments.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static Plugin plugin() {
        return Bukkit.getServer().getPluginManager().getPlugin("DailyPayments");
    }

    public static FileConfiguration getConfig() {
        return plugin().getConfig();
    }

    public static String colorFormat(String message){
        return ChatColor.translateAlternateColorCodes('&',message);
    }

    public static String getMessage(String path) {
        return colorFormat(getConfig().getString(path));
    }

    public static String formatDate(Date date) {
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
        return date_format.format(date);
    }
}
