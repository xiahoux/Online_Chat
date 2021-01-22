package sample.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class EchoServer {

    public static Scanner scanner = new Scanner(System.in);

    private static final int SERVER_PORT = 8888;

    public static void main(String[] args) {
        Socket socket = null;

        try(ServerSocket serverSocket = new ServerSocket(SERVER_PORT)){
            System.out.println("Ожидание подключения...");
            while (true) {
                socket = serverSocket.accept();
                System.out.println("Клиент подключился");
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                try {
                    Thread t1 = new Thread(() -> {
                        try{
                            while (true){
                                String serverMessage = scanner.next();
                                out.writeUTF(serverMessage);
                                if(serverMessage.equals("/end")){
                                    break;
                                }
                            }
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    });
                    t1.setDaemon(true);
                    t1.start();
                    while (true) {
                        String message = in.readUTF();
                        if (message.equals("/end")) {
                            break;
                        }
                        System.out.println("Сообщение пользователя: " + message);
                        /*out.writeUTF("Эхо: " + message);*/
                    }


                    while (true){
                        String serverMessage = scanner.next();
                        out.writeUTF(serverMessage);
                        if(serverMessage.equals("/end")){
                            break;
                        }
                    }

                    System.out.println("Сервер остановлен");
                }catch (IOException e){
                    socket.close();
                    System.out.println("Клиент отключился");
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }


}
