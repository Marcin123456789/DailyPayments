package indy.payments.mysql;

import indy.payments.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class MySQL {

    private final String host = getConfig().getString("Database.host");
    private final String port = getConfig().getString("Database.port");
    private final String database = getConfig().getString("Database.database");
    private final String user = getConfig().getString("Database.user");
    private final String password = getConfig().getString("Database.password");

    private Connection connection;

    public static FileConfiguration getConfig() {
        return Utils.getConfig();
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
            PreparedStatement paymentsTable = connection.prepareStatement("CREATE TABLE IF NOT EXISTS dailypayments_payments " +
                    "(ID INT AUTO_INCREMENT, NAME VARCHAR(20), UUID VARCHAR(150), DATE DATE, PRIMARY KEY (ID))");
            PreparedStatement joinsTable = connection.prepareStatement("CREATE TABLE IF NOT EXISTS dailypayments_joins " +
                    "(ID INT AUTO_INCREMENT, NAME VARCHAR(20), UUID VARCHAR(150), DATE DATE, PRIMARY KEY (ID))");
            paymentsTable.executeUpdate();
            joinsTable.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void savePayment(Player player, Date date) {
        try {
            String name = player.getName();
            UUID uuid = player.getUniqueId();

            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO dailypayments_payments (NAME, UUID, DATE) VALUES " +
                    "('" + name + "', '" + uuid + "', '" + Utils.formatDate(date) + "')");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getPayments(Player p) {
        try {
            UUID uuid = p.getUniqueId();

            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT DATE FROM dailypayments_payments WHERE UUID = '" + uuid + "'");
            return results;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void registerJoin(Player p) {
        try {
            String name = p.getName();
            UUID uuid = p.getUniqueId();
            Date date = new Date(System.currentTimeMillis());

            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO dailypayments_joins (NAME, UUID, DATE) VALUES " +
                    "('" + name + "', '" + uuid + "', '" + Utils.formatDate(date) + "')");
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getJoins(String date) {
        try {
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT UUID FROM dailypayments_joins WHERE DATE = '" + date + "'");
            return results;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
