package Client;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Register extends Stage {

    public Register() {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("register.fxml"));
            setTitle("Регистрация");
            Scene scene = new Scene(root, 500, 150);
            setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
