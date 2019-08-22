package SuperSecureChat.Network;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPClient implements Runnable {
    // Find the server using UDP broadcast
    private DatagramSocket c;

    private static final UDPClient INSTANCE = new UDPClient();

    private Network network;

    public static void main(String[] args) {
        UDPClient ct = new UDPClient();
        ct.run();
    }

    public static UDPClient getInstance() {
        return INSTANCE;
    }

    @Override
    public void run() {
        network = Network.getInstance();
        System.out.println("UDP run");
        try {
            while (true) {
                c = new DatagramSocket();
                c.setBroadcast(true);
                ArrayList<String> myIPs = network.getMyIPs();

                byte[] sendData = "DISCOVER_SUPERSECURECHAT_REQUEST".getBytes();

//            try {
//                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 40);
//                c.send(sendPacket);
//            } catch (Exception e) {
//            }


                // Broadcast the message over all the network interfaces
                Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
                while (interfaces.hasMoreElements()) {
                    NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();


                    if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                        continue; // Don't want to broadcast to the loopback interface
                    }

                    for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                        InetAddress broadcast = interfaceAddress.getBroadcast();
                        if (broadcast == null) {
                            continue;
                        }
                        try {
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 40);
                            c.send(sendPacket);
                        } catch (Exception e) {
                        }

                        //System.out.println(getClass().getName() + ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                    }
                }

                //System.out.println(getClass().getName() + ">>> Done looping over all network interfaces. Now waiting for a reply!");

                //Wait for a response
                byte[] recvBuf = new byte[15000];
                try {
                    while (true) {
                        DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                        c.setSoTimeout(10000);
                        c.receive(receivePacket);

                        if (myIPs.contains(receivePacket.getAddress().getHostAddress())) {
                            //System.out.println(getClass().getName() + ">>> Broadcast response from myself: " + receivePacket.getAddress().getHostAddress());
                        } else {
                            //We have a response
                            //System.out.println(getClass().getName() + ">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());
                            //Check if the message is correct
                            String message = new String(receivePacket.getData()).trim();
                            if (message.equals("DISCOVER_SUPERSECURECHAT_RESPONSE")) {
                                //DO SOMETHING WITH THE SERVER'S IP (for example, store it in your controller)
                                network.addIP(receivePacket.getAddress().getHostAddress());

                                //new TCPClient(receivePacket.getAddress().getHostAddress()).init();

                                //Controller_Base.setServerIp(receivePacket.getAddress());
                            }
                        }

                    }
                } catch (SocketTimeoutException ignored) {

                }

                c.close();
            }
        } catch (
                IOException ex) {
            Logger.getLogger(UDPClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
