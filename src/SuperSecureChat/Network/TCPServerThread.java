package SuperSecureChat.Network;

import SuperSecureChat.ClassConnector;
import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Contacts.ContactList;
import SuperSecureChat.Database;
import SuperSecureChat.Message;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.time.Instant;
import java.util.ArrayList;

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
                            Message message = Message.fromJSON(json);
                            message.setReceived(Instant.now().getEpochSecond());
                            message.setTrace(message.getTrace() + "");
                            ClassConnector.getInstance().sendMessageToAllChatControllers(message);
                            Database.getInstance().newMessage(message);
                            Network.getInstance().relayMessage(message);
                            break;
                        case "CONTACT:":
                            System.out.println("Kontakt empfangen!");
                            Contact contact = Contact.fromJSON(json);
                            System.out.println(contact.getId());
                            String url = socket.getRemoteSocketAddress().toString();
                            String ip = url.substring(1).split(":")[0];
                            contact.setUrl(ip);
                            ContactList.getInstance().addContact(contact);
                            Database.getInstance().newContact(contact);
                            break;
                        case "GETCONTA"://CT
                            System.out.println("Kontaktanfrage empfangen!");
                            TCPClient client = new TCPClient(socket.getInetAddress().getHostAddress(), TCPServer.PORT);
                            client.sendContact(Contact.getMyContact());
                            break;
                        case "GETMYMM:"://GETMessagesWithID
                            System.out.println("Nachrichtenanfrage empfangen, sende alle Nachrichten...");
                            ArrayList<Message> messages = Database.getInstance().getMessagesWithId(json);
                            TCPClient tcpClient = new TCPClient(socket.getInetAddress().getHostAddress(), TCPServer.PORT);
                            for (Message m : messages) {
                                //tcpClient.sendMessage(m);
                            }
                            break;
                    }

                    //out.writeBytes(line + "\n\r");
                    out.writeBytes("200 OK" + "\n\r");
                    out.flush();
                }

            } catch (SocketException e) {
                return;

            } catch (IOException e) {
                e.printStackTrace();
                Thread.currentThread().stop();
                return;
            }
        }
    }

}
