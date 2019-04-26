package cn.yxisme;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by yangxiong on 2019/4/25.
 */
public class Client {

    private Socket server;

    public static void main(String[] args) {
        new Client();
    }

    private Client() {
        try {
            server = new Socket("127.0.0.1", 9999);
            System.out.println("连接服务器(" + server.getRemoteSocketAddress() + ")成功！");

            // 填写用户名
            System.out.println("请输入用户名:");
            Scanner scanner = new Scanner(System.in);
            String username = scanner.next();

            BufferedWriter serverWriter = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
            serverWriter.write("%username%:" + username);
            serverWriter.write("\n");
            serverWriter.flush();

            System.out.println("============================");

            new SendMessage().start();

            BufferedReader serverReader = new BufferedReader(new InputStreamReader(server.getInputStream()));
            String serverReply;
            while (true) {
                if ((serverReply = serverReader.readLine()) != null) {
                    System.out.println(serverReply);
                }
            }
        } catch (IOException e) {
            System.out.println("连接超时...");
            e.printStackTrace();
        }
    }

    class SendMessage extends Thread {
        public void run() {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                BufferedWriter serverWriter = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));

                String message;
                while (true) {
                    message = bufferedReader.readLine();
                    serverWriter.write(message);
                    serverWriter.write("\n");
                    serverWriter.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
