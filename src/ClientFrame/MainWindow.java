package ClientFrame;

import DataBase.DataBaseConnect;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//设计主要窗口
public class MainWindow extends JFrame implements ActionListener, TreeSelectionListener {
    JPanel p1,p3,MyFriend,ChatGroup;//p1为窗口北部的面板，p3为窗口南部的面板，窗口中间位置使用JTabbedPane（选项卡面板）设计
    JLabel jl_name, jl_HeadImage;//jl_name用户昵称，jl_HeadImage 表示用户头像
    int userID;
    String userName;
    String nodeName;
    JButton addFriend,LogOff;
    JButton EnterChatRoom;
    AddFriendFrame addFriendFrame;
    DefaultMutableTreeNode root1;
    JTree jTree1;
    GroupChatFrame groupChatFrame;
    LoginWindow loginWindow;
    JTabbedPane tabbedPane;
    PrivateChatFrame privateChatFrame;
    DataBaseConnect dataBaseConnect;
    int selectNodeid;
    public MainWindow(LoginWindow loginWindow) throws IOException {
        this.loginWindow=loginWindow;
        userID=loginWindow.getuid();
        userName = loginWindow.username;
        p1 = new JPanel();//窗口北部面板p1
        p3 = new JPanel();//窗口南部面板p3
        addFriend = new JButton("添加好友");
        LogOff = new JButton("退出登录");

        //设计窗口北部面板p1
        p1.setLayout(new BorderLayout(10, 5));//设置面板p1的为边界布局以及面板内各组件的水平和垂直间隙

        //jl_name 用户昵称
        jl_name = new JLabel(loginWindow.username);
        jl_name.setFont(new Font("Dialog",Font.BOLD,14));//设置标签的字体
        p1.add(jl_name,BorderLayout.CENTER);//将标签jl_name放入面板p1中间
        //设置用户头像
        ImageIcon icon2 = new ImageIcon("J:\\ChatInQQ\\src\\client\\img\\headImage\\head_boy_01_64.jpg");
        jl_HeadImage = new JLabel(icon2);  //jl_HeadImage 用户头像
        p1.add(jl_HeadImage, BorderLayout.WEST);//将头像放到面板p1的西边

        //设计窗口中间
         tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        JScrollPane scrollPane = new JScrollPane();

        MyFriend = new JPanel();//联系人面板
        MyFriend.setLayout(new BorderLayout(0, 0));
        MyFriend.add(scrollPane, BorderLayout.CENTER);
        ChatGroup = new JPanel();//群聊面板
        ChatGroup.setLayout(new BorderLayout(0, 0));
        ChatGroup.add(scrollPane, BorderLayout.CENTER);
        tabbedPane.addTab("联系人", new ImageIcon("J:\\ChatInQQ\\src\\client\\img\\friend_list.png"), MyFriend, null);
        tabbedPane.addTab("聊天室", new ImageIcon("J:\\ChatInQQ\\src\\client\\img\\friend_qun.png"), ChatGroup, null);

        //设计好友列表
        root1 = new DefaultMutableTreeNode("好友列表");
        jTree1 = new JTree(root1);
        jTree1.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);//设置jTree1的选择模式为一次只能选择一个节点
        jTree1.addTreeSelectionListener(this);
        MyFriend.add(jTree1);
        EnterChatRoom = new JButton("进入聊天室");
        ChatGroup.add(EnterChatRoom, BorderLayout.NORTH);
        EnterChatRoom.addActionListener(this);
        //设计窗口南部 1
        addFriend = new JButton("添加好友");
        LogOff = new JButton("退出登录");
        p3.setLayout(new FlowLayout(20));
        p3.add(addFriend);
        p3.add(LogOff);

        addFriend.addActionListener(this);

        LogOff.addActionListener(this);
        add(p1, BorderLayout.NORTH); //将面板一放入窗口北部
        add(tabbedPane, BorderLayout.CENTER);//将选项卡面板放入窗口中间
        add(p3, BorderLayout.SOUTH); //将面板三放入窗口底部
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 600);
        Random random = new Random();
        setLocation(random.nextInt(900),150);

        //设置窗口左上角图标
        setIconImage(new ImageIcon("src/image/QQ_64.png").getImage());
        setMinimumSize(new Dimension(350,700));//设置窗口的最小大小
        pack();
        UpdataFriendList();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                UpOnlineList();
            }
        });
    }
    //定义刷新好友列表的方法
    public void UpdataFriendList(){
        MyFriend.removeAll();
        jTree1.removeAll();
        root1.removeAllChildren();
            DataBaseConnect dataBaseConnect = new DataBaseConnect();
            List<String> friendList = new ArrayList<>();
            Connection connection = dataBaseConnect.getConnection();
            try {
                String sql1 ="SELECT friendName FROM friendlist WHERE selfid =?";
                PreparedStatement p = connection.prepareStatement(sql1);
                p.setInt(1, userID);
                ResultSet resultSet = p.executeQuery();
                while (resultSet.next()) {
                    friendList.add(resultSet.getString("friendName"));
                }
                for (String s : friendList) {
                    root1.add(new DefaultMutableTreeNode(s));
                }
                jTree1 = new JTree(root1);
                jTree1.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);//设置jTree1的选择模式为一次只能选择一个节点
                jTree1.addTreeSelectionListener(this);
                MyFriend.add(jTree1);
                MyFriend.repaint();
                MyFriend.revalidate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }
    public void UpOnlineList(){
        DataBaseConnect dataBaseConnect = new DataBaseConnect();
        Connection connection = dataBaseConnect.getConnection();
        String sql = "DELETE From OnlineList where userid=?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userID);
            int i = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        JButton jButton = (JButton) e.getSource();
        if (jButton.equals(addFriend)){
            addFriendFrame= new AddFriendFrame(this);
            addFriendFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosed(e);
                    UpdataFriendList();
                }
            });
        }

        if (jButton.equals(LogOff)) {
            UpOnlineList();
            new LoginWindow();
            this.setVisible(false);
        }
        if (jButton.equals(EnterChatRoom)) {
            try {
                groupChatFrame = new GroupChatFrame(this);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            groupChatFrame.setVisible(true);
            groupChatFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);
                    groupChatFrame.setVisible(false);
                }
            });
        }
    }
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        JTree jTree= (JTree) e.getSource();
        if (jTree.equals(jTree1)&root1.getChildCount()!=0) {
            DefaultMutableTreeNode selectionNode = (DefaultMutableTreeNode) jTree1.getLastSelectedPathComponent();
            this.nodeName = selectionNode.toString();
            if (selectionNode.isLeaf()) {
                dataBaseConnect = new DataBaseConnect();
                Connection connection = dataBaseConnect.getConnection();
                String sql = "SELECT friendid from friendlist where selfid=? and friendname=?";
                try {
                    PreparedStatement p = connection.prepareStatement(sql);
                    p.setInt(1, userID);
                    p.setString(2,this.nodeName);
                    ResultSet resultSet = p.executeQuery();
                    while (resultSet.next()) {
                        this.selectNodeid = resultSet.getInt("friendid");//获取所点击好友的id
                    }
                    try {
                        privateChatFrame=new PrivateChatFrame(this, userName,nodeName);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                privateChatFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        super.windowClosing(e);
                        UpdataFriendList();
                    }
                });
            }
        }
    }
}