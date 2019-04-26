package cn.yxisme;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangxiong on 2019/4/25.
 */
public class Server {

    private final static int PORT = 9999;
    private Map<String, Socket> clients;
    private Map<String, Socket> exited;

    public static void main(String[] args) {
        new Server();
    }

    private Server() {
        try {
            clients = new HashMap<>();
            ServerSocket serverSocket = new ServerSocket(PORT);

            System.out.println("服务器创建成功！");
            System.out.println("==============");

            while (true) {
                Socket client = serverSocket.accept();
                new ClientHandler(client).start();
            }
        } catch (IOException e) {
            System.out.println("socket创建失败");
            e.printStackTrace();
        }
        System.out.println("服务端已关闭！");
    }

    class ClientHandler extends Thread {
        Socket client;

        ClientHandler(Socket client) {
            this.client = client;
        }

        public void run() {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));

                String username;
                if ((username = br.readLine()) != null && username.contains("%username%")) {
                    username = username.split(":")[1];
                    clients.put(username, client);
                }

                System.out.println(username + "----" + client.getRemoteSocketAddress() + "加入连接");
                String msg = "欢迎【" + username + "】进入聊天室！当前聊天室有【" + clients.size() + "】人";

                sendMsg(msg, true);
                while ((msg = br.readLine()) != null) {
                    msg =  username + "：" + msg;
                    sendMsg(msg, false);
                    removeUnLiveSocket();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void sendMsg(String msg, boolean all) {
            exited = new HashMap<>();

            clients.forEach((k, v) -> {
                try {
                    if (all || !(v.getRemoteSocketAddress().toString())
                            .equals(client.getRemoteSocketAddress().toString())) {
                        BufferedWriter serverWriter = new BufferedWriter(new OutputStreamWriter(v.getOutputStream()));
                        serverWriter.write(msg);
                        serverWriter.write("\n");
                        serverWriter.flush();
                    }
                } catch (IOException e) {
                    exited.put(k, v);
                }
            });
        }

        private void removeUnLiveSocket() {
            exited.forEach((k, v) -> {
                clients.remove(k);
                String msg = "【" + k + "】已退出聊天室！当前聊天室有【" + clients.size() + "】人";
                sendMsg(msg, true);
            });
        }
    }
}
