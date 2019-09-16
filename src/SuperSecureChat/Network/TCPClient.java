package SuperSecureChat.Network;

import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Message;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient {
    private Socket socket;
    private Scanner scanner;

    TCPClient(String serverAddress, int serverPort) {
        try {
            this.socket = new Socket(serverAddress, serverPort);
            this.scanner = new Scanner(System.in);
        } catch (IOException ignored) {

        }
    }

    public static void main(String[] args) {
        TCPClient client = new TCPClient("172.17.41.214", TCPServer.PORT);

        System.out.println("\r\nConnected to Server: " + client.socket.getInetAddress());
        new Thread(client::start).start();
    }

    private void start() {
        String input;
        while (true) {
            input = scanner.nextLine();
            sendText(input);
            System.out.println(receiveText());
        }
    }

    void sendText(String input) {
        System.out.println("Sende: " + input.substring(0, Math.min(input.length() - 1, 64)) + "...");
        if (socket != null) {
            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(input);
                out.flush();
            } catch (IOException ignored) {

            }
        }
    }

    String receiveText() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = bufferedReader.readLine();
            if (line != null) {
                System.out.println("TCPClient: RecievedMessage:");
                System.out.println(line);
                return line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    void sendMessage(Message message) {
        sendMessage(message, false);
    }

    void sendMessage(Message message, boolean relay) {
        String json = message.toJSONString();
        String command = relay ? "MESSAGR:" : "MESSAGE:";
        if (json != null) {
            sendText("MESSAGE:" + json);
        }
    }

    void sendContact(Contact contact) {
        sendText("CONTACT:" + contact.toJSONString());
    }

    public void relayContact(Contact contact) {
        System.out.println("Relay contact " + contact.getId());
        sendText("CONTACR:" + contact.toJSONString());
    }

    public void close() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String exchangePublicKey(String publicKey) {
        sendText("KEYEXCH:" + publicKey);
        while (true) {
            String text = receiveText();
            if (text.length() > 8) {
                if (text.substring(0, 8).equals("KEYPUBL:")) {
                    return text.substring(8);
                }
            }
        }
    }

    void sendJSONBlob(JSONObject jsonBlob) {
        sendText("JSNBLOB:" + jsonBlob.toJSONString());
    }
}
