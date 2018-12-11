package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

public class Server {

    private Vector<ClientHandler> clients;

    public Server() {
        clients = new Vector<>();
        ServerSocket server = null;
        Socket socket = null;
        try {
            server = new ServerSocket(8585);
            System.out.println("Сервер запущен!");




            while (true) {
                socket = server.accept();

                System.out.println("Клиент подключен!");
                clients.add(new ClientHandler(this, socket));


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
    }

    public void broadcastMSG(String msg) {
        for (ClientHandler c:clients)
            c.sendMSG(msg);
             {

        }
    }
}
