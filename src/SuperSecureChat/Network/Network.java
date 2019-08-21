package SuperSecureChat.Network;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

public class Network {

    static ArrayList<String> getMyIpAdresses() {
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

    //TODO Methode, der man eine Adresse übergibt und die dann prüft, ob die Adresse via Ping erreichbar ist

    // TODO Broadcast an gesamtes Netzwerk

    // TODO send Message to all Contacts
    // Port 40 scheint frei zu sein
}
