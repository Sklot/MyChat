package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Arrays;
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
                new ClientHandler(this, socket);


            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
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
        String[] words = msg.split(" ");
        if (words[1].equals("/w")) {
            sendPrivateMsg(msg);

        } else {
            for (ClientHandler c : clients)
                c.sendMSG(msg);
            {
            }
        }
    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public void sendPrivateMsg(String private_msg) {
        String[] words = private_msg.split(" ",4);
        String nick_from = words[0];
        String nick_to = words[2];

        nick_from = nick_from.substring(0, nick_from.length() - 1);
        for (int i = 0; i <clients.size() ; i++) {
            if (nick_to.equals(clients.elementAt(i).getNick())) {
                clients.elementAt(i).sendMSG("PM FROM " + nick_from + ": " + words[3]);
            } else {

            }
        }
    }
}
