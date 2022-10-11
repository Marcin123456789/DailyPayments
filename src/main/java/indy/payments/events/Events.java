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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Events implements Listener {

    public MySQL SQL;

    public static FileConfiguration getConfig() {
        return Utils.getConfig();
    }

    private org.bukkit.World world = Bukkit.getServer().getWorld(getConfig().getString("Payment.area.world"));
    private Location firstAreaCorner = new Location(world,
            getConfig().getInt("Payment.area.pos1.x"),
            getConfig().getInt("Payment.area.pos1.y"),
            getConfig().getInt("Payment.area.pos1.z"));
    private Location secondAreaCorner = new Location(world,
            getConfig().getInt("Payment.area.pos2.x"),
            getConfig().getInt("Payment.area.pos2.y"),
            getConfig().getInt("Payment.area.pos2.z"));

    public ArrayList<Location> getArea(Location firstCorner, Location secondCorner) {

        ArrayList<Location> area = new ArrayList();

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
            try {
                SQL.connect();
                ResultSet results = SQL.getPayments(e.getPlayer());
                ArrayList<String> payments = new ArrayList();
                Date date = new Date(System.currentTimeMillis());

                while(results.next()) {
                    payments.add(results.getString(1));
                }
                if(contains(e.getBlock().getLocation()) &&
                   e.getBlock().getBlockData().getMaterial().equals(Material.getMaterial(getConfig().getString("Payment.block-type")))) {
                    if((!payments.contains(Utils.formatDate(date)) ||
                       (!getConfig().getBoolean("Payment.only-one-payment-per-day")) && getConfig().getInt("Payment.allow-paying-for-next-days") == 0)) {
                        if (getConfig().getBoolean("Payment.announce-to-chat")) {
                            String message = Utils.getMessage("Payment.message")
                                    .replace("%player%", e.getPlayer().getName());
                            for (Player players : Bukkit.getOnlinePlayers()) {
                                if (players.hasPermission("payments.announce"))
                                    players.sendMessage(message);
                            }
                        }
                        if (getConfig().getBoolean("Payment.announce-to-console")) {
                            String message = Utils.getMessage("Payment.message")
                                    .replace("%player%", e.getPlayer().getName());
                            Bukkit.getServer().getConsoleSender().sendMessage(message);
                        }
                        date = new Date(System.currentTimeMillis());
                        SQL.savePayment(e.getPlayer(), date);
                    } else if(!getConfig().getBoolean("Payment.only-one-payment-per-day") && getConfig().getInt("Payment.allow-paying-for-next-days") > 0) {
                        for(int i = 0; i < getConfig().getInt("Payment.allow-paying-for-next-days"); i++) {
                            Date nextDate = new Date(System.currentTimeMillis() + ((i + 1) * 86400000));
                            results = SQL.getPayments(e.getPlayer());

                            while(results.next()) {
                                payments.add(results.getString(1));
                            }

                            if(!payments.contains(Utils.formatDate(nextDate))) {
                                SQL.savePayment(e.getPlayer(), nextDate);
                                if (getConfig().getBoolean("Payment.announce-to-chat")) {
                                    String message = Utils.getMessage("Payment.message")
                                            .replace("%player%", e.getPlayer().getName());
                                    for (Player players : Bukkit.getOnlinePlayers()) {
                                        if (players.hasPermission("payments.announce"))
                                            players.sendMessage(message);
                                    }
                                }
                                if (getConfig().getBoolean("Payment.announce-to-console")) {
                                    String message = Utils.getMessage("Payment.message")
                                            .replace("%player%", e.getPlayer().getName());
                                    Bukkit.getServer().getConsoleSender().sendMessage(message);
                                }
                                break;
                            } else if(!(i < (getConfig().getInt("Payment.allow-paying-for-next-days") - 1))) {
                                e.getPlayer().sendMessage(Utils.getMessage("Messages.tex-paid-already"));
                                e.setCancelled(true);
                            }
                        }
                    } else {
                        e.getPlayer().sendMessage(Utils.getMessage("Messages.tex-paid-already"));
                        e.setCancelled(true);
                    }
                } else if(contains(e.getBlock().getLocation()) && getConfig().getBoolean("Payment.prevent-placing-wrong-block")) {
                    String message = getConfig().getString("Messages.wrong-block-message")
                            .replace("%block-type%", getConfig().getString("Payment.block-type"));
                    e.getPlayer().sendMessage(Utils.colorFormat(message));
                    e.setCancelled(true);
                }
            } catch (SQLException | ClassNotFoundException ex) {
                ex.printStackTrace();
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

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        this.SQL = new MySQL();
        Date date = new Date(System.currentTimeMillis());
        try {
            SQL.connect();
            SQL.registerJoin(e.getPlayer());

            if(getConfig().getBoolean("Payment.reminder-on-join")) {

                ResultSet results = SQL.getPayments(e.getPlayer());
                ArrayList<String> payments = new ArrayList();

                while (results.next()) {
                        payments.add(results.getString(1));
                }
                if (!payments.contains(Utils.formatDate(date).toString())) {
                        e.getPlayer().sendMessage(Utils.colorFormat(getConfig().getString("Messages.payment-reminder")));
                }
            }
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static int scheduledEvent(Plugin plugin, Runnable task, int hour, int min) {
        Calendar calendar = Calendar.getInstance();
        long currentTime = System.currentTimeMillis();
        long timeOffset;
        long ticks;

        if(calendar.get(Calendar.HOUR_OF_DAY) >= hour && calendar.get(Calendar.MINUTE) >= min) {
            calendar.add(Calendar.DATE, 1);
        }

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        timeOffset = calendar.getTimeInMillis() - currentTime;
        ticks = timeOffset / 50L;

        return Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, task, ticks, 1728000L);
    }
}
