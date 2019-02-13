package Server;

import Client.Controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private String nick;
    DataInputStream in;
    DataOutputStream out;
    private List<String> blacklist;

    public String getNick() {
        return nick;
    }

    public List<String> getBlacklist() {
        return blacklist;
    }

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.blacklist = new ArrayList<>();


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
                                    server.broadcastMSG(this, nick + " Подключился");
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
                        if (str.startsWith("/")) {
                            if (str.equals("/end")) {
                                out.writeUTF("/serverclosed");
                                server.broadcastMSG(this, nick + " Отключился");
                                break;
                            }
                            if (str.startsWith("/blacklist")) {
                                String[] words = str.split(" ");
                                blacklist.add(words[1]);
                                sendMSG("Пользователь "+words[1]+" добавлен в чёрный список.");

                            }
                            if (str.startsWith("/w")) {
                                String[] words = str.split(" ", 3);

                                server.sendPrivateMsg(this, words[1], words[2]);
                            }
                        } else server.broadcastMSG(this, nick + ": " + str);
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
