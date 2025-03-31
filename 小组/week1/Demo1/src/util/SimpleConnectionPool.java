package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// 自定义数据库连接池类
public class SimpleConnectionPool {
    // 数据库连接的 URL，指定了要连接的 MySQL 数据库地址和数据库名
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/select_course_system";
    // 数据库用户名
    private static final String USER = "root";
    // 数据库密码
    private static final String PASSWORD = "Fkas514*mysql";
    // 初始连接池大小，即连接池初始化时创建的连接数量
    private static final int INITIAL_POOL_SIZE = 5;
    // 最大连接池大小，即连接池最多能容纳的连接数量
    private static final int MAX_POOL_SIZE = 10;

    // 泛型，连接池，存储空闲连接的列表，即当前未被使用的连接
    private static List<Connection> connectionPool = new ArrayList<>();
    // 存储正在使用的连接的列表
    private static List<Connection> usedConnections = new ArrayList<>();

    // 静态代码块，在类加载时执行，用于初始化连接池
    static {
        try {
            // 手动加载 MySQL JDBC 驱动，让 JVM 知道如何与 MySQL 数据库建立连接
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 初始化连接池，创建指定数量的初始连接
            for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
                connectionPool.add(createConnection());//把创建出来的连接都放到池里
            }
        } catch (ClassNotFoundException | SQLException e) {
            // 若加载驱动或创建连接时出现异常，打印异常信息
            e.printStackTrace();
        }
    }

    // 创建新的数据库连接的私有方法
    private static Connection createConnection() throws SQLException {
        // 使用 DriverManager 根据指定的 URL、用户名和密码创建一个新的数据库连接
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // 从连接池获取连接的公共方法，使用 synchronized 保证线程安全
    public static synchronized Connection getConnection() throws SQLException {
        // 若空闲连接列表为空
        if (connectionPool.isEmpty()) {
            // 检查正在使用的连接数量是否小于最大连接池大小
            if (usedConnections.size() < MAX_POOL_SIZE) {
                // 若小于最大连接池大小，创建一个新的连接并添加到空闲连接列表
                connectionPool.add(createConnection());
            } else {
                // 若达到最大连接池大小，抛出异常表示无法获取连接
                throw new SQLException("连接池已满，无法获取连接。");
            }
        }
        // 从空闲连接列表中移除最后一个连接
        Connection connection = connectionPool.remove(connectionPool.size() - 1);
        // 将该连接添加到正在使用的连接列表
        usedConnections.add(connection);
        // 返回获取到的连接
        return connection;
    }

    // 将连接返回到连接池的公共方法，使用 synchronized 保证线程安全
    public static synchronized void releaseConnection(Connection connection) {
        // 若传入的连接不为空
        if (connection != null) {
            // 从正在使用的连接列表中移除该连接
            usedConnections.remove(connection);
            // 将该连接添加到空闲连接列表
            connectionPool.add(connection);
        }
    }
}