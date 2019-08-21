package SuperSecureChat.Network;

import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Message;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

public class TCPClient {
    private Socket socket;
    private Scanner scanner;

    private TCPClient(String serverAddress, int serverPort) throws Exception {
        this.socket = new Socket(serverAddress, serverPort);
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) throws Exception {
        TCPClient client = new TCPClient("172.17.27.237", TCPServer.PORT);

        System.out.println("\r\nConnected to Server: " + client.socket.getInetAddress());
        new Thread(client::start).start();
        client.sendMessage(new Message("1234", "", new Contact("1234", "Philip", "Schneider", "169.254.162.72", new Date(), null, 0),
                new Contact("1234", "Philip", "Schneider", "169.254.162.72", new Date(), null, 0),
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

        sendText(message.toJSONString());
    }
}
