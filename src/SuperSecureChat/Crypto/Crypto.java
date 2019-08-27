package SuperSecureChat.Crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


public class Crypto {


    //~ --- [INSTANCE FIELDS] ------------------------------------------------------------------------------------------

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private PublicKey receivedPublicKey;
    private byte[] secretKey;
    private String secretMessage;


    //~ --- [METHODS] --------------------------------------------------------------------------------------------------

    public void encryptAndSendMessage(final String message, final Crypto crypto) {

        try {

            // You can use Blowfish or another symmetric algorithm but you must adjust the key size.
            final SecretKeySpec keySpec = new SecretKeySpec(secretKey, "DES");
            final Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            final byte[] encryptedMessage = cipher.doFinal(message.getBytes());

            crypto.receiveAndDecryptMessage(encryptedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String text) {
        try {

            // You can use Blowfish or another symmetric algorithm but you must adjust the key size.
            final SecretKeySpec keySpec = new SecretKeySpec(secretKey, "DES");
            final Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            final byte[] encryptedMessage = cipher.doFinal(text.getBytes());

            return Base64.getEncoder().encodeToString(encryptedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //~ ----------------------------------------------------------------------------------------------------------------

    public void generateCommonSecretKey() {

        try {
            final KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
            keyAgreement.init(privateKey);
            keyAgreement.doPhase(receivedPublicKey, true);

            secretKey = shortenSecretKey(keyAgreement.generateSecret());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //~ ----------------------------------------------------------------------------------------------------------------

    public void generateKeys() {

        try {
            final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
            keyPairGenerator.initialize(1024);

            final KeyPair keyPair = keyPairGenerator.generateKeyPair();

            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //~ ----------------------------------------------------------------------------------------------------------------

    public PublicKey getPublicKey() {

        return publicKey;
    }


    //~ ----------------------------------------------------------------------------------------------------------------

    public void receiveAndDecryptMessage(final byte[] message) {

        try {

            // You can use Blowfish or another symmetric algorithm but you must adjust the key size.
            final SecretKeySpec keySpec = new SecretKeySpec(secretKey, "DES");
            final Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            secretMessage = new String(cipher.doFinal(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String decrypt(String text) {
        try {

            // You can use Blowfish or another symmetric algorithm but you must adjust the key size.
            final SecretKeySpec keySpec = new SecretKeySpec(secretKey, "DES");
            final Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            return new String(cipher.doFinal(Base64.getDecoder().decode(text)));
        } catch (Exception e) {
            return text;
        }
    }

    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * In a real life example you must serialize the public key for transferring.
     *
     * @param crypto
     */
    public void receivePublicKeyFrom(final Crypto crypto) {

        receivedPublicKey = crypto.getPublicKey();
    }

    public void receivePublicKey(byte[] publicKeyBytes) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("DH");
            receivedPublicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

    }


    //~ ----------------------------------------------------------------------------------------------------------------

    public void whisperTheSecretMessage() {

        System.out.println(secretMessage);
    }


    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * 1024 bit symmetric key size is so big for DES so we must shorten the key size. You can get first 8 longKey of the
     * byte array or can use a key factory
     *
     * @param longKey
     * @return
     */
    private byte[] shortenSecretKey(final byte[] longKey) {

        try {

            // Use 8 bytes (64 bits) for DES, 6 bytes (48 bits) for Blowfish
            final byte[] shortenedKey = new byte[8];

            System.arraycopy(longKey, 0, shortenedKey, 0, shortenedKey.length);

            return shortenedKey;

            // Below lines can be more secure
            // final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            // final DESKeySpec       desSpec    = new DESKeySpec(longKey);
            //
            // return keyFactory.generateSecret(desSpec).getEncoded();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public byte[] getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(byte[] secretKey) {
        this.secretKey = secretKey;
    }

}
