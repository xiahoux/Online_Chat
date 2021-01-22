package sample.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import sample.client.models.Network;

import java.io.IOException;

public class Controller {

    @FXML
    private TextField textField;

    @FXML
    private Button button;

    @FXML
    private ListView<String> usersList;

    @FXML
    private ListView<String> chatList;

    private Network network;

    public void setNetwork(Network network){
        this.network = network;
    }


    @FXML
    private void sendMessage() {
        String message = textField.getText();
        if (!message.isBlank()) {
            chatList.getItems().add(message);
        }
        textField.clear();

        try{
            network.getOut().writeUTF(message);
        }catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при отправке сообщения");
        }

    }

    public void appendMessage(String message){
        chatList.getItems().add(message);
    }

}
