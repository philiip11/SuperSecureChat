package SuperSecureChat.Network;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class TCPServerThread extends Thread {
    protected Socket socket;

    public TCPServerThread(Socket clientSocket) {
        this.socket = clientSocket;
    }

    public void run() {
        InputStream inp = null;
        BufferedReader brinp = null;
        DataOutputStream out = null;
        try {
            inp = socket.getInputStream();
            brinp = new BufferedReader(new InputStreamReader(inp));
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            return;
        }
        String line;
        while (true) {
            try {
                line = brinp.readLine();
                if ((line == null) || line.equalsIgnoreCase("QUIT")) {
                    socket.close();
                    return;
                } else {

                    out.writeBytes(line + "\n\r");
                    System.out.println(line);
                    out.flush();
                }
            } catch (SocketException ignored) {

            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

}
