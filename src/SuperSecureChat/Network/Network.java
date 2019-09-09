package SuperSecureChat.Network;

import SuperSecureChat.ClassConnector;
import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Contacts.ContactList;
import SuperSecureChat.Controller.NetworkController;
import SuperSecureChat.Crypto.Crypto;
import SuperSecureChat.Database;
import SuperSecureChat.Main;
import SuperSecureChat.Message;
import SuperSecureChat.NetworkMap.NetworkContactMessage;
import SuperSecureChat.NetworkMap.NetworkMessage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;

public class Network {

    private static final Network INSTANCE = new Network();

    private ArrayList<TCPServer> tcpServers = new ArrayList<>();
    private ArrayList<TCPClient> tcpClients = new ArrayList<>();
    private ArrayList<String> myIPs;
    private ArrayList<String> otherIPs = new ArrayList<>();
    private ArrayList<String> secretBlockedIPs = new ArrayList<>();
    private HashMap<String, Long> otherIPsLastPing = new HashMap<>();
    private ObservableList<String> relayedMessages = FXCollections.observableArrayList();
    private ObservableList<String> relayedContacts = FXCollections.observableArrayList();

    public Network() {
        myIPs = getMyIpAddresses();
        for (String ip : myIPs) {
            TCPServer tcpServer = new TCPServer(ip);
            if (!tcpServer.isError()) {
                System.out.println("Created Server: " + ip);
                tcpServers.add(tcpServer);
            }
        }

    }

    public static Network getInstance() {
        return INSTANCE;
    }

    public void initUDP() {
        System.out.println("Start UDP...");
        new Thread(() -> UDPServer.getInstance().run()).start();
        new Thread(() -> UDPClient.getInstance().run()).start();
    }

    public void sendMessage(Message m) {
        new Thread(() -> {
            for (String ip : otherIPs) {
                TCPClient tcpClient = new TCPClient(ip, TCPServer.PORT);
                tcpClient.sendMessage(m);
                tcpClient.close();
                Message mm = new Message();
                mm.setSender(Contact.getMyContact());
                mm.setReceiver(ContactList.getInstance().getContactByIP(ip));
                ClassConnector.getInstance().sendMessageToNetworkMap(m, mm);
            }
        }).start();
    }

    public void sendContact(Contact c, NetworkContactMessage networkContactMessage) {
        new Thread(() -> {
            NetworkController networkController = ClassConnector.getInstance().getNetworkController();
            for (String ip : otherIPs) {
                TCPClient tcpClient = new TCPClient(ip, TCPServer.PORT);
                if (networkContactMessage == null) {
                    tcpClient.sendContact(c);
                } else {
                    tcpClient.relayContact(c);
                    NetworkContactMessage response = new NetworkContactMessage(c,
                            networkController.getNetworkContactByContact(Contact.getMyContact()),
                            networkController.getNetworkContactByContact(ContactList.getInstance().getContactByIP(ip)));
                    networkContactMessage.addResponse(response);
                }
                tcpClient.close();
                Message m = new Message();
                m.setSender(Contact.getMyContact());
                m.setReceiver(ContactList.getInstance().getContactByIP(ip));
            }
        }).start();
    }

    void addIP(String ip) {
        if (!otherIPs.contains(ip)) {
            otherIPs.add(ip);
            System.out.println("Neue IP: " + ip);
            TCPClient tcpClient = new TCPClient(ip, TCPServer.PORT);
            Contact me = Contact.getMyContact();
            tcpClient.sendContact(me);
            tcpClient.close();
            tcpClient = new TCPClient(ip, TCPServer.PORT);
            tcpClient.sendText("GETCONTACT");
            tcpClient.close();
            tcpClient = new TCPClient(ip, TCPServer.PORT);
            tcpClient.sendText("GETMYMM:" + me.getId()); //GetMyMessages
            tcpClient.close();
            tcpClient = new TCPClient(ip, TCPServer.PORT);
            tcpClient.sendText("VERSION:" + Main.VERSION); //GetMyMessages
            tcpClient.close();

        }
        otherIPsLastPing.put(ip, Instant.now().getEpochSecond());
        ContactList.getInstance().setOnlineByIp(ip);
    }

    ArrayList<String> getMyIPs() {
        return myIPs;
    }


    private ArrayList<String> getMyIpAddresses() {
        ArrayList<String> myIPs = new ArrayList<>();
        try {
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();

                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        myIPs.add(addr.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return myIPs;
    }

    public void relayMessage(Message message, NetworkMessage networkMessage) {
        NetworkController networkController = ClassConnector.getInstance().getNetworkController();
        if (!relayedMessages.contains(message.getId())) {
            relayedMessages.add(message.getId());
            sendMessage(message);
            if (networkMessage != null) {
                for (String ip : otherIPs) {
                    NetworkMessage nm = new NetworkMessage(message.getText(),
                            networkController.getNetworkContactByContact(Contact.getMyContact()),
                            networkController.getNetworkContactByContact(ContactList.getInstance().getContactByIP(ip)));
                    networkMessage.addResponse(nm);
                }
            }
        }
    }

    public void relayContact(Contact contact, NetworkContactMessage networkContactMessage) {
        if (!relayedContacts.contains(contact.getId() + contact.getImage().hashCode())) {
            relayedContacts.add(contact.getId() + contact.getImage().hashCode());
            sendContact(contact, networkContactMessage);
        }
    }


    public byte[] getNewSecretKeyFrom(Contact contact) throws IOException {
        if (!secretBlockedIPs.contains(contact.getUrl())) {
            secretBlockedIPs.add(contact.getUrl());
            Crypto crypto = new Crypto();
            crypto.generateKeys();
            TCPClient tcpClient = new TCPClient(contact.getUrl(), TCPServer.PORT);
            String publicKey = tcpClient.exchangePublicKey(Base64.getEncoder().encodeToString(crypto.getPublicKey().getEncoded()));
            tcpClient.close();
            crypto.receivePublicKey(Base64.getDecoder().decode(publicKey));
            crypto.generateCommonSecretKey();
            byte[] secretKey = crypto.getSecretKey();
            Database.getInstance().addSecretKey(contact, secretKey);
            return secretKey;
        } else {
            throw new IOException("Only one SecretKey at a time!");
        }
    }

    public void updateContact() {
        new Thread(() -> {
            for (String ip : otherIPs) {
                TCPClient tcpClient = new TCPClient(ip, TCPServer.PORT);
                tcpClient.sendContact(Contact.getMyContact());
                tcpClient.close();
            }
        }).start();
    }

    void clearCache() {
        relayedMessages.clear();
        relayedContacts.clear();
    }


    // TODO Methode, der man eine Adresse übergibt und die dann prüft, ob die Adresse via Ping erreichbar ist

    // TODO Broadcast an gesamtes Netzwerk

    // TODO send Message to all Contacts
}
