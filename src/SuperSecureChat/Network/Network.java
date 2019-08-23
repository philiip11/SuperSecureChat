package SuperSecureChat.Network;

import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Contacts.ContactList;
import SuperSecureChat.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

public class Network {

    private static final Network INSTANCE = new Network();

    private ArrayList<TCPServer> tcpServers = new ArrayList<>();
    private ArrayList<TCPClient> tcpClients = new ArrayList<>();
    private ArrayList<String> myIPs;
    private ArrayList<String> otherIPs = new ArrayList<>();
    private HashMap<String, Long> otherIPsLastPing = new HashMap<>();
    private ObservableList<String> relayedMessages = FXCollections.observableArrayList();

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
            tcpClient.sendText("GETCONTACT");

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

    public void relayMessage(Message message) {
        if (!relayedMessages.contains(message.getId())) {
            relayedMessages.add(message.getId());
            sendMessage(message);
        }

    }


    // TODO Methode, der man eine Adresse übergibt und die dann prüft, ob die Adresse via Ping erreichbar ist

    // TODO Broadcast an gesamtes Netzwerk

    // TODO send Message to all Contacts
}
