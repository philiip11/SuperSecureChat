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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.function.Consumer;

public class TCPServerThread extends Thread {
    private Socket socket;

    TCPServerThread(Socket clientSocket) {
        this.socket = clientSocket;
    }

    public void run() {
        InputStream inp;
        BufferedReader brinp;
        try {
            inp = socket.getInputStream();
            brinp = new BufferedReader(new InputStreamReader(inp));
        } catch (IOException e) {
            return;
        }
        Crypto crypto = new Crypto();
        String line;
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
                parseInput(crypto, command, json, ip, mToMe, mFromMe, notMe, me);

                //out.writeBytes(line + "\n\r");
                sendText("200 OK");
            }


        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings({"unchecked", "DuplicateBranchesInSwitch"})
    private void parseInput(Crypto crypto, String command, String json, String ip, Message mToMe, Message mFromMe, NetworkContact notMe, NetworkContact me) {
        boolean relay = false;
        switch (command) {
            case "MESSAGR:":
                relay = true;
            case "MESSAGE:":
                Message message = Message.fromJSON(json);
                message.setTrace(message.getTrace() + "Recieved by " + Contact.getMyContact().getId() + " at " + Instant.now().getEpochSecond() + "; ");
                if (message.getReceiver().getId().equals(Contact.getMyContact().getId())) {
                    if (message.getReceived() == 0) {
                        message.setReceived(Instant.now().getEpochSecond());
                    }
                }
                ClassConnector.getInstance().sendMessageToAllChatControllers(message, relay);
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
                NetworkIconMessage networkIconMessage = ClassConnector.getInstance().sendIconMessageToNetworkMap(new Image(getClass().getResourceAsStream("/icons/round_vpn_key_white_24dp.png")), mToMe);
                System.out.println("Schlüsselaustausch...");
                crypto.generateKeys();
                crypto.receivePublicKey(Base64.getDecoder().decode(json));
                sendText("KEYPUBL:" + Base64.getEncoder().encodeToString(crypto.getPublicKey().getEncoded()));
                crypto.generateCommonSecretKey();
                Database.getInstance().addSecretKey(ContactList.getInstance().getContactByIP(ip), crypto.getSecretKey());
                crypto.getSecretKey();
                networkIconMessage.addResponse(new NetworkIconMessage(new Image(getClass().getResourceAsStream("/icons/round_vpn_key_white_24dp.png")), me, notMe));
                break;
            case "GETMYMM:"://GETMessagesWithID
                System.out.println("Nachrichtenanfrage empfangen, sende alle Nachrichten...");
                Network.getInstance().clearCache();
                //ArrayList<Message> messages = Database.getInstance().getMessagesWithId(json);
                ArrayList<Message> messages = Database.getInstance().getMessagesWithIdNotInTrace(json);
                JSONArray jsonMessages = new JSONArray();
                JSONArray jsonContacts = new JSONArray();


                for (Contact c : Database.getInstance().getContacts()) {
                    if (!c.getId().equals(Contact.getMyContact().getId())) {
                        jsonContacts.add(c);
                        /*TCPClient tcpClient = new TCPClient(socket.getInetAddress().getHostAddress(), TCPServer.PORT);
                        tcpClient.relayContact(c);
                        tcpClient.close();
                        Thread.sleep(100);
                        ClassConnector.getInstance().sendContactToNetworkMap(c, mFromMe);*/
                    }
                } // TODO JSONBLOB = All in one Message
                /*TCPClient tcpClient = new TCPClient(socket.getInetAddress().getHostAddress(), TCPServer.PORT);
                    tcpClient.sendMessage(mmmm, true);
                    tcpClient.close();
                    Thread.sleep(50);
                    ClassConnector.getInstance().sendMessageToNetworkMap(mmmm, mFromMe);*/
                jsonMessages.addAll(messages);
                JSONObject jsonBlob = new JSONObject();
                jsonBlob.put("MESSAGR:", jsonMessages);
                jsonBlob.put("CONTACR:", jsonContacts);
                TCPClient tcpClient = new TCPClient(socket.getInetAddress().getHostAddress(), TCPServer.PORT);
                tcpClient.sendJSONBlob(jsonBlob);
                tcpClient.close();
                ClassConnector.getInstance().sendIconMessageToNetworkMap(new Image(getClass().getResourceAsStream("/icons/round_all_inbox_white_48dp.png")), mFromMe);


                break;
            case "JSNBLOB:":
                try {
                    JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);
                    for (Object o : jsonObject.keySet()) {
                        String commando = (String) o;
                        JSONArray jsonArrayo = (JSONArray) jsonObject.get(commando);
                        jsonArrayo.forEach((Consumer<String>) s -> parseInput(crypto, commando, s, ip, mToMe, mFromMe, notMe, me));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                break;
            case "VERSION:":
                System.out.println("Version " + json + " empfangen!");
                if (!json.equals(Main.VERSION)) {
                    System.out.println("Neue Version, starte Update...");
                    new Thread(() -> Platform.runLater(() -> Main.startMain(new Stage()))).start();
                }
                break;
            default:
                System.out.println("Unkown Message recieved: " + command);
                System.out.println(json);
        }
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
