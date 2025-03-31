package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// 通用的CRUD工具类，封装了数据库的增删改查操作，方便复用
public class CrudUtil {
    /**
     * 执行查询操作
     * @param sql 要执行的 SQL 查询语句
     * @param params SQL 语句中的参数数组，可为 null
     * @param mapper 结果集映射器，用于将 ResultSet 中的数据映射为指定类型的对象
     * @param <T> 映射对象的类型
     * @return 包含映射后对象的列表
     */
    public static <T> List<T> executeQuery(String sql, Object[] params, ResultSetMapper<T> mapper) {
        // 用于存储查询结果的列表
        List<T> resultList = new ArrayList<>();
        // 数据库连接对象
        Connection connection = null;
        // 预编译的 SQL 语句对象
        PreparedStatement preparedStatement = null;
        // 结果集对象
        ResultSet resultSet = null;

        try {
            // 从自定义的数据库连接池获取数据库连接
            connection = SimpleConnectionPool.getConnection();
            // 使用获取到的连接对 SQL 语句进行预编译
            preparedStatement = connection.prepareStatement(sql);
            // 如果参数数组不为空
            if (params != null) {
                // 遍历参数数组，将参数设置到预编译的 SQL 语句中
                for (int i = 0; i < params.length; i++) {
                    preparedStatement.setObject(i + 1, params[i]);
                }
            }
            // 执行查询操作，得到结果集
            resultSet = preparedStatement.executeQuery();
            // 遍历结果集
            while (resultSet.next()) {
                // 使用映射器将当前行的数据映射为指定类型的对象，并添加到结果列表中
                resultList.add(mapper.map(resultSet));
            }
        } catch (SQLException e) {
            // 若发生 SQL 异常，打印异常堆栈信息
            e.printStackTrace();
        } finally {
            // 无论是否发生异常，都关闭数据库资源
            closeResources(resultSet, preparedStatement, connection);
        }
        // 返回查询结果列表
        return resultList;
    }

    /**
     * 执行更新操作（插入、更新、删除）
     * @param sql 要执行的 SQL 更新语句
     * @param params SQL 语句中的参数数组，可为 null
     * @return 受影响的行数
     * @throws SQLException 如果执行 SQL 语句时发生异常
     */
    public static int executeUpdate(String sql, Object[] params) throws SQLException {
        // 数据库连接对象
        Connection connection = null;
        // 预编译的 SQL 语句对象
        PreparedStatement preparedStatement = null;
        // 受影响的行数
        int rowsAffected = 0;

        try {
            // 从自定义的数据库连接池获取数据库连接
            connection = SimpleConnectionPool.getConnection();
            // 使用获取到的连接对 SQL 语句进行预编译
            preparedStatement = connection.prepareStatement(sql);
            // 如果参数数组不为空
            if (params != null) {
                // 遍历参数数组，将参数设置到预编译的 SQL 语句中
                for (int i = 0; i < params.length; i++) {
                    preparedStatement.setObject(i + 1, params[i]);
                }
            }
            // 执行更新操作，获取受影响的行数
            rowsAffected = preparedStatement.executeUpdate();
        } finally {
            // 无论是否发生异常，都关闭数据库资源
            closeResources(null, preparedStatement, connection);
        }
        // 返回受影响的行数
        return rowsAffected;
    }

    /**
     * 关闭数据库资源
     * @param resultSet 结果集对象
     * @param preparedStatement 预编译的 SQL 语句对象
     * @param connection 数据库连接对象
     */
    private static void closeResources(ResultSet resultSet, PreparedStatement preparedStatement, Connection connection) {
        try {
            // 如果结果集对象不为空，关闭结果集
            if (resultSet != null) {
                resultSet.close();
            }
            // 如果预编译的 SQL 语句对象不为空，关闭该对象
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            // 如果数据库连接对象不为空，将连接释放回连接池
            if (connection != null) {
                SimpleConnectionPool.releaseConnection(connection);
            }
        } catch (SQLException e) {
            // 若关闭资源时发生 SQL 异常，打印异常堆栈信息
            e.printStackTrace();
        }
    }

    /**
     * 结果集映射接口，用于将 ResultSet 中的数据映射为指定类型的对象
     * @param <T> 映射对象的类型
     */
    public interface ResultSetMapper<T> {
        /**
         * 将 ResultSet 中的当前行数据映射为指定类型的对象
         * @param resultSet 结果集对象
         * @return 映射后的对象
         * @throws SQLException 如果从结果集中获取数据时发生异常
         */
        T map(ResultSet resultSet) throws SQLException;
    }
}