package indy.payments.mysql;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class MySQL {

    private final String host = getConfig().getString("Database.host");
    private final String port = getConfig().getString("Database.port");
    private final String database = getConfig().getString("Database.database");
    private final String user = getConfig().getString("Database.user");
    private final String password = getConfig().getString("Database.password");

    private Connection connection;

    public Plugin plugin() {
        return Bukkit.getServer().getPluginManager().getPlugin("DailyPayments");
    }

    public FileConfiguration getConfig() {
        return Bukkit.getServer().getPluginManager().getPlugin("DailyPayments").getConfig();
    }

    public boolean isConnected() {
        return (connection != null);
    }

    public void connect() throws ClassNotFoundException, SQLException {
        if(!isConnected()) {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false", user, password);
        }
    }

    public void disconnect() {
        if(isConnected()) {
            try {
                connection.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void createTable() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS dailypayments " +
                    "(ID INT AUTO_INCREMENT, NAME VARCHAR(20), UUID VARCHAR(150), DATE DATETIME, PRIMARY KEY (ID))");
            preparedStatement.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void savePayment(Player player) {
        try {
            String name = player.getName();
            UUID uuid = player.getUniqueId();

            SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO dailypayments (NAME, UUID, DATE) VALUES " +
                    "('" + name + "', '" + uuid + "', '" + date_format.format(date) + "')");
            preparedStatement.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
