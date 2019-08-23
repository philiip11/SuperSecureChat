package SuperSecureChat.Crypto;

import java.util.Scanner;

public class test {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        Encryption en = new Encryption();

        System.out.println("Enter your Message: ");
        Scanner scanner = new Scanner(System.in);
        String text = scanner.nextLine();
        System.out.println("Your Message is: " + text);
        String encryptedWord = en.encrypt(text);
        System.out.println("Encrypted Message is: " + encryptedWord);
        Decryption de = new Decryption();
        System.out.println("Decrypted Message is: " + de.decrypt(encryptedWord));
    }
}