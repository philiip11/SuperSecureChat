package SuperSecureChat.Network;

import SuperSecureChat.Contacts.Contact;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
    }

    public static boolean file_put_contents(String filename, String data) {
        try {
            FileWriter fstream = new FileWriter(filename, true);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(data);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (true);
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


    // TODO Methode, der man eine Adresse übergibt und die dann prüft, ob die Adresse via Ping erreichbar ist

    // TODO Broadcast an gesamtes Netzwerk

    // TODO send Message to all Contacts
}
