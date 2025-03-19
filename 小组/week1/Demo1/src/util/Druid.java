package util;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import javax.sql.DataSource;
import java.io.FileInputStream;
import java.sql.Connection;
import java.util.Properties;
/*
public class Druid {
    public static void main(String[] args) throws Exception {
        // 导入jar包
        // 定义配置文件
        // 加载配置文件
        Properties prop = new Properties();
        prop.load(new FileInputStream("Demo1/src/druid.properties"));

        // 获取连接池对象
        DataSource dataSourse = DruidDataSourceFactory.createDataSource(prop);
        // 获取数据库连接
        Connection connection = dataSourse.getConnection();

        System.out.println(connection);

        //System.out.println(System.getProperty("user.dir"));
    }
}*/

public class Druid {
    private static DataSource dataSource;

    static {
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream("Demo1/src/util/druid.properties"));
            dataSource = DruidDataSourceFactory.createDataSource(prop);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws Exception {
        return dataSource.getConnection();
    }
}