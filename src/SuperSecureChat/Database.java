package SuperSecureChat;

import java.sql.*;

class DBController {

    private static final DBController dbcontroller = new DBController();
    private static final String DB_PATH = "testdb.db";
    private static final int DB_VERSION = 1;
    private static Connection connection;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Fehler beim Laden des JDBC-Treibers");
            e.printStackTrace();
        }
    }

    private DBController() {
    }

    public static DBController getInstance() {
        return dbcontroller;
    }

    public static void main(String[] args) {
        DBController dbc = DBController.getInstance();
        dbc.initDBConnection();
        dbc.handleDB();
    }

    private void initDBConnection() {
        try {
            if (connection != null)
                return;
            System.out.println("Creating Connection to Database...");
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
            if (!connection.isClosed())
                System.out.println("...Connection established");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    if (!connection.isClosed() && connection != null) {
                        connection.close();
                        if (connection.isClosed())
                            System.out.println("Connection to Database closed");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void handleDB() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("PRAGMA user_version;");
            int databaseVersion = rs.getInt(1);
            System.out.println(rs.getInt(1));
            if (databaseVersion != DB_VERSION) {
                stmt.executeUpdate("PRAGMA user_version = " + DB_VERSION);


                stmt = connection.createStatement();
                stmt.executeUpdate("DROP TABLE IF EXISTS messages;");
                stmt.executeUpdate("DROP TABLE IF EXISTS contacts;");
                stmt.executeUpdate("DROP TABLE IF EXISTS cryptoKeys;");

                stmt.executeUpdate("CREATE TABLE messages (id TEXT ,sender TEXT, reciever TEXT,text TEXT, data BLOB, trace TEXT);");
                stmt.executeUpdate("CREATE TABLE contacts (id TEXT, firstname TEXT, lastname TEXT, url TEXT);");
                stmt.executeUpdate("CREATE TABLE cryptoKeys (id TEXT, firstname TEXT, lastname TEXT, url TEXT);");
                stmt.close();

            }
            rs.close();
            connection.close();
        } catch (SQLException e) {
            System.err.println("Couldn't handle DB-Query");
            e.printStackTrace();
        }
    }
}