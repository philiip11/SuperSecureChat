package SuperSecureChat.Network;

import SuperSecureChat.ClassConnector;
import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Contacts.ContactList;
import SuperSecureChat.Message;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class TCPServerThread extends Thread {
    private Socket socket;

    TCPServerThread(Socket clientSocket) {
        this.socket = clientSocket;
    }

    public void run() {
        InputStream inp;
        BufferedReader brinp;
        DataOutputStream out;
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
                    String command = line.substring(0, 8);
                    String json = line.substring(8);
                    switch (command) {
                        case "MESSAGE:":
                            System.out.println("Neue Nachricht empfangen!");
                            System.out.println(Message.fromJSON(json).getText());
                            ClassConnector.getInstance().sendMessageToAllChatControllers(Message.fromJSON(json));
                            //TODO schreibe Nachricht in die Datenbank
                            break;
                        case "CONTACT:":
                            System.out.println("Kontakt empfangen!");
                            ContactList.getInstance().addContact(Contact.fromJSON(json));
                            //TODO Mach was mit dem Kontakt
                            break;
                        case "GETCONTA"://CT
                            System.out.println("Kontaktanfrage empfangen!");
                            TCPClient client = new TCPClient(socket.getInetAddress().getHostAddress(), TCPServer.PORT);
                            client.sendContact(Contact.getMyContact());
                            break;
                    }

                    out.writeBytes(line + "\n\r");
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
