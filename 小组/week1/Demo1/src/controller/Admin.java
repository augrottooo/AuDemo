package controller;

import model.Course;
import model.Stu;
import util.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Admin {
    // 管理员菜单
    public static boolean adminMenu(Scanner scanner, String username) {
        while (true) {
            System.out.println("===== 管理员菜单 =====");
            System.out.println("1. 查询所有学生");
            System.out.println("2. 修改学生手机号");
            System.out.println("3. 查询所有课程");
            System.out.println("4. 修改课程信息");
            System.out.println("5. 查询某课程的学生名单");
            System.out.println("6. 查询某学生的选课情况");
            System.out.println("7. 修改密码");
            System.out.println("8. 查看个人信息");
            System.out.println("9. 退出");
            System.out.print("请选择操作(输入1-9): ");
            // 读取用户选择的操作
            int choice = scanner.nextInt();
            // 消耗换行符，清除缓冲区
            scanner.nextLine();
            switch (choice) {
                case 1:
                    //调用查询所有学生方法
                    searchAllStudents(scanner);
                    break;
                case 2:
                    // 调用修改学生手机号方法
                    updateStudentPhone(scanner);
                    break;
                case 3:
                    // 调用查询所有课程方法
                    getAllCourses();
                    break;
                case 4:
                    //调用修改课程信息方法
                    updateCourseInfo(scanner);
                    break;
                case 5:
                    //调用查询某课程的学生名单方法
                    searchCourseStudents(scanner);
                    break;
                case 6:
                    //调用查询某学生的选课情况方法
                    searchStudentCourses(scanner);
                    break;
                case 7:
                    //调用修改密码方法
                    Main.changePasswordMenu(scanner, username);
                    break;
                case 8:
                    //调用查看个人信息方法
                    UserService.queryAdminInfo(username);
                    break;
                case 9:
                    System.out.println("退出登录，再见！");
                    return true;
                default:
                    // 输入无效选项时提示用户重新输入
                    System.out.println("无效的选择，请重新输入。");
            }
        }
    }

    // 查询所有学生方法
    public static void searchAllStudents(Scanner scanner) {
        String sql = "SELECT s.stu_id, s.stu_name, s.phone, u.password " +
                "FROM students s " +
                "JOIN users u ON s.user_id = u.id"; // 查询所有学生及其密码
        List<Stu> students = new ArrayList<>();
        try (Connection conn = JDBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                // 使用构造函数简化对象创建
                Stu student = new Stu(
                        rs.getString("stu_id"),
                        rs.getString("stu_name"),
                        rs.getString("phone"),
                        rs.getString("password")
                );
                students.add(student);
            }
            // 打印所有学生
            if (students.isEmpty()) {
                System.out.println("当前没有学生。");
            } else {
                System.out.println("===== 所有学生 =====");
                for (Stu student : students) {
                    System.out.println("学生姓名: " + student.getName());
                    System.out.println("学生学号: " + student.getStuId());
                    System.out.println("手机号: " + student.getPhoneNumber());
                    System.out.println("密码: " + student.getPassword());
                    System.out.println("-----------------------------");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 修改学生手机号
    public static void updateStudentPhone(Scanner scanner) {
        System.out.println("===== 修改学生手机号 =====");
        System.out.print("请输入学生的学号: ");
        String stuId = scanner.nextLine(); // 读取学号
        System.out.print("请输入新的手机号: ");
        String newPhone = scanner.nextLine(); // 读取新手机号

        // 调用数据库更新方法
        if (updatePhoneInDatabase(stuId, newPhone)) {
            System.out.println("手机号修改成功！");
        } else {
            System.out.println("手机号修改失败，请检查学号是否正确。");
        }
    }

    // 在数据库中更新学生的手机号
    private static boolean updatePhoneInDatabase(String stuId, String newPhone) {
        String sql = "UPDATE students SET phone = ? WHERE stu_id = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPhone); // 设置新手机号
            pstmt.setString(2, stuId); // 设置学号

            int rows = pstmt.executeUpdate();
            return rows > 0; // 返回是否更新成功
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 查询所有课程方法
    public static void getAllCourses() {
        String sql = "SELECT * FROM courses "; // 查询所有课程
        List<Course> courses = new ArrayList<>();
        try (Connection conn = JDBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                // 使用构造函数简化对象创建
                Course course = new Course(
                        rs.getInt("id"),
                        rs.getString("course_name"),
                        rs.getInt("credit"),
                        rs.getInt("nownum"),
                        rs.getInt("sumnum"),
                        rs.getString("teacher"),
                        rs.getString("course_time"),
                        rs.getString("course_place"),
                        rs.getString("course_id")
                );
                courses.add(course);
            }
            // 打印课程
            if (courses.isEmpty()) {
                System.out.println("当前没有课程。");
            } else {
                System.out.println("===== 所有课程 =====");
                for (Course course : courses) {
                    System.out.println("课程编号: " + course.getCourseId());
                    System.out.println("课程名称: " + course.getCourseName());
                    System.out.println("学分: " + course.getCredit());
                    System.out.println("授课教师: " + course.getTeacher());
                    System.out.println("上课时间: " + course.getTime());
                    System.out.println("上课地点: " + course.getPlace());
                    System.out.println("已选人数: " + course.getNowNum() + "/" + course.getSumNum());
                    System.out.println("-----------------------------");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //修改课程信息方法
    public static void updateCourseInfo(Scanner scanner) {
        System.out.println("===== 修改课程信息 =====");
        System.out.print("请输入课程编号: ");
        String courseId = scanner.nextLine(); // 读取课程编号
        System.out.print("请输入新的学分（留空则不修改）: ");
        String creditInput = scanner.nextLine();
        Integer credit = creditInput.isEmpty() ? null : Integer.parseInt(creditInput);
        System.out.print("请输入新的授课教师（留空则不修改）: ");
        String teacher = scanner.nextLine();
        System.out.print("请输入新的上课时间（留空则不修改）: ");
        String courseTime = scanner.nextLine();
        System.out.print("请输入新的上课地点（留空则不修改）: ");
        String coursePlace = scanner.nextLine();
        // 更新课程信息
        if (updateCourseInDatabase(courseId, credit, teacher, courseTime, coursePlace)) {
            System.out.println("课程信息修改成功！");
        } else {
            System.out.println("课程信息修改失败，请检查课程编号是否正确。");
        }
    }

    // 在数据库中更新课程信息
    private static boolean updateCourseInDatabase(String courseId, Integer credit, String teacher, String courseTime, String coursePlace) {
        StringBuilder sql = new StringBuilder("UPDATE courses SET ");
        boolean hasUpdate = false;
        if (credit != null) {
            sql.append("credit = ?, ");
            hasUpdate = true;
        }
        if (!teacher.isEmpty()) {
            sql.append("teacher = ?, ");
            hasUpdate = true;
        }
        if (!courseTime.isEmpty()) {
            sql.append("course_time = ?, ");
            hasUpdate = true;
        }
        if (!coursePlace.isEmpty()) {
            sql.append("course_place = ?, ");
            hasUpdate = true;
        }
        if (!hasUpdate) {
            System.out.println("未提供任何修改信息。");
            return false;
        }

        // 移除最后一个逗号和空格
        sql.setLength(sql.length() - 2);
        sql.append(" WHERE course_id = ?");

        try (Connection conn = JDBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;

            if (credit != null) {
                pstmt.setInt(paramIndex++, credit);
            }
            if (!teacher.isEmpty()) {
                pstmt.setString(paramIndex++, teacher);
            }
            if (!courseTime.isEmpty()) {
                pstmt.setString(paramIndex++, courseTime);
            }
            if (!coursePlace.isEmpty()) {
                pstmt.setString(paramIndex++, coursePlace);
            }

            pstmt.setString(paramIndex, courseId);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    //查询某课程的学生名单方法
    public static void searchCourseStudents(Scanner scanner) {
        System.out.println("===== 查询某课程的学生名单 =====");
        System.out.print("请输入课程编号: ");
        String courseId = scanner.nextLine(); // 读取课程编号
        String sql = "SELECT s.stu_id, s.stu_name, s.phone " +
                "FROM students s " +
                "JOIN student_courses sc ON s.stu_id = sc.stu_id " +
                "JOIN courses c ON sc.course_id = c.course_id " +
                "WHERE c.course_id = ?";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                boolean hasStudents = false;
                System.out.println("===== 选课学生名单 =====");
                while (rs.next()) {
                    hasStudents = true;
                    System.out.println("学生学号: " + rs.getString("stu_id"));
                    System.out.println("学生姓名: " + rs.getString("stu_name"));
                    System.out.println("手机号: " + rs.getString("phone"));
                    System.out.println("-----------------------------");
                }
                if (!hasStudents) {
                    System.out.println("该课程暂无学生选课。");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //查询某学生的选课情况方法
    public static void searchStudentCourses(Scanner scanner) {
        System.out.println("===== 查询某学生的选课情况 =====");
        System.out.print("请输入学生学号: ");
        String stuId = scanner.nextLine(); // 读取学生学号
        String sql = "SELECT c.course_id, c.course_name, c.credit, c.teacher, c.course_time, c.course_place " +
                "FROM courses c " +
                "JOIN student_courses sc ON c.course_id = sc.course_id " +
                "JOIN students s ON sc.stu_id = s.stu_id " +
                "WHERE s.stu_id = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, stuId);

            try (ResultSet rs = pstmt.executeQuery()) {
                boolean hasCourses = false;
                System.out.println("===== 学生选课情况 =====");
                while (rs.next()) {
                    hasCourses = true;
                    System.out.println("课程编号: " + rs.getString("course_id"));
                    System.out.println("课程名称: " + rs.getString("course_name"));
                    System.out.println("学分: " + rs.getInt("credit"));
                    System.out.println("授课教师: " + rs.getString("teacher"));
                    System.out.println("上课时间: " + rs.getString("course_time"));
                    System.out.println("上课地点: " + rs.getString("course_place"));
                    System.out.println("-----------------------------");
                }
                if (!hasCourses) {
                    System.out.println("该学生暂无选课。");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}