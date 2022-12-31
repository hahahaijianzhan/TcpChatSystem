package ClientFrame;

import DataBase.DataBaseConnect;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//注册窗口
public class RegisterWindow extends JFrame implements ActionListener {
    JLabel jl1,jl2,jl3,jl4,jl5;
    JTextField jt1,jt2,jt3,jt4;
    JButton jb;
    JPanel jp;
    public RegisterWindow(final LoginWindow lg){
        //设置窗口左上角图标
        setIconImage(new ImageIcon("src/image/QQ_64.png").getImage());
        setTitle("注册");
            jl1 = new JLabel("账号");
            jl2 = new JLabel("密码");
            jl3 = new JLabel("确认密码");
            jl4 = new JLabel("昵称");
            jl5 = new JLabel("");
            jt1 = new JTextField("");
            jt2 = new JTextField("");
            jt3 = new JTextField("");
            jt4 = new JTextField("");
            jb = new JButton("确认");
            jb.addActionListener(this);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            jp = new JPanel();
            //运用GroupLayout将组件整齐放入界面
            GroupLayout layout = new GroupLayout(jp);
            jp.setLayout(layout);

            layout.setAutoCreateGaps(true);
            layout.setAutoCreateContainerGaps(true);

            //水平组
            GroupLayout.ParallelGroup hParalGp1 = layout.createParallelGroup().addComponent(jl4).addComponent(jl1).addComponent(jl2).addComponent(jl3);
            GroupLayout.ParallelGroup hParalGp2 = layout.createParallelGroup().addComponent(jt4).addComponent(jt1).addComponent(jt2).addComponent(jt3);
            GroupLayout.SequentialGroup hSeqGp1 = layout.createSequentialGroup().addGroup(hParalGp1).addGroup(hParalGp2);
            GroupLayout.SequentialGroup hSeqGp2 = layout.createSequentialGroup().addGap(150).addComponent(jb).addComponent(jl5);
            GroupLayout.ParallelGroup hPralGroup = layout.createParallelGroup().addGroup(hSeqGp1).addGroup(hSeqGp2);
            layout.setHorizontalGroup(hPralGroup);

            //垂直组
        GroupLayout.ParallelGroup vParalGp4 = layout.createParallelGroup().addComponent(jl4).addComponent(jt4);
            GroupLayout.ParallelGroup vParalGp1 = layout.createParallelGroup().addComponent(jl1).addComponent(jt1);
            GroupLayout.ParallelGroup vParalGp2 = layout.createParallelGroup().addComponent(jl2).addComponent(jt2);
            GroupLayout.ParallelGroup vParalGp3 = layout.createParallelGroup().addComponent(jl3).addComponent(jt3);
            GroupLayout.ParallelGroup vParalGp5 = layout.createParallelGroup().addComponent(jb);
            GroupLayout.ParallelGroup vParalGp6 = layout.createParallelGroup().addComponent(jl5);
            GroupLayout.SequentialGroup vSeqGroup = layout.createSequentialGroup().addGroup(vParalGp4).addGroup(vParalGp1).addGroup(vParalGp2).addGroup(vParalGp3).addGroup(vParalGp5).addGroup(vParalGp6);
            layout.setVerticalGroup(vSeqGroup);

            add(jp);
            setSize(400, 200);
            setLocationRelativeTo(null);
            setVisible(true);
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);
                    //设置启用
                    lg.setEnabled(true);
                }
            });
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        List<Integer> list = new ArrayList<>();
        Connection connection = new DataBaseConnect().getConnection();
        try {
            Statement statement = connection.createStatement();
            String sql = "SELECT userID FROM user";
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                list.add(rs.getInt("userID"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        //使用正则表达式校验账号和密码的格式
        String regex1= "^[0-9]*[1-9][0-9]*$";
        String regex2 = "^[A-Za-z0-9]+$";
        boolean a = this.jt1.getText().matches(regex1);
        boolean b = this.jt2.getText().matches(regex2);
        boolean c = this.jt3.getText().matches(regex2);
        try {
            if (!Objects.equals(jt1.getText(), "") & !Objects.equals(jt2.getText(), "") & !Objects.equals(jt3.getText(), "")) {
                if (jt4.getText().equals("")) {
                    jl5.setText("昵称不能为空哦");
                } else if (list.contains(Integer.parseInt(jt1.getText()))) {
                    jl5.setText("该账号已被注册！");
                } else if (!a) {
                    jl5.setText("账号只能为纯数字！");
                } else if (!b || !c) {
                    jl5.setText("密码只能由数字和字母组成");
                } else if (!(jt2.getText().equals(jt3.getText()))) {
                    jl5.setText("两次输入的密码必须一致！");
                } else {
                    jl5.setText("注册成功！");
                    String sql1 = "INSERT INTO user (userID,userPassword,userName) VALUES (?,?,?)";
                    try {
                        PreparedStatement p = connection.prepareStatement(sql1);
                        p.setInt(1, Integer.parseInt(jt1.getText()));
                        p.setString(2, jt2.getText());
                        p.setString(3, jt4.getText());
                        p.executeUpdate();

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } finally {
                        try {
                            connection.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        } catch (NumberFormatException exception) {
            jl5.setText("账号只能为纯数字！");
        }
    }
}
