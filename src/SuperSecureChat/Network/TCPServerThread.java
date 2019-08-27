package SuperSecureChat.Network;

import SuperSecureChat.ClassConnector;
import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Contacts.ContactList;
import SuperSecureChat.Crypto.Crypto;
import SuperSecureChat.Database;
import SuperSecureChat.Message;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;

public class TCPServerThread extends Thread {
    private Socket socket;
    DataOutputStream out;

    TCPServerThread(Socket clientSocket) {
        this.socket = clientSocket;
    }

    public void run() {
        InputStream inp;
        BufferedReader brinp;
        try {
            inp = socket.getInputStream();
            brinp = new BufferedReader(new InputStreamReader(inp));
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            return;
        }
        String line;
        //while (true) {
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
                    case "KEYEXCH:"://CT
                        System.out.println("Schlüsselaustausch...");
                        Crypto crypto = new Crypto();
                        crypto.generateKeys();
                        crypto.receivePublicKey(Base64.getDecoder().decode(json));
                        sendText("KEYPUBL:" + Base64.getEncoder().encodeToString(crypto.getPublicKey().getEncoded()));
                        crypto.generateCommonSecretKey();
                        crypto.getSecretKey(); //TODO Do something with the Secret Key
                        break;
                    case "GETMYMM:"://GETMessagesWithID
                        System.out.println("Nachrichtenanfrage empfangen, sende alle Nachrichten...");
                        ArrayList<Message> messages = Database.getInstance().getMessagesWithId(json);
                        for (Message m : messages) { //TODO only one Thread
                            TCPClient tcpClient = new TCPClient(socket.getInetAddress().getHostAddress(), TCPServer.PORT);
                            tcpClient.sendMessage(m);
                            tcpClient.close();
                        }
                        break;
                }

                //out.writeBytes(line + "\n\r");
                sendText("200 OK");
            }

        } catch (SocketException e) {
            return;

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        //}
    }

    private void sendText(String text) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(text);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
