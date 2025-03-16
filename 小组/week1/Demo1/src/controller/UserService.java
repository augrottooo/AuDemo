package controller;

import util.JDBC;

import java.sql.*;
import java.util.regex.Pattern;

// 公共功能：注册、登录、改密码、查自己信息
public class UserService {
    // 手机号码格式验证正则表达式，要求为 11 位数字
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^[0-9]{11}$");
    // 密码格式验证正则表达式，要求为 6 位数字
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[0-9]{6}$");

    // 用户注册方法
    public static boolean registerUser(String username, String password, String role, String name, String phoneNumber) {
        // 检查密码是否符合格式要求
        if (!isValidPassword(password)) {
            return false;
        }
        // 如果是学生，才验证手机号
        if (role.equals("1") && !isValidPhoneNumber(phoneNumber)) {
            return false;
        }
        Connection conn = null;
        try {
            // 获取数据库连接
            conn = JDBC.getConnection();
            // 关闭自动提交，开启手动事务管理
            conn.setAutoCommit(false);

            // 插入 users 表
            String userSql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            try (PreparedStatement userPstmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) { // 获取自增主键
                // 设置 users 表的参数
                userPstmt.setString(1, username);
                userPstmt.setString(2, password);
                userPstmt.setString(3, role);

                // 执行插入操作
                int userRows = userPstmt.executeUpdate();
                if (userRows <= 0) {
                    // 插入 users 表失败，回滚事务
                    conn.rollback();
                    return false;
                }

                // 如果是学生，还需要插入 students 表
                if (role.equals("1")) { // 假设 "1" 表示学生
                    // 获取刚插入的用户的 ID
                    try (ResultSet generatedKeys = userPstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int userId = generatedKeys.getInt(1); // 获取自增主键的值

                            // 插入 students 表
                            String studentSql = "INSERT INTO students (user_id, stu_id, stu_name, phone) VALUES (?, ?, ?, ?)";
                            try (PreparedStatement studentPstmt = conn.prepareStatement(studentSql)) {
                                studentPstmt.setInt(1, userId); // 设置 user_id
                                studentPstmt.setString(2, username); // 设置学号
                                studentPstmt.setString(3, name); // 设置姓名
                                studentPstmt.setString(4, phoneNumber); // 设置手机号

                                // 执行插入操作
                                int studentRows = studentPstmt.executeUpdate();
                                if (studentRows <= 0) {
                                    // 插入 students 表失败，回滚事务
                                    conn.rollback();
                                    return false;
                                }
                            }
                        } else {
                            // 获取自增主键失败，回滚事务
                            conn.rollback();
                            return false;
                        }
                    }
                }

                // 所有事务成功后，提交事务
                conn.commit();
                return true;
            }
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    // 出现异常，回滚事务
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {// 资源管理和状态恢复
            try {
                if (conn != null) {
                    // 恢复自动提交模式
                    conn.setAutoCommit(true);
                    // 关闭连接
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //用户登录方法
    public static String login(String username, String password) {
        String sql = "SELECT role FROM users WHERE username = ? AND password = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (SQLException e) {
            // 打印异常信息
            e.printStackTrace();
        }
        return null;
    }

    // 用户修改密码方法
    public static boolean changePassword(String username, String oldPassword, String newPassword) {
        // 检查旧密码是否正确
        if (!validatePassword(username, oldPassword)) {
            System.out.println("旧密码错误。");
            return false;
        }

        // 检查新密码是否符合格式要求
        if (!isValidPassword(newPassword)) {
            System.out.println("新密码不符合格式要求。");
            return false;
        }

        // 更新密码
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);

            int rows = pstmt.executeUpdate();
            return rows > 0; // 返回是否更新成功
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 验证旧密码是否正确的方法
    private static boolean validatePassword(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    return storedPassword.equals(password); // 比较密码
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 验证手机号码格式的方法
    private static boolean isValidPhoneNumber(String phoneNumber) {
        // 使用正则表达式验证手机号码格式
        return PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches();
    }

    // 验证密码格式的方法
    private static boolean isValidPassword(String password) {
        // 使用正则表达式验证密码格式
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    // 查询学生信息
    public static void queryStudentInfo(String username) {
        String sql = "SELECT s.stu_id, s.stu_name, s.phone " +
                "FROM students s " +
                "JOIN users u ON s.user_id = u.id " +// 只返回两个表中匹配的行
                "WHERE u.username = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("===== 学生基本信息 =====");
                    System.out.println("学号: " + rs.getString("stu_id"));
                    System.out.println("姓名: " + rs.getString("stu_name"));
                    System.out.println("手机号: " + rs.getString("phone"));
                } else {
                    System.out.println("未找到该学生的信息。");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 查询管理员信息
    public static void queryAdminInfo(String username) {
        String sql = "SELECT username, role FROM users WHERE username = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("===== 管理员基本信息 =====");
                    System.out.println("用户名: " + rs.getString("username"));
                    System.out.println("角色: " + rs.getString("role"));
                } else {
                    System.out.println("未找到该管理员的信息。");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
