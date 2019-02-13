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

    public void broadcastMSG(ClientHandler client, String msg) {
        String nick = client.getNick();
        for (int i = 0; i <clients.size() ; i++) {
            if (!clients.elementAt(i).getBlacklist().contains(nick)) {
                clients.elementAt(i).sendMSG(msg);
            }else client.sendMSG(clients.elementAt(i).getNick() + " заблокировал вас.");

        }
//        for (ClientHandler c : clients)
//            c.sendMSG(msg);
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
        broadcastClientList();
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastClientList();
    }

    public void sendPrivateMsg(ClientHandler cl, String to, String msg) {
        ClientHandler pm_to = client_by_nickname(to);


        if (to != null) {
            pm_to.sendMSG("PM FROM " + cl.getNick() + ": " + msg);
            cl.sendMSG("PM FOR " + to + ": " + msg);
        } else cl.sendMSG("Пользователь " + to + " не онлайн.");
    }

    public void broadcastClientList() {
        StringBuilder sb = new StringBuilder();
        sb.append("/clientlist ");
        for (ClientHandler c : clients) {
            sb.append(c.getNick()+ " ");
        }
        String out = sb.toString();
        for (ClientHandler cl : clients) {
            cl.sendMSG(out);
        }
    }

    public ClientHandler client_by_nickname(String nick) {
        ClientHandler client = null;
        for (ClientHandler cl : clients)
            if (cl.getNick().equals(nick)) {
                client = cl;
            }
        return client;
    }
}
