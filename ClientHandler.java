package sample.server.chat.handler;

import sample.server.chat.MyServer;
import sample.server.chat.auth.AuthService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private final MyServer myServer;
    private final Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;

    private static final String AUTH_CMD_PREFIX = "/auth"; // + login + pass
    private static final String AUTHOK_CMD_PREFIX = "/authok"; // + username
    private static final String AUTHERR_CMD_PREFIX = "/autherr"; // + error message
    private static final String CLIENT_MSG_CMD_PREFIX = "/clientMsg"; // + msg
    private static final String SERVER_MSG_CMD_PREFIX = "/serverMsg"; // + msg
    private static final String PRIVATE_MSG_CMD_PREFIX = "/w"; //sender + p + msg
    private static final String END_CMD_PREFIX = "/end"; // end
    private String username;


    public ClientHandler(MyServer myServer, Socket socket){
        this.myServer = myServer;
        this.clientSocket = socket;
    }

    public void handle() throws IOException {

        out = new DataOutputStream(clientSocket.getOutputStream());
        in = new DataInputStream(clientSocket.getInputStream());
        
        new Thread(() -> {
            try {
                authentication();
                readMessage();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        }).start();
    }

    private void authentication() throws IOException {
        while (true) {
            String message = in.readUTF();
            /*String message = "/auth martin 1111";*/
            if (message.startsWith(AUTH_CMD_PREFIX)) { //проверка префикса аутентификации
                boolean isSuccessAuth = processAuthCommand(message); //проверка логина и пароля
                if(isSuccessAuth) {
                    break;
                }
            } else {
                out.writeUTF(AUTHERR_CMD_PREFIX + " Ошибка авторизации");
            }
        }
    }

    private boolean processAuthCommand(String message) throws IOException {
        String[] parts = message.split("\\s+", 3); //разбиваем строку на слова по принципу разрыва \\s+
        String login = parts[1];
        String password = parts[2];

        AuthService authService = myServer.getAuthService();

        username = authService.getUsernameByLoginAndPassword(login, password);

        if(username != null) {
            if(myServer.isUsernameBusy(username)) {
                out.writeUTF(AUTHERR_CMD_PREFIX + " Логин уже используется");
                return false;
            }


            out.writeUTF(AUTHOK_CMD_PREFIX + " " + username);
            myServer.subscribe(this);
            out.writeUTF(  username + " зашёл в чат");
            return true;
        }

        else {
            out.writeUTF(AUTHERR_CMD_PREFIX + " Логин или пароль не соответствуют действительности");
            return false;
        }

    }

    private void readMessage() throws IOException { //сообщения от клиента
        while (true) {
            String message = in.readUTF();
            System.out.println("message | " + username + " : " + message);
            if(message.startsWith(END_CMD_PREFIX)) {
                return;
            }
            else if(message.startsWith(PRIVATE_MSG_CMD_PREFIX)) {
                String[] parts = message.split("\\s+", 3);
                String recipientUsername = parts[1];
                String privateMessage = parts[2];
                myServer.sendPrivateMessage(message, this, recipientUsername);

            } else {
                myServer.broadcastMessage(message, this);
            }
        }
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String sender, String message) throws IOException {
        out.writeUTF(String.format("%s %s %s", CLIENT_MSG_CMD_PREFIX, sender, message));
    }
}
