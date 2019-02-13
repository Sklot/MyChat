package Server;

import Client.Controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
                                    this.sendMSG("Для списка команд введите /help");
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
                            if (str.equals("/quit")) {
                                out.writeUTF("/serverclosed");
                                server.broadcastMSG(this, nick + " Отключился");
                                break;
                            }
                            if (str.startsWith("/blacklist")) {
                                String[] words = str.split(" ");
                                AuthService.addNickInBlackList(nick, words[1]);
                                sendMSG("Пользователь " + words[1] + " добавлен в чёрный список.");

                                int howmany = AuthService.howManyInBl(nick);
                                switch (howmany) {
                                    case 1:
                                        sendMSG("Вы добавили первого персонажа в свой ЧС!");
                                        break;
                                    case 2:
                                        sendMSG("Вдвоём им не так скучно будет в темнице блоклиста.");
                                        break;
                                    case 3:
                                        sendMSG("Теперь и на троих сообразить могут.");
                                        break;
                                    case 4:
                                        sendMSG("Опа. Басенный квартет =)");
                                        break;
                                    case 5:
                                        sendMSG("Были б нормальные ребята, хватило б на хоккейную пятёрку...");
                                        break;
                                    default:
                                        sendMSG("В чёрном списке уже " + howmany + " сомнительных персонажей.");
                                }
                            }
                            if (str.equals("/help")) {
                                sendMSG("Список служебных команд:\n" +
                                        "/quit  ==>  выход из чата. \n" +
                                        "/blacklist  nickname ==>  добавить nickname в ЧС. \n" +
                                        "/removefromBL nickname ==> удалить nickname из ЧС. \n" +
                                        "/showmyBL ==> показать весь ЧС. \n" +
                                        "/clearBL ==> очистить ЧС. \n" +
                                        "/w nickname ==> отправить ЛС для nickname.");
                            }

                            if (str.startsWith("/removefromBL")) {
                                String[] words = str.split(" ");
                                AuthService.removeNickFromBlackList(nick, words[1]);
                                sendMSG("Пользователь " + words[1] + " удалён из чёрного списка.");
                            }
                            if (str.equals("/clearBL")) {
                                AuthService.clearBlackList(nick);
                                sendMSG("Чёрный список очищен!");
                            }

                            if (str.equals("/showmyBL")) {
                                ArrayList<String> res = AuthService.showMyBlackList(nick);
                                if (res.size() > 0) {
                                    sendMSG("Ваш чёрный список:");
                                    for (String s : res) {
                                        sendMSG(s);
                                    }
                                } else sendMSG("Ваш чёрный список пуст.");

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
