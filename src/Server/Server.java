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

    public boolean isNickBusy(String nick) {
        for (ClientHandler o : clients) {
            if (o.getNick().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public void sendPrivateMsg(String private_msg) {
        String[] words = private_msg.split(" ",4);

        String nick = words[0];
        String nick_to = words[2];

        nick = nick.substring(0, nick.length() - 1);

        if (client_by_nickname(nick_to) != null) {
            client_by_nickname(nick_to).sendMSG("PM FROM " + nick + ": " + words[3]);
            client_by_nickname(nick).sendMSG("PM FOR " + nick_to + ": " + words[3]);
        } else client_by_nickname(nick).sendMSG("Пользователь " + nick_to + " не онлайн.");
    }

    public ClientHandler client_by_nickname(String nick){
            ClientHandler client = null;
            for (ClientHandler cl:clients)
                if (cl.getNick().equals(nick)) {
                    client = cl;
                }
            return client;
        }



}
