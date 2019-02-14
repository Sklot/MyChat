package Client;

import Server.AuthService;
import Server.Server;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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

    @FXML
    Button register;

    @FXML
    Button ok;

    public void registerMe() throws SQLException {
        try {
            AuthService.connect();
            String login = loginField.getText();
            String nick = nickField.getText();
            String password = passwordField.getText();

            if (isNullOrEmmpty(login) || isNullOrEmmpty(nick) || isNullOrEmmpty(password)) {
                result.appendText("Заполните все поля!" + "\n");
            } else {
                if (!AuthService.isLoginFree(login)) {
                    result.appendText("Логин уже занят!" + "\n");
                }else if (!AuthService.isNickFree(nick)) {
                    result.appendText("Никнейм уже занят!" + "\n");
                } else {
                    AuthService.addNewUser(login, password, nick);
                    result.appendText("Вы успешно создали учётную запись." + "\n");
                    loginField.setVisible(false);
                    nickField.setVisible(false);
                    passwordField.setVisible(false);
                    register.setVisible(false);

                }
            }


            loginField.clear();
            nickField.clear();
            passwordField.clear();


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            AuthService.disconnect();
        }
    }

    private boolean isNullOrEmmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    public void closeIt() {
        Stage stage = (Stage) ok.getScene().getWindow();
        stage.close();
    }
}
