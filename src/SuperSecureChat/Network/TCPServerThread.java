package SuperSecureChat.Network;

import SuperSecureChat.ClassConnector;
import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Contacts.ContactList;
import SuperSecureChat.Controller.MainController;
import SuperSecureChat.Crypto.Crypto;
import SuperSecureChat.Database;
import SuperSecureChat.Main;
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
        Crypto crypto = new Crypto();
        String line;
        boolean loop = true;
        boolean firstrun = true;
        while (loop) {
            if (firstrun) {
                loop = false;
                firstrun = false;
            }
            try {
                line = brinp.readLine();
                if ((line != null) && line.length() > 8) {
                    String command = line.substring(0, 8);
                    String json = line.substring(8);
                    boolean relay = false;
                    switch (command) {
                        case "MESSAGE:":
                            Message message = Message.fromJSON(json);
                            message.setReceived(Instant.now().getEpochSecond());
                            message.setTrace(message.getTrace() + "Recieved by " + Contact.getMyContact().getId() + " at " + Instant.now().getEpochSecond() + "; ");
                            if (message.getReceiver().getId().equals(Contact.getMyContact().getId())) {
                                if (message.getReceived() == 0) {
                                    message.setReceived(Instant.now().getEpochSecond());
                                }
                            }
                            ClassConnector.getInstance().sendMessageToAllChatControllers(message, !loop);
                            Database.getInstance().newMessage(message);
                            Network.getInstance().relayMessage(message);
                            break;
                        case "CONTACR:":
                            relay = true;
                        case "CONTACT:":
                            Contact contact = Contact.fromJSON(json);
                            if (!contact.getId().equals(Contact.getMyContact().getId())) {
                                System.out.println("Kontakt empfangen!");
                                System.out.println(contact.getId());
                                if (!relay) {
                                    String url = socket.getRemoteSocketAddress().toString();
                                    String ip = url.substring(1).split(":")[0];
                                    contact.setUrl(ip);
                                }
                                ContactList.getInstance().addContact(contact);
                                Database.getInstance().newContact(contact);
                                Network.getInstance().relayContact(contact);
                            }
                            break;
                        case "GETCONTA"://CT
                            System.out.println("Kontaktanfrage empfangen!");
                            TCPClient client = new TCPClient(socket.getInetAddress().getHostAddress(), TCPServer.PORT);
                            client.sendContact(Contact.getMyContact());
                            break;
                        case "KEYEXCH:"://CT
                            System.out.println("Schlüsselaustausch...");
                            crypto.generateKeys();
                            crypto.receivePublicKey(Base64.getDecoder().decode(json));
                            sendText("KEYPUBL:" + Base64.getEncoder().encodeToString(crypto.getPublicKey().getEncoded()));
                            crypto.generateCommonSecretKey();
                            String urll = socket.getRemoteSocketAddress().toString();
                            String ipp = urll.substring(1).split(":")[0];
                            Database.getInstance().addSecretKey(ContactList.getInstance().getContactByIP(ipp), crypto.getSecretKey());
                            crypto.getSecretKey();
                            break;
                        case "GETMYMM:"://GETMessagesWithID
                            System.out.println("Nachrichtenanfrage empfangen, sende alle Nachrichten...");
                            ArrayList<Message> messages = Database.getInstance().getMessagesWithId(json);
                            TCPClient tcpClient = new TCPClient(socket.getInetAddress().getHostAddress(), TCPServer.PORT);
                            tcpClient.sendText("OPENTCPP");
                            tcpClient.sendText("TESTTEST");
                            for (Contact c : Database.getInstance().getContacts()) {
                                if (!c.getId().equals(Contact.getMyContact().getId())) {
                                    tcpClient.relayContact(c);
                                }
                            }
                            for (Message m : messages) {
                                if (m.getReceived() == 0) {
                                    tcpClient.sendMessage(m);
                                }
                            }
                            tcpClient.sendText("CLOSETCP");
                            tcpClient.close();
                            break;
                        case "OPENTCPP":
                            loop = true;
                            System.out.println("loop=true");
                            break;
                        case "CLOSETCP":
                            loop = false;
                            System.out.println("loop=false");
                            break;
                        case "VERSION:":
                            System.out.println("Version " + json + " empfangen!");
                            if (versionNumber(json) > versionNumber(Main.VERSION)) {
                                System.out.println("Neue Version, starte Update...");
                                new MainController().checkForUpdate();
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
        }
    }

    private int versionNumber(String version) {
        String number = version.replace("v", "").replace(".", "");
        return Integer.parseInt(number);
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
