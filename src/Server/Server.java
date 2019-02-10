package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.Vector;

public class Server {

    private Vector<ClientHandler> clients;

    public Server() throws SQLException {
        clients = new Vector<>();
        ServerSocket server = null;
        Socket socket = null;
        try {
            AuthService.connect();
            server = new ServerSocket(8585);
            System.out.println("Сервер запущен!");

            while (true) {
                socket = server.accept();

                System.out.println("Клиент подключен!");
//                subscribe(new ClientHandler(this, socket ));
                new ClientHandler(this, socket);


            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        AuthService.disconnect();
    }

    public void broadcastMSG(String msg) {
        for (ClientHandler c:clients)
            c.sendMSG(msg);
             {

        }
    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }
}
