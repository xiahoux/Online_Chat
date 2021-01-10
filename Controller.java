package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class Controller {

    @FXML
    private TextField textField;

    @FXML
    private Button button;

    @FXML
    private ListView<String> usersList;

    @FXML
    private ListView<String> chatList;

    @FXML
    void sendMessage() {
        String message = textField.getText();
        if(!message.isBlank()) {
            chatList.getItems().add(message);
        }
        textField.clear();
    }

}