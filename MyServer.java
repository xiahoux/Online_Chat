package sample.server.chat;

import sample.server.chat.auth.BaseAuthService;
import sample.server.chat.handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {
    private final ServerSocket serverSocket;
    private final BaseAuthService authService;
    private final List<ClientHandler> clients = new ArrayList<>();

    public MyServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.authService = new BaseAuthService();
    }

    public BaseAuthService getAuthService() {
        return authService;
    }

    public void start() {

        System.out.println("Сервер запущен");
        try {
            while (true) { //ожидание пользователей
                waitAndProcessNewClientConnection();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void waitAndProcessNewClientConnection () throws IOException {
        System.out.println("Ожидание пользователя...");
        Socket socket = serverSocket.accept();
        System.out.println("Клиент подключился");


    }

    private void processClientConnection(Socket socket) throws IOException {
        ClientHandler clientHandler = new ClientHandler(this, socket);
        clientHandler.handle();
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public synchronized void unSubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public synchronized boolean isUsernameBusy(String username) {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void broadcastMessage(String message, ClientHandler sender) throws IOException {
        for (ClientHandler client : clients) {
            if(client == sender) {
                continue;
            }
            client.sendMessage(sender.getUsername(), message);
        }
    }

    public synchronized void sendPrivateMessage(String message, ClientHandler sender, String recipient) throws IOException {
        for (ClientHandler client : clients) {
            if(client == sender) {
                continue;
            } else if(client.getUsername().equals(recipient)) {
                client.sendMessage(sender.getUsername(), message);
            }
        }
    }
}
