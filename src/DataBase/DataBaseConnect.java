package DataBase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DataBaseConnect {
    Connection connection;

    //配置类，用于调用我们在sqlserver.properties中写的数据库连接信息
        private static Properties p = null;
        static {
            try {
                p = new Properties();
                //加载配置文件
                p.load(new FileInputStream("src/sqlserver.properties"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //获取键对应的值
        public static String getValue(String key) {
            return  p.get(key).toString();
        }
    public Connection getConnection(){
        try {
            Class.forName(this.getValue("driver"));
            connection = DriverManager.getConnection(this.getValue("url"),this.getValue("username"),this.getValue("password"));
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
        return connection;
    }
}
