package Client;

import Server.AuthService;
import Server.Server;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class RegisterController {
    @FXML
    TextField loginField;

    @FXML
    TextField nickField;

    @FXML
    PasswordField passwordField;


    @FXML
    TextArea result;

    public void registerMe() {
        try {
            AuthService.connect();
//            Проверка на валидность логина и никнейма
            String login = loginField.getText();
            String nick = nickField.getText();
            String password = passwordField.getText();
            AuthService.addNewUser(login, password, nick);
            loginField.clear();
            nickField.clear();
            passwordField.clear();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            AuthService.disconnect();
        }
    }
}
