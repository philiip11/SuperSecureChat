package SuperSecureChat;

import SuperSecureChat.Contacts.Contact;

import java.sql.*;

public class Database {

    private static final Database database = new Database();
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

    private Database() {
    }

    public static Database getInstance() {
        return database;
    }

    public static void main(String[] args) {
        Database dbc = Database.getInstance();
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

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (!connection.isClosed() && connection != null) {
                    connection.close();
                    if (connection.isClosed())
                        System.out.println("Connection to Database closed");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }));
    }

    private void handleDB() {
        try {
            Statement stmt = connection.createStatement();
            String pragma = "PRAGMA "; // Fixes Intellij Errors
            ResultSet rs = stmt.executeQuery(pragma + "user_version;");
            int databaseVersion = rs.getInt(1);
            System.out.println(rs.getInt(1));
            if (databaseVersion != DB_VERSION) {
                stmt.executeUpdate(pragma + "user_version = " + DB_VERSION);


                stmt = connection.createStatement();
                stmt.executeUpdate("DROP TABLE IF EXISTS messages;");
                stmt.executeUpdate("DROP TABLE IF EXISTS contacts;");
                stmt.executeUpdate("DROP TABLE IF EXISTS cryptoKeys;");

                stmt.executeUpdate("CREATE TABLE contacts (id TEXT, firstname TEXT, lastname TEXT, url TEXT, lastOnline TEXT, image BLOB);");
                stmt.executeUpdate("CREATE TABLE messages (id TEXT, sender TEXT, receiver TEXT, text TEXT, data BLOB, trace TEXT,  created TEXT, received INTEGER, 'read' INTEGER," +
                        "UNIQUE(id, sender), FOREIGN KEY(sender) REFERENCES contacts (id), FOREIGN KEY(receiver) REFERENCES contacts (id));");
                stmt.executeUpdate("CREATE TABLE cryptoKeys (id TEXT, firstname TEXT, lastname TEXT, url TEXT, 'key' TEXT);");
                stmt.close();

            }
            rs.close();
            connection.close();
        } catch (SQLException e) {
            System.err.println("Couldn't handle DB-Query");
            e.printStackTrace();
        }
    }

    public void newMessage(Message message) {
        try {
            PreparedStatement ps = connection.prepareStatement("");
            //TODO

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void newContact(Contact contact) {
        //TODO
    }

    public int countUnreadMessagesByContact(Contact contact) {
        //TODO
        return 0;
    }

}