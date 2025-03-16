package controller;

import model.Course;
import util.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Student {
    // 学生菜单
    public static Object stuMenu(Scanner scanner, String username) {
        while (true) {
            System.out.println("===== 学生菜单 =====");
            System.out.println("1. 查看可选课程");
            System.out.println("2. 选择课程");
            System.out.println("3. 退选课程");
            System.out.println("4. 查看已选课程");
            System.out.println("5. 修改密码");
            System.out.println("6. 查看个人信息");
            System.out.println("7. 退出");
            System.out.print("请选择操作(输入1-7): ");
            // 读取用户选择的操作
            int choice = scanner.nextInt();
            // 消耗换行符，清除缓冲区
            scanner.nextLine();
            switch (choice) {
                case 1:
                    //调用查询可选课程方法
                    getAvailableCourses();
                    break;
                case 2:
                    // 调用选课方法
                    selectCourse(scanner);
                    break;
                case 3:
                    // 调用退课方法
                    dropCourse(scanner);
                    break;
                case 4:
                    //调用打印已选课程方法
                    getchoseCourses(username);
                    break;
                case 5:
                    //调用修改密码方法
                    Main.changePasswordMenu(scanner, username);
                    break;
                case 6:
                    //调用查看个人信息方法
                    UserService.queryStudentInfo(username);
                    break;
                case 7:
                    System.out.println("退出登录，再见！");
                    return false;
                default:
                    // 输入无效选项时提示用户重新输入
                    System.out.println("无效的选择，请重新输入。");
            }
        }
    }


    // 查看可选课程方法
    public static void getAvailableCourses() {
        String sql = "SELECT * FROM courses WHERE sumnum > nownum"; // 查询未满的课程
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
            // 打印可选课程
            if (courses.isEmpty()) {
                System.out.println("当前没有可选课程。");
            } else {
                System.out.println("===== 可选课程 =====");
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


    // 选课方法
    public static void selectCourse(Scanner scanner) {
        System.out.println("===== 选择课程 =====");
        System.out.print("请输入您的学号: ");
        String stuId = scanner.nextLine(); // 读取学号
        System.out.print("请输入课程编号: ");
        String courseId = scanner.nextLine(); // 读取课程编号

        // 获取学生姓名
        String stuName = getStudentName(stuId);
        if (stuName == null) {
            System.out.println("未找到学生信息，请检查学号是否正确。");
            return;
        }

        // 检查是否已选该课程
        if (isCourseSelected(stuId, courseId)) {
            System.out.println("您已选择该课程，无需重复选择。");
            return;
        }
        // 检查课程是否已满
        if (isCourseFull(courseId)) {
            System.out.println("该课程已满，无法选择。");
            return;
        }
        // 插入选课记录
        if (insertStudentCourse(stuId, stuName, courseId)) {
            System.out.println("选课成功！");
        } else {
            System.out.println("选课失败，请重试。");
        }
    }

    //获取学生姓名方法
    private static String getStudentName(String stuId) {
        String sql = "SELECT stu_name FROM students WHERE stu_id = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, stuId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("stu_name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 检查学生是否已选该课程
    private static boolean isCourseSelected(String stuId, String courseId) {
        String sql = "SELECT * FROM student_courses WHERE stu_id = ? AND course_id = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, stuId);
            pstmt.setString(2, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 检查课程是否已满
    private static boolean isCourseFull(String courseId) {
        String sql = "SELECT nownum, sumnum FROM courses WHERE course_id = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int nowNum = rs.getInt("nownum");
                    int sumNum = rs.getInt("sumnum");
                    return nowNum >= sumNum;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    // 插入选课记录
    private static boolean insertStudentCourse(String stuId, String stuName, String courseId) {
        String sql1 = "INSERT INTO student_courses (stu_id, stu_name, course_id) VALUES (?, ?, ?)";
        String sql2 = "UPDATE courses SET nownum = nownum + 1 WHERE course_id = ?";
        try (Connection conn = JDBC.getConnection()) {
            // 开启事务
            conn.setAutoCommit(false);
            // 插入选课记录
            try (PreparedStatement pstmt1 = conn.prepareStatement(sql1)) {
                pstmt1.setString(1, stuId);
                pstmt1.setString(2, stuName);
                pstmt1.setString(3, courseId);
                pstmt1.executeUpdate();
            }
            // 更新课程已选人数
            try (PreparedStatement pstmt2 = conn.prepareStatement(sql2)) {
                pstmt2.setString(1, courseId);
                pstmt2.executeUpdate();
            }
            // 提交事务
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // 退选课程
    public static void dropCourse(Scanner scanner) {
        System.out.println("===== 退选课程 =====");
        System.out.print("请输入您的学号: ");
        String stuId = scanner.nextLine(); // 读取学号
        System.out.print("请输入课程编号: ");
        String courseId = scanner.nextLine(); // 读取课程编号
        // 检查是否已选该课程
        if (!isCourseSelected(stuId, courseId)) {// 和选课时共用一个方法判断
            System.out.println("您未选择该课程，无法退选。");
            return;
        }
        // 删除选课记录
        if (deleteStudentCourse(stuId, courseId)) {
            System.out.println("退选成功！");
        } else {
            System.out.println("退选失败，请重试。");
        }
    }

    // 删除选课记录
    private static boolean deleteStudentCourse(String stuId, String courseId) {
        String sql1 = "DELETE FROM student_courses WHERE stu_id = ? AND course_id = ?";
        String sql2 = "UPDATE courses SET nownum = nownum - 1 WHERE course_id = ?";
        try (Connection conn = JDBC.getConnection()) {
            // 开启事务
            conn.setAutoCommit(false);
            // 删除选课记录
            try (PreparedStatement pstmt1 = conn.prepareStatement(sql1)) {
                pstmt1.setString(1, stuId);
                pstmt1.setString(2, courseId);
                pstmt1.executeUpdate();
            }
            // 更新课程已选人数
            try (PreparedStatement pstmt2 = conn.prepareStatement(sql2)) {
                pstmt2.setString(1, courseId);
                pstmt2.executeUpdate();
            }
            // 提交事务
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    //打印已选课程方法
    public static void getchoseCourses(String username) {
        // SQL 查询,通过 students 表找到学生的 stu_id,通过 student_courses 表找到学生已选的课程。
        //通过 courses 表获取课程的详细信息, 使用子查询根据 username 找到对应的 stu_id。
        String sql = "SELECT c.course_id, c.course_name, c.credit, c.teacher, c.course_time, c.course_place " +
                "FROM students s " +
                "JOIN student_courses sc ON s.stu_id = sc.stu_id " +
                "JOIN courses c ON sc.course_id = c.course_id " +
                "WHERE s.stu_id = (SELECT stu_id FROM students WHERE user_id = (SELECT id FROM users WHERE username = ?))";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // 设置 SQL 参数
            pstmt.setString(1, username);
            // 执行查询
            try (ResultSet rs = pstmt.executeQuery()) {
                boolean hasCourses = false;
                System.out.println("===== 已选课程 =====");
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
                    System.out.println("您当前没有选择任何课程。");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
