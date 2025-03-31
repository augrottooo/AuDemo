package controller;

import util.CrudUtil;
import util.SimpleConnectionPool;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
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

        try {
            // 插入 users 表
            String userSql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            Object[] userParams = {username, password, role};
            int userRows = CrudUtil.executeUpdate(userSql, userParams);
            if (userRows <= 0) {
                return false;
            }

            // 如果是学生，还需要插入 students 表
            if (role.equals("1")) {
                String userIdSql = "SELECT id FROM users WHERE username = ?";
                Object[] userIdParams = {username};
                List<Integer> userIdList = CrudUtil.executeQuery(userIdSql, userIdParams, resultSet -> resultSet.getInt("id"));
                if (userIdList.isEmpty()) {
                    return false;
                }
                int userId = userIdList.get(0);

                String studentSql = "INSERT INTO students (user_id, stu_id, stu_name, phone) VALUES (?, ?, ?, ?)";
                Object[] studentParams = {userId, username, name, phoneNumber};
                int studentRows = CrudUtil.executeUpdate(studentSql, studentParams);
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

    // 用户登录方法
    public static String login(String username, String password) {
        String sql = "SELECT role FROM users WHERE username = ? AND password = ?";
        Object[] params = {username, password};
        List<String> roles = CrudUtil.executeQuery(sql, params, resultSet -> resultSet.getString("role"));
        return roles.isEmpty() ? null : roles.get(0);
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

    // 验证旧密码是否正确的方法
    private static boolean validatePassword(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";
        Object[] params = {username};
        List<String> storedPasswords = CrudUtil.executeQuery(sql, params, resultSet -> resultSet.getString("password"));
        return !storedPasswords.isEmpty() && storedPasswords.get(0).equals(password);
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
                "JOIN users u ON s.user_id = u.id " +
                "WHERE u.username = ?";
        Object[] params = {username};
        List<String> studentInfo = CrudUtil.executeQuery(sql, params, resultSet -> {
            StringBuilder info = new StringBuilder();
            info.append("===== 学生基本信息 =====").append("\n");
            info.append("学号: ").append(resultSet.getString("stu_id")).append("\n");
            info.append("姓名: ").append(resultSet.getString("stu_name")).append("\n");
            info.append("手机号: ").append(resultSet.getString("phone")).append("\n");
            return info.toString();
        });
        if (studentInfo.isEmpty()) {
            System.out.println("未找到该学生的信息。");
        } else {
            System.out.println(studentInfo.get(0));
        }
    }

    // 查询管理员信息
    public static void queryAdminInfo(String username) {
        String sql = "SELECT username, role FROM users WHERE username = ?";
        Object[] params = {username};
        List<String> adminInfo = CrudUtil.executeQuery(sql, params, resultSet -> {
            StringBuilder info = new StringBuilder();
            info.append("===== 管理员基本信息 =====").append("\n");
            info.append("用户名: ").append(resultSet.getString("username")).append("\n");
            info.append("角色: ").append(resultSet.getString("role")).append("\n");
            return info.toString();
        });
        if (adminInfo.isEmpty()) {
            System.out.println("未找到该管理员的信息。");
        } else {
            System.out.println(adminInfo.get(0));
        }
    }
}