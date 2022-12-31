package ClientFrame;

import DataBase.DataBaseConnect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddFriendFrame extends JFrame implements ActionListener {
    JLabel jLabel,warning;
    JPanel jp1;
    JTextField jt1;
    JButton jb1;
    MainWindow mainWindow;
    DataBaseConnect dataBaseConnect;
    public AddFriendFrame(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setIconImage(new ImageIcon("src/image/QQ_64.png").getImage());
        setTitle("添加好友");
        warning = new JLabel();//若添加成功则显示添加成功
        jLabel = new JLabel("请输入要添加的账号");
        jp1=new JPanel();
        jt1=new JTextField("",20);
        jb1=new JButton("添加");
        jb1.addActionListener(this);
        jp1.add(jLabel);
        jp1.add(jt1);
        jp1.add(jb1);
        add(jp1);
        add(warning, BorderLayout.SOUTH);
        setSize(500,150);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        dataBaseConnect = new DataBaseConnect();
        Connection connection = dataBaseConnect.getConnection();
        String sql1 = "SELECT userID FROM user";//获取用户表中的所有用户ID,用于判断要添加的好友是否已经注册
        String sql2 = "SELECT friendID FROM friendlist WHERE selfID=?";//获取自己账号所拥有的所有好友
        String sql4 = "SELECT userName From user WHERE userID=?";//获取要添加的好友的名字
        String sql5 = "INSERT INTO friendlist (selfID,selfName,friendID,friendName) VALUES(?,?,?,?)";//向好友表中插入数据

        List<Integer> userlist = new ArrayList<>();
        List<Integer> friendlist = new ArrayList<>();
        try {
            int friendid = Integer.parseInt(jt1.getText());
            Statement statement = connection.createStatement();
            ResultSet r1 = statement.executeQuery(sql1);
            while (r1.next()) {
                userlist.add(r1.getInt("userid"));//获取已注册用户列表
            }
            if (userlist.contains(friendid)){
                PreparedStatement p1 = connection.prepareStatement(sql2);
                PreparedStatement p2 = connection.prepareStatement(sql4);
                PreparedStatement p3 = connection.prepareStatement(sql5);

                p1.setInt(1,mainWindow.userID);
                ResultSet r2 = p1.executeQuery();
                String name = null;
                while (r2.next()) {
                    friendlist.add(r2.getInt("friendID"));
                }
                if (friendid == mainWindow.userID) {
                    warning.setText("不能添加自己为好友");
                }
                //判断要添加的好友是否已存在于自己的好友列表
                else if (friendlist.contains(friendid)) {
                    warning.setText("您的好友列表已有此人");
                }
                else //若没有则向表中添加数据
                //先获取要添加好友的名字
                    p2.setInt(1, friendid);
                    ResultSet r3 = p2.executeQuery();
                    String friendName = "";
                    while (r3.next()) {
                        friendName = r3.getString("userName");
                    }
                        //插入数据
                        p3.setInt(1, mainWindow.userID);
                        p3.setString(2, mainWindow.userName);
                        p3.setInt(3, friendid);
                        p3.setString(4, friendName);
                        int rs1 = p3.executeUpdate();
                        p3.setInt(1, friendid);
                        p3.setString(2, friendName);
                        p3.setInt(3, mainWindow.userID);
                        p3.setString(4, mainWindow.userName);
                        int rs2 = p3.executeUpdate();
                        warning.setText("添加成功！");
            }
            else
                warning.setText("您要添加的好友不存在");

        } catch (NumberFormatException n){
            warning.setText("请输入数字");
        }catch (SQLException ignored) {
        }finally {
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}