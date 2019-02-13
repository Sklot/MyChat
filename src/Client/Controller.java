package Client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.awt.event.ActionEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class Controller {

    @FXML
    TextArea textAreaField;

    @FXML
    TextField textField;

    @FXML
    HBox bottomPanel;

    @FXML
    HBox upperPanel;

    @FXML
    TextField loginField;

    @FXML
    PasswordField passwordField;

    @FXML
    ListView<String> clientlist;

    private boolean isAuthorized;


    final String IP_ADRES = "localhost";
    final int PORT = 8585;

    public void setAuthorized(boolean isAuthorized) {
        this.isAuthorized = isAuthorized;
        if (!isAuthorized) {
//            Проверка на авторизацию. Смена видимости панелей для неавторизванных клиентов.
            upperPanel.setVisible(true);
            upperPanel.setManaged(true);
            bottomPanel.setVisible(false);
            bottomPanel.setManaged(false);
            clientlist.setVisible(false);
            clientlist.setManaged(false);
        } else {
            upperPanel.setVisible(false);
            upperPanel.setManaged(false);
            bottomPanel.setVisible(true);
            bottomPanel.setManaged(true);
            clientlist.setVisible(true);
            clientlist.setManaged(true);
        }
    }

    Socket socket;
    DataInputStream in;
    DataOutputStream out;


    public void connect() {
        try {
            socket = new Socket(IP_ADRES, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            setAuthorized(false);

            Thread t1 = new Thread(() -> {
                try {
                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/authOK")) {
                            setAuthorized(true);
                            break;
                        } else {
                            textAreaField.appendText(str + "\n");
                        }
                    }

                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/")) {
                            if (str.equals("/serverclosed")) break;

                            if (str.startsWith("/clientlist")) {
                                String[] data = str.split(" ");
                                Platform.runLater(() -> {
                                    clientlist.getItems().clear();
                                    for (int i = 1; i < data.length; i++) {
                                        clientlist.getItems().add(data[i]);
                                    }
                                });
                            }
                        } else
                            textAreaField.appendText(str + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setAuthorized(false);
                }
            });
            t1.setDaemon(true);
            t1.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendMsg() {

        try {
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tryToRegister() {
       Register reg = new Register();
       reg.show();
        textAreaField.appendText("Вы успешно создали учётную запись." + "\n");
        reg.close();
    }

    public void tryToAuth() {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        try {
            out.writeUTF("/auth " + loginField.getText() + " " + passwordField.getText());
            loginField.clear();
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


