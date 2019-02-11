package Server;

import Client.Controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Objects;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private String nick;
    DataInputStream in;
    DataOutputStream out;

    public String getNick() {
        return nick;
    }

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());


            new Thread(() -> {
                try {
                    while (true) {
                        String str = in.readUTF();

                        if (str.startsWith("/auth")) {
                            String[] tokens = str.split(" ");
                            String newNick = AuthService.getNickName(tokens[1], tokens[2]);
                            if (newNick != null) {
                                if (!server.isNickBusy(newNick)) {
                                    sendMSG("/authOK");
                                    nick = newNick;
                                    server.subscribe(this);
                                    server.broadcastMSG(nick + " Подключился");
                                    break;
                                } else {
                                    sendMSG("Already login");
                                }
                            } else {
                                sendMSG("Неверный логин/пароль!");
                            }
                        }
                    }

                    while (true) {
                        String str = in.readUTF();
                        if (str.equals("/end")) {
                            out.writeUTF("/serverclosed");
                            break;
                        }
                        server.broadcastMSG(nick + ": " + str);
                    }
                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/w")) {
                            String[] line = str.split(" ");
                            server.sendPrivateMsg(str);
                        }
                    }

                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    server.unsubscribe(ClientHandler.this);
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMSG(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
