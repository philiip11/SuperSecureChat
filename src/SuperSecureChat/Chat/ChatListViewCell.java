package SuperSecureChat.Chat;


import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Controller.ShowPictureController;
import SuperSecureChat.Crypto.Crypto;
import SuperSecureChat.Database;
import SuperSecureChat.Message;
import SuperSecureChat.Network.Network;
import com.jfoenix.controls.JFXBadge;
import com.jfoenix.controls.JFXListCell;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.marnic.jiconextract.extractor.IconSize;
import me.marnic.jiconextract.extractor.JIconExtractor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class ChatListViewCell extends JFXListCell<Message> {

    private static final int MAX_EMOJI_LENGTH = 8;
    @FXML
    Label labelTime;
    @FXML
    Label received;
    @FXML
    ImageView imageView;
    @FXML
    AnchorPane anchorPane;
    @FXML
    JFXBadge badge;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    Database db = Database.getInstance();
    private FXMLLoader mLLoader;
    @FXML
    TextFlow labelMessage;

    protected static String htmlifyHelper(String text) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            int ch = text.codePointAt(i);

            if (ch <= 128) {
                sb.appendCodePoint(ch);
            } else if (ch >= 159 && (ch < 55296 || ch > 57343)) {
                sb.append("&#x").append(Integer.toHexString(ch)).append(";");

            }

        }

        return sb.toString();
    }

    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);
        if (empty || message == null) {

            setText(null);
            setGraphic(null);

        } else {
            Crypto crypto = new Crypto();
            byte[] secretKey;
            String fxmlResource;
            boolean messageFromMe;
            boolean dataMessage = false;
            if (message.getSender().getId().equals(Contact.getMyContact().getId())) {
                if (message.getData() == null || message.getData().equals("")) {
                    fxmlResource = "/fxml/chatCellMe.fxml";
                } else {
                    fxmlResource = "/fxml/chatCellPictureMe.fxml";
                    dataMessage = true;
                }
                secretKey = Database.getInstance().getSecretKeyByContact(message.getReceiver());
                messageFromMe = true;
            } else {
                if (message.getData() == null || message.getData().equals("")) {
                    fxmlResource = "/fxml/chatCell.fxml";
                } else {
                    fxmlResource = "/fxml/chatCellPicture.fxml";
                    dataMessage = true;
                }
                secretKey = Database.getInstance().getSecretKeyByContact(message.getSender());
                messageFromMe = false;
            }
            crypto.setSecretKey(secretKey);
            mLLoader = new FXMLLoader(getClass().getResource(fxmlResource));
            mLLoader.setController(this);

            try {
                mLLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            labelMessage.getChildren().addAll(parseText(crypto.decrypt(message.getText())));
            labelTime.setText(simpleDateFormat.format(new Date(message.getCreated() * 1000L)));
            if (messageFromMe) {
                received.setVisible(message.getReceived() > 0);
                received.getStyleClass().clear();
                if (message.getRead() != 0) {
                    received.getStyleClass().add("read");
                }
            } else {
                markRead(message);
            }
            if (dataMessage) {
                String filename = crypto.decrypt(message.getText());
                String filenameLower = filename.toLowerCase();
                if (filenameLower.contains(".png") ||
                        filenameLower.contains(".jpg") ||
                        filenameLower.contains(".jfif")) {
                    showImage(message, crypto, filename);
                } else {
                    showFile(message, crypto, filename);
                }

            }

            //contactImage.setImage(contact.getJavaFXImage());
            //int notifications = db.countUnreadMessagesByContact(contact);
            //badge.setEnabled(notifications > 0);
            //badge.setText(String.valueOf(notifications));


            setText(null);
            setGraphic(anchorPane);
        }

    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void showFile(Message message, Crypto crypto, String filename) {
        if (getScene() != null) {
            new Thread(() -> {
                String home = System.getProperty("user.home");
                new File(home + "/Downloads/SuperSecureChat/").mkdir();
                File file = new File(home + "/Downloads/SuperSecureChat/" + filename);
                if (!file.exists()) {
                    try {
                        Files.write(file.toPath(), Base64.getDecoder().decode(crypto.decrypt(message.getData())));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (!message.getData().equals("DELDATA")) {
                    Network.getInstance().sendMessage(new ReferenceMessage(message, "DELDATA:THIS"));
                    message.setData("DELDATA");
                    Database.getInstance().updateMessage(message);
                    Database.getInstance().vacuum();
                }
                BufferedImage thumbnail = JIconExtractor.getJIconExtractor().extractIconFromFile(file, IconSize.JUMBO);
                Platform.runLater(() -> imageView.setImage(SwingFXUtils.toFXImage(thumbnail, null)));
                imageView.setOnMouseClicked(event -> openFile(filename));
            }).start();
        }
    }

    private void openFile(String filename) {
        String home = System.getProperty("user.home");
        File file = new File(home + "/Downloads/SuperSecureChat/" + filename);
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showImage(Message message, Crypto crypto, String filename) {
        Image image;
        String home = System.getProperty("user.home");
        if (!message.getData().equals("DELDATA")) {
            File file = new File(home + "/Downloads/SuperSecureChat/" + filename);
            if (!file.exists()) {
                if (!file.exists()) {
                    try {
                        Files.write(file.toPath(), Base64.getDecoder().decode(crypto.decrypt(message.getData())));
                        Network.getInstance().sendMessage(new ReferenceMessage(message, "DELDATA:THIS"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        File file = new File(home + "/Downloads/SuperSecureChat/" + filename);
        try {
            image = new Image(new FileInputStream(file));
            //Image image = Contact.imageDecoder(message.getData());
            imageView.setImage(image);
            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    try {
                        double imageWidth = image.getWidth();
                        double imageHeight = image.getHeight();
                        double screenHeight = Screen.getPrimary().getBounds().getHeight();
                        double screenWidth = Screen.getPrimary().getBounds().getWidth();
                        double width = Math.min(imageWidth, screenWidth);
                        double height = Math.min(imageHeight, screenHeight);
                        if (width < imageWidth || height < imageHeight) {
                            double ratio = Math.min(width / imageWidth, height / imageHeight);
                            width = imageWidth * ratio;
                            height = imageHeight * ratio;
                        }
                        double x = (screenWidth - width) / 2;
                        double y = (screenHeight - height) / 2;

                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/showPicture.fxml"));
                        Parent root = loader.load();
                        ShowPictureController showPictureController = loader.getController();
                        Stage stage = new Stage();
                        stage.setX(x);
                        stage.setY(y);
                        stage.initStyle(StageStyle.TRANSPARENT);
                        stage.setTitle(filename);
                        stage.getIcons().add(image);
                        Scene scene = new Scene(root, width, height, true, SceneAntialiasing.BALANCED);
                        scene.setFill(Color.TRANSPARENT);
                        stage.setScene(scene);
                        showPictureController.showPicture(image, width, height);
                        showPictureController.setStage(stage);
                        stage.show();
                        event.consume();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    private void markRead(Message message) {
        if (message.getRead() == 0) {
            if (getScene() != null) {
                message.setRead(Instant.now().getEpochSecond());
                new Thread(() -> {
                    Database.getInstance().markRead(message);
                    Network.getInstance().relayMessage(message, null);
                }).start();
            }
        }
    }

    private ArrayList<Node> parseText(String string) {
        ArrayList<Node> textList = new ArrayList<>();
        /*String htmlifiedText = EmojiUtils.hexHtmlify(string);
        // regex to identify html entitities in htmlified text
        Matcher matcher = htmlEntityPattern.matcher(htmlifiedText);
        int start = 0;
        int end = 0;
        while (matcher.find()) {
            String emojiCode = matcher.group();
            if (isEmoji(emojiCode)) {
                end = matcher.start(0);
                addText(textList, htmlifiedText, start, end);
                start = matcher.end(0);
                addEmoji(textList, emojiCode);
                //emojis.add(EmojiUtils.getEmoji(emojiCode).getEmoji());
            }
        }*/
        int size = 128;
        for (int i = 0; i < string.length(); i++) {
            int ch = string.codePointAt(i);
            if (ch <= 128) {
                size = 24;
                break;
            }
        }
        boolean success = true;
        int charCounter = 0;
        int i = -1;

        // !sucess  i       while
        //  true    false   true
        //  true    true    true
        //  false   false   false
        //  false   true    true
        StringBuilder charBuffer = new StringBuilder();
        StringBuilder lastEmojis = new StringBuilder();
        /*if(string.equals("\uD83C\uDDE6\uD83C\uDDFA")){
            System.out.println("Australia");
        }*/
        while (!success || i < string.length()) {
            i++;
            if (i >= string.length()) {
                i -= charCounter;
                if (i < string.length()) {
                    int ch = string.codePointAt(i);
                    addText(textList, ch);
                    charBuffer = new StringBuilder();
                    charCounter = 0;
                    success = true;
                    i++;
                    if (i == string.length()) {
                        break;
                    }
                } else {
                    break;
                }
            }
            int ch = string.codePointAt(i);
            if (success && ch <= 256 && ch != 169 && ch != 174) {           // Copyright and Registered
                addText(textList, ch);
                lastEmojis = new StringBuilder();
            } else if (ch < 55296 || ch > 57343) {
                success = addEmoji(textList, size, charBuffer + Integer.toHexString(ch));
                if (!success) {
                    charCounter++;
                    charBuffer.append(Integer.toHexString(ch)).append("_");
                    if (charCounter > MAX_EMOJI_LENGTH) {
                        i -= charCounter;
                        i++;
                        ch = string.codePointAt(i);
                        addText(textList, ch);
                        charCounter = 0;
                        charBuffer = new StringBuilder();
                    }
                } else {
                    charCounter = 0;
                    charBuffer = new StringBuilder();
                }
                if (lastEmojis.length() > 0) {
                    boolean success2 = addEmoji(textList, size, lastEmojis + Integer.toHexString(ch));
                    if (success2) {
                        removePenultimateEmoji(textList);
                        success = true;
                        charCounter = 0;
                        charBuffer = new StringBuilder();
                    }
                }
                lastEmojis.append(Integer.toHexString(ch)).append("_");

            }

        }
        /*
        String patternStart = "&#;";

        String emojiCode = matcher.group();
        if (isEmoji(emojiCode)) {
            end = matcher.start(0);
            addText(textList, htmlifiedText, start, end);
            start = matcher.end(0);
            addEmoji(textList, emojiCode);
            //emojis.add(EmojiUtils.getEmoji(emojiCode).getEmoji());
        }
        //addText(textList, htmlifiedText, start);*/
        return textList;
    }

    private void removePenultimateEmoji(ArrayList<Node> textList) {
        textList.remove(textList.size() - 2);

    }

    private boolean addEmoji(List<Node> textList, int size, String string) {
        String emojiPath = "/emoji/128/emoji_u" + string + ".png";
        try {
            ImageView emoji = new ImageView(getClass().getResource(emojiPath).toExternalForm());
            emoji.setFitHeight(size);
            emoji.setPreserveRatio(true);
            emoji.setSmooth(true);
            textList.add(emoji);
            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }


    private void addText(List<Node> textList, String htmlifiedText, int start, int end) {

        Text text = new Text(htmlifiedText.substring(start, end - start));
        addText(textList, text);
    }

    private void addText(List<Node> textList, String htmlifiedText, int start) {
        Text text = new Text(htmlifiedText.substring(start));
        addText(textList, text);
    }

    private void addText(List<Node> textList, Text text) {
        text.setFont(Font.font("Roboto", 14));
        text.setFill(Paint.valueOf("white"));
        textList.add(text);
    }

    private void addText(List<Node> textList, String string) {
        Text text = new Text(string);
        text.setFont(Font.font("Roboto", 14));
        text.setFill(Paint.valueOf("white"));
        textList.add(text);
    }

    private void addText(List<Node> textList, int codePoint) {
        String string = String.valueOf((char) codePoint);
        Text text = new Text(string);
        text.setFont(Font.font("Roboto", 14));
        text.setFill(Paint.valueOf("white"));
        textList.add(text);
    }

}
