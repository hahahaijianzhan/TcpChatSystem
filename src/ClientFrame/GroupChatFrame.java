package ClientFrame;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

//设计群聊界面
public class GroupChatFrame extends JFrame implements ActionListener {
    JPanel jp1,jp2;
    JTextArea jt1,jt2;  //jt1为显示聊天记录界面，jt2为编辑消息文本域
    JButton close,send;
    private PrintWriter pw;
    private BufferedReader br;
    String userChatName;
    ReadMessageThread readMessageThread;
    public GroupChatFrame(MainWindow mainWindow) throws IOException {
        this.userChatName = mainWindow.userName;
        Socket socket = new Socket("192.168.0.101", 1111);
        pw = new PrintWriter(socket.getOutputStream());
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        setTitle("聊天室");
        pw.println(mainWindow.userName);
        pw.flush();
        setMinimumSize(new Dimension(600, 300));
        setIconImage(new ImageIcon("src/image/QQ_64.png").getImage());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setSize(600, 400);
        setLocationRelativeTo(null);
        init();
    }
    public void init()  {
        jp1 = new JPanel(new BorderLayout(30,40));
        jp2 = new JPanel(new BorderLayout(50,0));
        jt1 = new JTextArea(10,50);
        jt1.setEditable(false);
        jt2 = new JTextArea(5,50);

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
            return;
        }
        if (jt2.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "不能发送空消息！");
        }else
            pw.println("#" + ":" + userChatName+":"+jt2.getText());
        pw.flush();
        jt2.setText("");
    }
}
