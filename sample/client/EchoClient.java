package sample.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.client.controllers.Controller;
import sample.client.models.Network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class EchoClient extends Application {

    private final String SERVER_HOST = "localhost";
    private final int SERVER_PORT = 8888;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(EchoClient.class.getResource("views/sample.fxml"));
        Parent root = loader.load();
        /*Parent root = FXMLLoader.load(getClass().getResource("views/sample.fxml"));*/
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();


        Network network = new Network();
        network.connect();

        Controller controller = loader.getController();
        controller.setNetwork(network);

        network.waitMessage(controller);
    }




    public static void main(String[] args) {
        launch(args);
    }
}
