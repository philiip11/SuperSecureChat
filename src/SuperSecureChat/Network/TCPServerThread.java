package SuperSecureChat.Network;

import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Contacts.ContactList;

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
                if ((line != null) && line.length() > 8) {
                    System.out.println(line);
                    String command = line.substring(0, 8);
                    String json = line.substring(8);
                    System.out.println(command);
                    switch (command) {
                        case "MESSAGE:":
                            System.out.println("Neue Nachricht empfangen!");
                            //TODO Mach was mit der Nachricht
                            break;
                        case "CONTACT:":
                            System.out.println("Kontakt empfangen!");
                            ContactList.getInstance().addContact(Contact.fromJSON(json));
                            //TODO Mach was mit dem Kontakt
                            break;
                    }

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
