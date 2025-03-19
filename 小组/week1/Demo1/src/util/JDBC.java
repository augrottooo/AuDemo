package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/*
public class JDBC {
    // 数据库连接 URL，指定数据库的地址、端口和名称。
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/select_course_system";
    // 数据库用户名
    private static final String USER = "root";
    // 数据库密码
    private static final String PASSWORD = "Fkas514*mysql";

    public static Connection getConnection() throws SQLException {
        /*
        try {
            // 显式加载 MySQL util.JDBC 驱动（注册驱动）
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        // 使用 DriverManager 获取数据库连接，如果连接成功，会返回一个 Connection 对象；如果连接失败，会抛出 SQLException 异常
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
*/

public class JDBC {
    public static Connection getConnection() throws SQLException {
        try {
            return Druid.getConnection();
        } catch (Exception e) {
            throw new SQLException("Failed to get database connection", e);
        }
    }
}