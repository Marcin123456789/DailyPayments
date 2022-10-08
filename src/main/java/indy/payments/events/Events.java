package indy.payments.events;

import indy.payments.mysql.MySQL;
import indy.payments.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;
import java.util.ArrayList;

public class Events implements Listener {

    public MySQL SQL;
    private org.bukkit.World world = Bukkit.getServer().getWorld(getConfig().getString("Payment.area.world"));
    private Location firstAreaCorner = new Location(world,
            getConfig().getInt("Payment.area.pos1.x"),
            getConfig().getInt("Payment.area.pos1.y"),
            getConfig().getInt("Payment.area.pos1.z"));
    private Location secondAreaCorner = new Location(world,
            getConfig().getInt("Payment.area.pos2.x"),
            getConfig().getInt("Payment.area.pos2.y"),
            getConfig().getInt("Payment.area.pos2.z"));
    public Plugin plugin() {
        return Bukkit.getServer().getPluginManager().getPlugin("DailyPayments");
    }

    public FileConfiguration getConfig() {
        return plugin().getConfig();
    }

    public ArrayList<Location> getArea(Location firstCorner, Location secondCorner) {

        ArrayList<Location> area = new ArrayList<Location>();

        int MinX, MinY, MinZ, MaxX, MaxY, MaxZ;

        MinX = Math.min(firstCorner.getBlockX(), secondCorner.getBlockX());
        MaxX = Math.max(firstCorner.getBlockX(), secondCorner.getBlockX());
        MinY = Math.min(firstCorner.getBlockY(), secondCorner.getBlockY());
        MaxY = Math.max(firstCorner.getBlockY(), secondCorner.getBlockY());
        MinZ = Math.min(firstCorner.getBlockZ(), secondCorner.getBlockZ());
        MaxZ = Math.max(firstCorner.getBlockZ(), secondCorner.getBlockZ());

        area.add(new Location(firstCorner.getWorld(), MinX, MinY, MinZ));
        area.add(new Location(secondCorner.getWorld(), MaxX, MaxY, MaxZ));

        return area;
    }

    Location areaMin = getArea(firstAreaCorner, secondAreaCorner).get(0);
    Location areaMax = getArea(firstAreaCorner, secondAreaCorner).get(1);

    public boolean contains(Location location) {
        return areaMin.getX() <= location.getX() && areaMax.getX() >= location.getX() &&
               areaMin.getY() <= location.getY() && areaMax.getY() >= location.getY() &&
               areaMin.getZ() <= location.getZ() && areaMax.getZ() >= location.getZ();
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        this.SQL = new MySQL();
        if(getConfig().getBoolean("Payment.enabled") &&
           getConfig().getString("Payment.payment-type").equalsIgnoreCase("block") &&
           e.getPlayer().hasPermission("payments.pay")) {
            if(contains(e.getBlock().getLocation()) && e.getBlock().getBlockData().getMaterial().equals(Material.getMaterial(getConfig().getString("Payment.block-type")))) {
                if(getConfig().getBoolean("Payment.announce-to-chat")) {
                    String message = getConfig().getString("Payment.message")
                           .replace("%player%", e.getPlayer().getName());
                    for(Player players : Bukkit.getOnlinePlayers()) {
                        if(players.hasPermission("payments.announce"))
                            players.sendMessage(Utils.colorFormat(message));
                    }
                }
                if (getConfig().getBoolean("Payment.announce-to-console")) {
                    String message = getConfig().getString("Payment.message")
                           .replace("%player%", e.getPlayer().getName());
                    Bukkit.getServer().getConsoleSender().sendMessage(Utils.colorFormat(message));
                }
                try {
                    SQL.connect();
                } catch (ClassNotFoundException | SQLException ex) {
                    ex.printStackTrace();
                }
                SQL.savePayment(e.getPlayer());
            } else if(contains(e.getBlock().getLocation()) && getConfig().getBoolean("Payment.prevent-placing-wrong-block")) {
                String message = getConfig().getString("Messages.wrong-block-message")
                       .replace("%block-type%", getConfig().getString("Payment.block-type"));
                e.getPlayer().sendMessage(Utils.colorFormat(message));
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if(getConfig().getBoolean("Payment.enabled") &&
           getConfig().getBoolean("Stealing.prevent-players-from-stealing") &&
           getConfig().getString("Payment.payment-type").equalsIgnoreCase("block")) {
            if(contains(event.getBlock().getLocation()) && event.getBlock().getBlockData().getMaterial().equals(Material.getMaterial(getConfig().getString("Payment.block-type")))) {
                if(getConfig().getBoolean("Stealing.announce-to-chat") && !event.getPlayer().hasPermission("payments.steal")) {
                    String message = getConfig().getString("Stealing.message")
                           .replace("%player%", event.getPlayer().getName());
                    for (Player players : Bukkit.getOnlinePlayers()) {
                        if (players.hasPermission("payments.announce")) {
                            players.sendMessage(Utils.colorFormat(message));
                        }
                    }
                }
                if(getConfig().getBoolean("Stealing.announce-to-console") && !event.getPlayer().hasPermission("payments.steal")) {
                    Bukkit.getServer().getConsoleSender().sendMessage(Utils.colorFormat(getConfig().getString("Stealing.message")
                           .replace("%player%", event.getPlayer().getName())));
                }
                if(getConfig().getBoolean("Stealing.cancel-stealing") && !event.getPlayer().hasPermission("payments.steal")) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
