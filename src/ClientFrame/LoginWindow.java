package ClientFrame;

import DataBase.DataBaseConnect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//登录界面
public class LoginWindow extends JFrame implements ActionListener{
    DataBaseConnect dataBaseConnect;
    Connection connection;
    Statement statement;
    JLabel jl1,jl2;
    JTextField jt1;
    JPasswordField pwd;//输入密码框
    JButton jb1,jb2;
    JPanel jp;
    String username;//用户昵称
    public LoginWindow(){
        dataBaseConnect = new DataBaseConnect();
        setTitle("登录");
        //设置窗口左上角图标
        setIconImage(new ImageIcon("src/image/QQ_64.png").getImage());
        init();
    }
    public void init(){
        jl1 = new JLabel("账号");
        jl2 = new JLabel("密码");
        jt1 = new JTextField("");
        pwd = new JPasswordField();
        pwd.setEchoChar('*');

        //添加显示密码图标按钮
        JButton viewButton = new JButton(new ImageIcon("src/image/密码不可见.png"));
        //添加隐藏密码图标按钮
        JButton viewHideButton = new JButton(new ImageIcon("src/image/密码可见.png"));
        pwd.putClientProperty("JTextField.trailingComponent", viewButton);
        //给显示密码图标绑定单击事件
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pwd.putClientProperty("JTextField.trailingComponent", viewHideButton);//设置隐藏按钮显示
                pwd.setEchoChar((char)0);//设置密码显示
                viewButton.setVisible(false);
                viewHideButton.setVisible(true);
            }
        });
        //给隐藏密码图标绑定单击事件
        viewHideButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pwd.putClientProperty("JTextFiled.trailingComponent", viewButton);//设置显示按钮显示
                pwd.setEchoChar('*');//设置密码隐藏
                viewHideButton.setVisible(false);
                viewButton.setVisible(true);
            }
        });
        jb1 = new JButton("登录");
        jb2 = new JButton("注册");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jp = new JPanel();
        jb1.addActionListener(this);

        //运用GroupLayout将组件整齐放入界面
        GroupLayout layout = new GroupLayout(jp);
        jp.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        //水平组
        GroupLayout.ParallelGroup hParalGp1 = layout.createParallelGroup().addComponent(jl1).addComponent(jl2);
        GroupLayout.ParallelGroup hParalGp2 = layout.createParallelGroup().addComponent(jt1).addComponent(pwd);
        GroupLayout.ParallelGroup hParalGp3 = layout.createParallelGroup().addComponent(viewButton).addComponent(viewHideButton);
        GroupLayout.SequentialGroup hSeqGp1 = layout.createSequentialGroup().addGroup(hParalGp1).addGroup(hParalGp2).addGroup(hParalGp3);
        GroupLayout.SequentialGroup hSeqGp2 = layout.createSequentialGroup().addGap(70).addComponent(jb1).addComponent(jb2);

        GroupLayout.ParallelGroup hPralGroup = layout.createParallelGroup().addGroup(hSeqGp1).addGroup(hSeqGp2);

        layout.setHorizontalGroup(hPralGroup);

        //垂直组
        GroupLayout.ParallelGroup vParalGp1 = layout.createParallelGroup().addComponent(jl1).addComponent(jt1);
        GroupLayout.ParallelGroup vParalGp2 = layout.createParallelGroup().addComponent(jl2).addComponent(pwd).addComponent(viewButton).addComponent(viewHideButton);
        GroupLayout.ParallelGroup vParalGp3 = layout.createParallelGroup().addComponent(jb1).addComponent(jb2);
        GroupLayout.SequentialGroup vSeqGroup = layout.createSequentialGroup().addGroup(vParalGp1).addGroup(vParalGp2).addGroup(vParalGp3);

        layout.setVerticalGroup(vSeqGroup);


        setContentPane(jp);
        setSize(300, 150);

        setLocationRelativeTo(null);
        setVisible(true);
        jb2.addActionListener(this);
        setResizable(false);//设置窗口不可以通过鼠标调节窗口大小
    }
    public int getuid(){
        int userID = 0;
        //获取输入的账号和密码
        try {
             userID = Integer.parseInt(String.valueOf(jt1.getText()));
        } catch (NumberFormatException e) {
            System.out.print("");
        }
        return userID;
    }
    public String getPassword(){
            String password = String.valueOf(pwd.getPassword());
            return password;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        int uid = getuid();
        String password = getPassword();
        JButton jButton =(JButton) e.getSource();
        if (jButton.equals(jb1)) {
            try {
                connection = dataBaseConnect.getConnection();//连接数据库
                //与数据库中的数据进行匹配
                statement = connection.createStatement();
                String sql1 = "SELECT userID FROM user";
                String sql2 = "SELECT userPassword FROM user WHERE userID=?";
                String sql3 = "SELECT userName FROM user WHERE userID =?";
                String sql4 = "SELECT userid from OnlineList ";//获取在线用户表
                String sql5 = "INSERT INTO OnlineList SET userid=?";
                PreparedStatement pp = connection.prepareStatement(sql5);

                Statement statement=connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql4);
                List<Integer> OnlineList = new ArrayList<>();

                while (resultSet.next()) {
                    OnlineList.add(resultSet.getInt("userid"));
                }

                PreparedStatement preparedStatement = connection.prepareStatement(sql2);
                preparedStatement.setInt(1, getuid());
                PreparedStatement p2 = connection.prepareStatement(sql3);
                p2.setInt(1, getuid());
                ResultSet rs = p2.executeQuery();
                while (rs.next()) {
                    username = rs.getString("userName");
                }
                List<Integer> uID = new ArrayList<>();
                List<String> upassword = new ArrayList<>();
                ResultSet rs1 = statement.executeQuery(sql1);
                while (rs1.next()) {
                    Integer id = rs1.getInt("userID");
                    uID.add(id);
                }
                ResultSet rs2 = preparedStatement.executeQuery();
                while (rs2.next()) {
                    String uPD = rs2.getString("userPassword");
                    upassword.add(uPD);
                }

                 if (uID.contains(uid)) {
                     if (OnlineList.contains(uid)) {
                         JOptionPane.showMessageDialog(this, "不可重复登录！");
                     }
                     else if (upassword.contains(password)) {
                         pp.setInt(1, uid);
                         int i = pp.executeUpdate();
                        new MainWindow(this).setVisible(true);//启动主窗口并使其显示
                        this.setVisible(false);

                     }else
                         JOptionPane.showMessageDialog(this, "密码错误请重新输入！");
                 }
                 else JOptionPane.showMessageDialog(this, "该用户未注册！");
            } catch (SQLException | IOException ex) {
                ex.printStackTrace();
            } catch (NumberFormatException eee) {
                System.out.print("");
            }
        }
        if (jButton.equals(jb2)){
            this.setEnabled(false);//设置禁用
            this.setModalExclusionType(Dialog.ModalExclusionType.NO_EXCLUDE);
            new RegisterWindow(this);
        }
    }

    public static void main(String[] args) {
        new LoginWindow();
    }
}
