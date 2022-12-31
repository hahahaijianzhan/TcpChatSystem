
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
public class Server {
    private ServerSocket serverSocket;
    private HashMap<String,Socket> userMap;//UserSocket用户的线程
    public Server() {
        try {
            serverSocket = new ServerSocket(1111);
            userMap = new HashMap<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void start() throws IOException {
        // 启动服务器，同时监听8888端口
        System.out.println("服务器启动");
        // 服务器始终处于一个保存连接的状态
        while (true) {
            // 接收客户端请求，得到一个Socket对象
            Socket socket = serverSocket.accept();
            System.out.println("服务器接收到新线程");
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            String userName = br.readLine();
                userMap.put(userName, socket);//添加到用户列表}
                System.out.println("建立新线程");
                for (String key : userMap.keySet()) {
                    Socket value = userMap.get(key);
                    System.out.println(value);
                }
                new UserSocket(userName, socket).start();
            }
    }
    class UserSocket extends Thread {
        private Socket socket;
        PrintWriter pw;
        BufferedReader br;
        private String userName;
        public UserSocket(String userName,Socket socket) {
            this.socket = socket;
            this.userName = userName;
            try {
                pw = new PrintWriter(socket.getOutputStream());
                br = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));

                System.out.println("创建专属用户线程成功");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run() {
            while (true) {
                try {
                    String str=br.readLine();
                    if(str.split(":")[0].equals("@")){
                        sendMessageToAClient(str);
                    }
                    else if(str.split(":")[0].equals("#")){
                        sendMessageToAllClient(str);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        public void sendMessageToAClient(String msg) {
            Date d = new Date();
            String[] mesg = msg.split(":");
            Socket socket = userMap.get(mesg[3]);
            Socket yoursocket = userMap.get(userName);
            PrintWriter pw;
            PrintWriter pww;
            try {
                pw = new PrintWriter(socket.getOutputStream());
                pw.println(mesg[1] + ":" + mesg[2]);
                pw.flush();
                pww = new PrintWriter(yoursocket.getOutputStream());
                pww.println("你说:"+mesg[2]);
                pww.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void sendMessageToAllClient(String mesg) {
            Date d = new Date();
            Collection values = userMap.values();
            for (Object temp : values) {
                try {
                    Socket s = (Socket)temp;
                    PrintWriter pw = new PrintWriter(s.getOutputStream());
                    String msgs[] = mesg.split(":");
                    pw.println(msgs[1]+":"+msgs[2]);
                    pw.flush();
                } catch (Exception e) {

                }
            }
        }

    }

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
