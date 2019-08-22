package SuperSecureChat.Network;

import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Message;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Instant;
import java.util.Date;
import java.util.Scanner;

public class TCPClient {
    private Socket socket;
    private Scanner scanner;

    public TCPClient(String serverAddress, int serverPort) {
        try {
            this.socket = new Socket(serverAddress, serverPort);
            this.scanner = new Scanner(System.in);
        } catch (IOException ignored) {

        }
    }

    public static void main(String[] args) throws Exception {
        TCPClient client = new TCPClient("172.17.41.214", TCPServer.PORT);

        System.out.println("\r\nConnected to Server: " + client.socket.getInetAddress());
        new Thread(client::start).start();
        client.sendMessage(new Message("1234", "", new Contact("1234", "Philip", "Schneider", "169.254.162.72", Instant.now().getEpochSecond(), null, 0),
                new Contact("1234", "Philip", "Schneider", "169.254.162.72", Instant.now().getEpochSecond(), null, 0),
                "Hallo, dies ist eine Testnachricht! :-)", null, "", new Date(), 0, 0));
    }

    private void start() {
        String input;
        while (true) {
            input = scanner.nextLine();
            sendText(input);
        }
    }

    private void sendText(String input) {
        System.out.println(input);
        try {
            PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
            out.println(input);
            out.flush();
        } catch (IOException ignored) {

        }
    }

    public void sendMessage(Message message) {

        sendText("MESSAGE:" + message.toJSONString());
    }

    public void sendContact(Contact contact) {

        sendText("CONTACT:" + contact.toJSONString());
    }
}
