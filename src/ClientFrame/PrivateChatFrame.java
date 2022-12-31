package ClientFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.util.Random;
//设计私聊界面
public class PrivateChatFrame extends JFrame implements ActionListener {
    JPanel jp1,jp2;
    JTextArea jt1,jt2;  //jt1为显示聊天记录界面，jt2为编辑消息文本域
    JButton close,send;
    MainWindow mainWindow;
    String userChatName;//自己用户名
    int userid;//自己用户id
    String nodeName;//对方用户名
    int nodeid;//对方id
    ReadMessageThread readMessageThread;
    boolean isconnect;
    private PrintWriter pw;
    private BufferedReader br;
    //到时候设计一个构造函数，参数为好友信息，就可以传递好友的名字了。
    public PrivateChatFrame(MainWindow mainWindow,String userChatName,String nodeName) throws IOException {
        Socket socket = new Socket("192.168.0.101", 1111);
        pw = new PrintWriter(socket.getOutputStream());
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        this.mainWindow=mainWindow;
        this.userChatName = userChatName;
        this.nodeName=nodeName;
        this.nodeid=mainWindow.selectNodeid;
        userid = mainWindow.userID;
        pw.println(userChatName);
        pw.flush();
        setTitle(nodeName);
        setMinimumSize(new Dimension(600,300));
        setIconImage(new ImageIcon("src/image/QQ_64.png").getImage());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600,400);
        Random random = new Random();
        setLocation(random.nextInt(700),150);
        init();
        isconnect = true;
        readMessageThread = new ReadMessageThread();
        readMessageThread.start();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                readMessageThread.interrupt();
            }
        });
    }
    public void init() {
        jp1 = new JPanel(new BorderLayout(30,40));
        jp2 = new JPanel(new BorderLayout(20,0));
        jt1 = new JTextArea(10,50);
        jt1.setEditable(false);
        jt2 = new JTextArea(5,50);
        jt2.setLineWrap(true);
        JScrollPane jScrollPane = new JScrollPane(jt1);
        jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollPane jScrollPane1 = new JScrollPane(jt2);
        jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        close = new JButton("关闭");
        send = new JButton("发送");
        close.addActionListener(this);
        send.addActionListener(this);
        jp1.add(jScrollPane);
        jp2.add(jScrollPane1, BorderLayout.CENTER);
        JPanel jPanel = new JPanel();
        jPanel.add(close);
        jPanel.add(send);
        jp2.add(jPanel, BorderLayout.SOUTH);
        add(jp1, BorderLayout.CENTER);
        add(jp2, BorderLayout.SOUTH);
        pack();
        setVisible(true);
    }
    class ReadMessageThread extends Thread {
        public void run() {
            while (true) {
                try {
                    String str = br.readLine();
                    System.out.println(str);
                    String[] msgs = str.split(":");
                        jt1.append(msgs[0] +":"+msgs[1]+ "\n");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        JButton jButton=(JButton)e.getSource();
        if (jButton.equals(close)) {
            this.setVisible(false);
            readMessageThread.interrupt();
            System.out.println();
            mainWindow.UpdataFriendList();
            return;
        }
        if (jt2.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "不能发送空消息！");
        }else
        pw.println("@" + ":" +userChatName +":"+jt2.getText()+ ":" + nodeName);
        pw.flush();
        jt2.setText("");
    }
}
