package SuperSecureChat.Network;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

    public static final int PORT = 40;
    private ServerSocket server;
    private boolean error = false;

    public TCPServer(String ipAddress) {
        try {
            this.server = new ServerSocket(PORT, 1, InetAddress.getByName(ipAddress));
            new Thread(() -> {
                Socket socket = null;
                while (true) {
                    try {
                        socket = server.accept();
                    } catch (IOException e) {
                        System.out.println("I/O error: " + e);
                    }
                    // new thread for a client
                    new TCPServerThread(socket).start();
                }
            }).start();
        } catch (BindException e) {
            error = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {


    }

    public boolean isError() {
        return error;
    }
}
