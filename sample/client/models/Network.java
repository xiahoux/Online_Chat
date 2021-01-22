package sample.client.models;

import sample.client.controllers.Controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Network {

    private static final int DEFAULT_SERVER_SOCKET = 8888;
    private static final String DEFAULT_SERVER_HOST = "localhost";

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private final int PORT;
    private final String HOST;

    public Network(int PORT, String HOST) {
        this.PORT = PORT;
        this.HOST = HOST;
    }

    public Network() {
        this.PORT = DEFAULT_SERVER_SOCKET;
        this.HOST = DEFAULT_SERVER_HOST;
    }

    public void connect(){
        try {
            socket = new Socket(HOST, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        }catch (IOException e) {
            System.out.println("Соединение не установлено");
            e.printStackTrace();
        }
    }

    public DataOutputStream getOut(){
        return out;
    }

    public DataInputStream getIn(){
        return in;
    }

    public void waitMessage(Controller controller) {
        Thread thread = new Thread(() -> {
            try {
                while (true){
                    String message = in.readUTF();
                    controller.appendMessage("СЕРВЕРНОЕ СООБЩЕНИЕ: " + message);
                }
            }catch (Exception e){
                System.out.println("Ошибка подключения");
            }
        });

        thread.setDaemon(true);
        thread.start();
    }




}




















/*
<?import javafx.geometry.Insets?>
<?import javafx.scene.layout.GridPane?>

<?import javafx.scene.control.Button?>
<?import javafx.scene.control.Label?>
<GridPane fx:controller="sample.client.controllers.Controller"
        xmlns:fx="http://javafx.com/fxml" alignment="center" hgap="10" vgap="10">
</GridPane>*/
