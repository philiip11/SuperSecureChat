package SuperSecureChat.Network;

import SuperSecureChat.ClassConnector;
import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Contacts.ContactList;
import SuperSecureChat.Controller.NetworkController;
import SuperSecureChat.Crypto.Crypto;
import SuperSecureChat.Database;
import SuperSecureChat.Main;
import SuperSecureChat.Message;
import SuperSecureChat.NetworkMap.NetworkContact;
import SuperSecureChat.NetworkMap.NetworkContactMessage;
import SuperSecureChat.NetworkMap.NetworkIconMessage;
import SuperSecureChat.NetworkMap.NetworkMessage;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

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
                    String url = socket.getRemoteSocketAddress().toString();
                    String ip = url.substring(1).split(":")[0];
                    NetworkController networkController = ClassConnector.getInstance().getNetworkController();
                    Message mToMe = new Message();
                    mToMe.setSender(ContactList.getInstance().getContactByIP(ip));
                    mToMe.setReceiver(Contact.getMyContact());
                    Message mFromMe = new Message();
                    mFromMe.setSender(ContactList.getInstance().getContactByIP(ip));
                    mFromMe.setReceiver(Contact.getMyContact());
                    NetworkContact notMe = networkController.getNetworkContactByContact(mFromMe.getSender());
                    NetworkContact me = networkController.getNetworkContactByContact(Contact.getMyContact());
                    boolean relay = false;
                    switch (command) {
                        case "MESSAGE:":
                            Message message = Message.fromJSON(json);
                            message.setTrace(message.getTrace() + "Recieved by " + Contact.getMyContact().getId() + " at " + Instant.now().getEpochSecond() + "; ");
                            if (message.getReceiver().getId().equals(Contact.getMyContact().getId())) {
                                if (message.getReceived() == 0) {
                                    message.setReceived(Instant.now().getEpochSecond());
                                }
                            }
                            ClassConnector.getInstance().sendMessageToAllChatControllers(message, !loop);
                            NetworkMessage networkMessage = ClassConnector.getInstance().sendMessageToNetworkMap(message, mToMe);
                            Database.getInstance().newMessage(message);
                            Network.getInstance().relayMessage(message, networkMessage);
                            break;
                        case "CONTACR:":
                            relay = true;
                        case "CONTACT:":
                            Contact contact = Contact.fromJSON(json);
                            NetworkContactMessage networkContactMessage = ClassConnector.getInstance().sendContactToNetworkMap(contact, mToMe);
                            if (!contact.getId().equals(Contact.getMyContact().getId())) {
                                System.out.println("Kontakt empfangen!");
                                System.out.println(contact.getId());
                                if (!relay) {
                                    contact.setUrl(ip);
                                }
                                ContactList.getInstance().addContact(contact);
                                Database.getInstance().newContact(contact);
                                Network.getInstance().relayContact(contact, networkContactMessage);
                            }
                            break;
                        case "GETCONTA"://CT
                            System.out.println("Kontaktanfrage empfangen!");
                            TCPClient client = new TCPClient(socket.getInetAddress().getHostAddress(), TCPServer.PORT);
                            client.sendContact(Contact.getMyContact());
                            ClassConnector.getInstance().sendContactToNetworkMap(Contact.getMyContact(), mFromMe);
                            break;
                        case "KEYEXCH:"://CT
                            NetworkIconMessage networkIconMessage = ClassConnector.getInstance().sendIconMessageToNetworkMap(new Image(getClass().getResourceAsStream("/icons/baseline_vpn_key_white_24dp.png")), mToMe);
                            System.out.println("Schlüsselaustausch...");
                            crypto.generateKeys();
                            crypto.receivePublicKey(Base64.getDecoder().decode(json));
                            sendText("KEYPUBL:" + Base64.getEncoder().encodeToString(crypto.getPublicKey().getEncoded()));
                            crypto.generateCommonSecretKey();
                            Database.getInstance().addSecretKey(ContactList.getInstance().getContactByIP(ip), crypto.getSecretKey());
                            crypto.getSecretKey();
                            networkIconMessage.addResponse(new NetworkIconMessage(new Image(getClass().getResourceAsStream("/icons/baseline_vpn_key_white_24dp.png")), me, notMe));
                            break;
                        case "GETMYMM:"://GETMessagesWithID
                            System.out.println("Nachrichtenanfrage empfangen, sende alle Nachrichten...");
                            //ArrayList<Message> messages = Database.getInstance().getMessagesWithId(json);
                            ArrayList<Message> messages = Database.getInstance().getMessagesWithIdNotInTrace(json);
                            for (Contact c : Database.getInstance().getContacts()) {
                                if (!c.getId().equals(Contact.getMyContact().getId())) {
                                    TCPClient tcpClient = new TCPClient(socket.getInetAddress().getHostAddress(), TCPServer.PORT);
                                    tcpClient.relayContact(c);
                                    tcpClient.close();
                                    Thread.sleep(100);
                                    ClassConnector.getInstance().sendContactToNetworkMap(c, mFromMe);
                                }
                            }
                            for (Message mmmm : messages) {
                                TCPClient tcpClient = new TCPClient(socket.getInetAddress().getHostAddress(), TCPServer.PORT);
                                tcpClient.sendMessage(mmmm);
                                tcpClient.close();
                                Thread.sleep(100);
                                ClassConnector.getInstance().sendMessageToNetworkMap(mmmm, mFromMe);
                            }
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
                            if (!json.equals(Main.VERSION)) {
                                System.out.println("Neue Version, starte Update...");
                                new Thread(() -> {
                                    Platform.runLater(() -> {
                                        Main.startMain(new Stage());
                                    });
                                }).start();
                            }
                            break;
                        default:
                            System.out.println("Unkown Message recieved: " + command);
                            System.out.println(json);
                    }

                    //out.writeBytes(line + "\n\r");
                    sendText("200 OK");
                }

            } catch (SocketException e) {
                return;

            } catch (IOException e) {
                e.printStackTrace();
                return;
            } catch (InterruptedException e) {
                e.printStackTrace();
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
