package SuperSecureChat;

import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Network.Network;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;

public class Database {

    private static final int DB_VERSION = 15;

    private static final Database INSTANCE = new Database();
    private final String DB_PATH = System.getenv("APPDATA") + "\\SuperSecureChat\\db.db";
    private Connection connection;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Fehler beim Laden des JDBC-Treibers");
            e.printStackTrace();
        }
    }

    private Database() {
        initDBConnection();
        handleDB();
    }

    public static Database getInstance() {
        return INSTANCE;
    }

    public static void main(String[] args) {
        Database dbc = getInstance();
        dbc.initDBConnection();
        dbc.handleDB();
    }

    private void initDBConnection() {
        try {
            if (connection != null)
                return;

            File oldDB = new File("testdb.db");
            File newDB = new File(DB_PATH);
            if (!newDB.exists() && oldDB.exists()) {
                //noinspection ResultOfMethodCallIgnored
                oldDB.renameTo(newDB);
            }
            System.out.println("Creating Connection to Database...");
            System.out.println("DB_PATH = " + DB_PATH);
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
            System.out.println("Database-Version:" + rs.getInt(1));
            if (databaseVersion != DB_VERSION) {
                switch (databaseVersion) { //Do not use break; here, please!
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                        stmt.executeUpdate("DROP TABLE IF EXISTS messages;");
                        stmt.executeUpdate("DROP TABLE IF EXISTS contacts;");
                        stmt.executeUpdate("DROP TABLE IF EXISTS cryptoKeys;");
                        stmt.executeUpdate("CREATE TABLE contacts (id TEXT PRIMARY KEY , firstname TEXT, lastname TEXT, url TEXT, lastOnline INTEGER, image BLOB, publicKey BLOB);");
                        stmt.executeUpdate("CREATE TABLE messages (id TEXT PRIMARY KEY , sender TEXT, receiver TEXT, text TEXT, data BLOB, trace TEXT,  created INTEGER, received INTEGER, 'read' INTEGER, reference TEXT);");
                        stmt.executeUpdate("CREATE TABLE cryptoKeys (id TEXT, secretKey TEXT, idd INTEGER AUTO_INCREMENT PRIMARY KEY);");
                    case 13:
                        stmt.executeUpdate("ALTER TABLE messages ADD sendTo TEXT;");
                    case 14: //empty trace coloumn
                        stmt.executeUpdate("BEGIN TRANSACTION;" +
                                "CREATE TEMPORARY TABLE messages_backup(id TEXT PRIMARY KEY , sender TEXT, receiver TEXT, text TEXT, data BLOB, created INTEGER, received INTEGER, 'read' INTEGER, reference TEXT);" +
                                "INSERT INTO messages_backup SELECT id, sender, receiver, text, data, created, received, 'read', reference FROM messages;" +
                                "DROP TABLE messages;" +
                                "CREATE TABLE messages (id TEXT PRIMARY KEY , sender TEXT, receiver TEXT, text TEXT, data BLOB, trace TEXT,  created INTEGER, received INTEGER, 'read' INTEGER, reference TEXT);" +
                                "INSERT INTO messages SELECT id, sender, receiver, text, data, '', created, received, 'read', reference FROM messages_backup;" +
                                "DROP TABLE messages_backup;" +
                                "COMMIT;");
                    case 15: // Insert future Database changes here


                }
                stmt.executeUpdate(pragma + "user_version = " + DB_VERSION);
                stmt.executeUpdate("VACUUM;");
                stmt.close();
                System.out.println("Database updated to " + DB_VERSION);

            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Couldn't handle DB-Query");
            e.printStackTrace();
        }
    }

    public void updateMessage(Message message) {
        newContact(message.getSender());
        newContact(message.getReceiver());
        try {
            PreparedStatement ps = connection.prepareStatement("REPLACE INTO messages (id, sender, receiver, text, data, trace, created, received, 'read', reference) VALUES (?,?,?,?,?,?,?,?,?,?)");
            ps.setString(1, message.getId());
            ps.setString(2, message.getSender().getId());
            ps.setString(3, message.getReceiver().getId());
            ps.setString(4, message.getText());
            ps.setString(5, message.getData());
            ps.setString(6, message.getTrace());
            ps.setLong(7, message.getCreated());
            ps.setLong(8, message.getReceived());
            ps.setLong(9, message.getRead());
            ps.setLong(10, message.getRead());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void newContact(Contact contact) {
        try {


            PreparedStatement ps = connection.prepareStatement("REPLACE INTO contacts (id, firstname, lastname, url, lastOnline, image) VALUES (?,?,?,?,?,?)");


            ps.setString(1, contact.getId());
            ps.setString(2, contact.getFirstname());
            ps.setString(3, contact.getLastname());
            ps.setString(4, contact.getUrl());
            ps.setLong(5, contact.getLastOnline());
            ps.setString(6, contact.getImage());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Message> getMessagesByContacts(Contact contact1, Contact contact2) {
        ArrayList<Message> result = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM messages WHERE (sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?) AND length(text) > 0");
            ps.setString(1, contact1.getId());
            ps.setString(2, contact2.getId());
            ps.setString(3, contact2.getId());
            ps.setString(4, contact1.getId());
            parseMessages(result, ps);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Contact getContactById(String id) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM contacts WHERE (id = ?)");
            ps.setString(1, id);
            ResultSet resultSet = ps.executeQuery();
            return getContactFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Contact getPrivateKeyById(String id) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM contacts WHERE (id = ?)");
            ps.setString(1, id);
            ResultSet resultSet = ps.executeQuery();
            return getContactFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Contact getPublicKeyById(String id) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM contacts WHERE (id = ?)");
            ps.setString(1, id);
            ResultSet resultSet = ps.executeQuery();
            return getContactFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Contact> getContacts() {
        ArrayList<Contact> result = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM contacts");
            while (resultSet.next()) {
                result.add(getContactFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Contact getContactFromResultSet(ResultSet resultSet) throws SQLException {
        Contact c = new Contact();
        c.setId(resultSet.getString(1));
        c.setFirstname(resultSet.getString(2));
        c.setLastname(resultSet.getString(3));
        c.setUrl(resultSet.getString(4));
        c.setLastOnline(resultSet.getLong(5));
        c.setImage(resultSet.getString(6));
        return c;
    }

    public int countUnreadMessagesByContact(Contact contact) {
        int result = 0;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT count(*) FROM messages WHERE sender like ? AND receiver like ? AND read = 0 AND data != 'DELDATA:THIS'");
            ps.setString(1, contact.getId());
            ps.setString(2, Contact.getMyContact().getId());
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();
            result = resultSet.getInt("count(*)");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<Message> getMessagesWithId(String id) {
        ArrayList<Message> result = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM messages WHERE sender = ? OR receiver = ?");
            ps.setString(1, id);
            ps.setString(2, id);
            parseMessages(result, ps);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<Message> getMessagesWithIdNotInTrace(String id) {
        ArrayList<Message> result = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM messages WHERE trace NOT LIKE ?");
            ps.setString(1, "%" + id + "%");
            parseMessages(result, ps);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


    private void parseMessages(ArrayList<Message> result, PreparedStatement ps) throws SQLException {
        ResultSet resultSet = ps.executeQuery();
        while (resultSet.next()) {
            Message m = new Message();
            m.setId(resultSet.getString(1));
            m.setSender(getContactById(resultSet.getString(2)));
            m.setReceiver(getContactById(resultSet.getString(3)));
            m.setText(resultSet.getString(4));
            m.setData(resultSet.getString(5));
            m.setTrace(resultSet.getString(6));
            m.setCreated(resultSet.getLong(7));
            m.setReceived(resultSet.getLong(8));
            m.setRead(resultSet.getLong(9));
            m.setReferencId(resultSet.getString(10));
            result.add(m);
        }
    }

    public byte[] getSecretKeyByContact(Contact contact) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM cryptoKeys WHERE id = ?");
            ps.setString(1, contact.getId());
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                return Base64.getDecoder().decode(resultSet.getString("secretKey"));
            } else {
                return Network.getInstance().getNewSecretKeyFrom(contact);
            }

        } catch (SQLException | IOException e) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                return getSecretKeyByContact(contact);
            }
            return null;
        }

    }

    public void addSecretKey(Contact contact, byte[] secretKey) {
        try {
            if (contact.getId().equals(Contact.getMyContact().getId())) {
                new Exception("Adding own Secret Key").printStackTrace();
            }
            PreparedStatement ps = connection.prepareStatement("INSERT INTO cryptoKeys (id, secretKey) VALUES (?,?)");
            ps.setString(1, contact.getId());
            ps.setString(2, Base64.getEncoder().encodeToString(secretKey));
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void markRead(Message message) {
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE messages SET read = ? WHERE id = ?");
            ps.setLong(1, message.getRead());
            ps.setString(2, message.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Message getMessagesById(String id) throws IndexOutOfBoundsException {
        ArrayList<Message> result = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM messages WHERE id = ?");
            ps.setString(1, id);
            parseMessages(result, ps);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (result.size() > 0) {
            return result.get(0);
        } else {
            throw new IndexOutOfBoundsException("Nachricht nicht gefunden! ID: " + id);
        }
    }

    public void vacuum() {
        try {
            connection.createStatement().executeUpdate("VACUUM;");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}