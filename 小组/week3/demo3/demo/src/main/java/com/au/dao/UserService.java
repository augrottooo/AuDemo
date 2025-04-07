package com.au.dao;

import com.au.util.CrudUtil;
import com.au.util.SimpleConnectionPool;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 用户服务类
 * 提供用户相关操作：注册、登录、修改密码、查询信息等
 */
public class UserService {
    // 手机号码格式验证正则表达式，要求为 11 位数字
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^[0-9]{11}$");
    // 密码格式验证正则表达式，要求为 6 位数字
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[0-9]{6}$");

    /**
     * 用户注册方法
     * @param username 用户名
     * @param password 密码
     * @param role 用户角色（1-学生，其他-管理员）
     * @param name 真实姓名（学生需要）
     * @param phoneNumber 手机号码（学生需要）
     * @return 注册是否成功
     */
    public static boolean registerUser(String username, String password, String role, String name, String phoneNumber) {
        // 验证密码格式是否符合要求
        if (!isValidPassword(password)) {
            return false;
        }

        // 如果是学生角色，验证手机号码格式
        if (role.equals("1") && !isValidPhoneNumber(phoneNumber)) {
            return false;
        }

        try {
            // 1. 先插入用户表(users)
            String userSql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
             // 创建一个 Object 类型的数组，用于存储 SQL 语句中的参数值
            Object[] userParams = {username, password, role};
            int userRows = CrudUtil.executeUpdate(userSql, userParams);

            // 如果用户表插入失败，直接返回false
            if (userRows <= 0) {
                return false;
            }

            // 2. 如果是学生角色，还需要插入学生表(students)
            if (role.equals("1")) {
                // 先查询刚插入的用户ID
                String userIdSql = "SELECT id FROM users WHERE username = ?";
                Object[] userIdParams = {username};
                List<Integer> userIdList = CrudUtil.executeQuery(
                        userIdSql,
                        userIdParams,
                        resultSet -> resultSet.getInt("id")
                );

                // 如果查询不到用户ID，返回false
                if (userIdList.isEmpty()) {
                    return false;
                }

                int userId = userIdList.get(0);

                // 插入学生信息
                String studentSql = "INSERT INTO students (user_id, stu_id, stu_name, phone) VALUES (?, ?, ?, ?)";
                Object[] studentParams = {userId, username, name, phoneNumber};
                int studentRows = CrudUtil.executeUpdate(studentSql, studentParams);

                // 如果学生表插入失败，返回false
                if (studentRows <= 0) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 用户登录验证
     * @param username 用户名
     * @param password 密码
     * @return 用户角色（登录成功返回角色，失败返回null）
     */
    public static String login(String username, String password) {
        String sql = "SELECT role FROM users WHERE username = ? AND password = ?";
        Object[] params = {username, password};
        List<String> roles = CrudUtil.executeQuery(
                sql,
                params,
                resultSet -> resultSet.getString("role")
        );
        return roles.isEmpty() ? null : roles.get(0);
    }

    /**
     * 修改用户密码
     * @param username 用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改是否成功
     */
    public static boolean changePassword(String username, String oldPassword, String newPassword) {
        // 1. 验证旧密码是否正确
        if (!validatePassword(username, oldPassword)) {
            System.out.println("旧密码错误。");
            return false;
        }

        // 2. 验证新密码格式是否符合要求
        if (!isValidPassword(newPassword)) {
            System.out.println("新密码不符合格式要求。");
            return false;
        }

        // 3. 更新密码
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        Object[] params = {newPassword, username};
        try {
            int rows = CrudUtil.executeUpdate(sql, params);
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("数据库操作出现异常，请稍后重试。");
            return false;
        }
    }

    /**
     * 验证密码是否正确（内部方法）
     * @param username 用户名
     * @param password 待验证密码
     * @return 密码是否正确
     */
    private static boolean validatePassword(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";
        Object[] params = {username};
        List<String> storedPasswords = CrudUtil.executeQuery(
                sql,
                params,
                resultSet -> resultSet.getString("password")
        );
        return !storedPasswords.isEmpty() && storedPasswords.get(0).equals(password);
    }

    /**
     * 验证手机号码格式（内部方法）
     * @param phoneNumber 手机号码
     * @return 格式是否正确
     */
    private static boolean isValidPhoneNumber(String phoneNumber) {
        return PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches();
    }

    /**
     * 验证密码格式（内部方法）
     * @param password 密码
     * @return 格式是否正确
     */
    private static boolean isValidPassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * 查询学生信息
     * @param username 用户名
     */
    public static void queryStudentInfo(String username) {
        String sql = "SELECT s.stu_id, s.stu_name, s.phone " +
                "FROM students s " +
                "JOIN users u ON s.user_id = u.id " +
                "WHERE u.username = ?";
        Object[] params = {username};

        List<String> studentInfo = CrudUtil.executeQuery(sql, params, resultSet -> {
            // 构建学生信息字符串
            StringBuilder info = new StringBuilder();
            info.append("===== 学生基本信息 =====").append("\n");
            info.append("学号: ").append(resultSet.getString("stu_id")).append("\n");
            info.append("姓名: ").append(resultSet.getString("stu_name")).append("\n");
            info.append("手机号: ").append(resultSet.getString("phone")).append("\n");
            return info.toString();
        });

        // 输出查询结果
        if (studentInfo.isEmpty()) {
            System.out.println("未找到该学生的信息。");
        } else {
            System.out.println(studentInfo.get(0));
        }
    }

    /**
     * 查询管理员信息
     * @param username 用户名
     */
    public static void queryAdminInfo(String username) {
        String sql = "SELECT username, role FROM users WHERE username = ?";
        Object[] params = {username};

        List<String> adminInfo = CrudUtil.executeQuery(sql, params, resultSet -> {
            // 构建管理员信息字符串
            StringBuilder info = new StringBuilder();
            info.append("===== 管理员基本信息 =====").append("\n");
            info.append("用户名: ").append(resultSet.getString("username")).append("\n");
            info.append("角色: ").append(resultSet.getString("role")).append("\n");
            return info.toString();
        });

        // 输出查询结果
        if (adminInfo.isEmpty()) {
            System.out.println("未找到该管理员的信息。");
        } else {
            System.out.println(adminInfo.get(0));
        }
    }
}