package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ClientHandler {
    private Server server;
    private Socket socket;
    DataInputStream in;
    DataOutputStream out;
    String nick = null;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());


            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String str = in.readUTF();

                            if (str.startsWith("/auth")) {
                                String[] tokens = str.split(" ");
                                String newNick = AuthService.getNickName(tokens[1], tokens[2]);
                                if (newNick != null) {
                                    sendMSG("/authOK");
                                    nick = newNick;
                                    server.subscribe(ClientHandler.this);
                                    break;
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
                                server.broadcastMSG(nick + ": " +str);
                        }
                    } catch (IOException | SQLException e) {
                        e.printStackTrace();
                    }finally {
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
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendMSG(String msg){
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
