package SuperSecureChat.Network;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPServer implements Runnable {
    private static final UDPServer INSTANCE = new UDPServer();

    public static void main(String[] args) {
        UDPServer st = new UDPServer();
        st.run();
    }

    public static UDPServer getInstance() {
        return INSTANCE;
    }

    @Override
    public void run() {
        try {
            ArrayList<String> myIPs = Network.getInstance().getMyIPs();
            //Keep a socket open to listen to all the UDP trafic that is destined for this port
            DatagramSocket socket = new DatagramSocket(40, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);

            while (true) {
                //System.out.println(getClass().getName() + ">>>Ready to receive broadcast packets!");

                //Receive a packet
                byte[] recvBuf = new byte[15000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);


                if (myIPs.contains(packet.getAddress().getHostAddress())) {
                    //System.out.println(getClass().getName() + ">>>Discovery packet received from: myself (" + packet.getAddress().getHostAddress() + ")");
                    //System.out.println(getClass().getName() + ">>>Packet received; data         : " + new String(packet.getData()));

                } else {
                    //Packet received
                    //System.out.println(getClass().getName() + ">>>Discovery packet received from: " + packet.getAddress().getHostAddress());
                    //System.out.println(getClass().getName() + ">>>Packet received; data         : " + new String(packet.getData()));

                    //See if the packet holds the right command (message)
                    String message = new String(packet.getData()).trim();
                    if (message.equals("DISCOVER_SUPERSECURECHAT_REQUEST")) {
                        byte[] sendData = "DISCOVER_SUPERSECURECHAT_RESPONSE".getBytes();

                        //Send a response
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                        socket.send(sendPacket);

                        //System.out.println(getClass().getName() + ">>>Sent packet to                : " + sendPacket.getAddress().getHostAddress());
                    }
                }
            }
        } catch (BindException ex) {
            System.out.println("SuperSecureChat läuft bereits!");
        } catch (IOException ex) {
            Logger.getLogger(UDPServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
