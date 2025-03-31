package controller;

import model.Course;
import model.Stu;
import util.CrudUtil;

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
        List<Stu> students = CrudUtil.executeQuery(sql, null, resultSet -> {
            return new Stu(
                    resultSet.getString("stu_id"),
                    resultSet.getString("stu_name"),
                    resultSet.getString("phone"),
                    resultSet.getString("password")
            );
        });
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
    }

    // 修改学生手机号
    public static void updateStudentPhone(Scanner scanner) {
        System.out.println("===== 修改学生手机号 =====");
        System.out.print("请输入学生的学号: ");
        String stuId = scanner.nextLine(); // 读取学号
        System.out.print("请输入新的手机号: ");
        String newPhone = scanner.nextLine(); // 读取新手机号

        String sql = "UPDATE students SET phone = ? WHERE stu_id = ?";
        Object[] params = {newPhone, stuId};
        try {
            if (CrudUtil.executeUpdate(sql, params) > 0) {
                System.out.println("手机号修改成功！");
            } else {
                System.out.println("手机号修改失败，请检查学号是否正确。");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("数据库操作出现异常，请稍后重试。");
        }
    }

    // 查询所有课程方法
    public static void getAllCourses() {
        String sql = "SELECT * FROM courses "; // 查询所有课程
        List<Course> courses = CrudUtil.executeQuery(sql, null, resultSet -> {
            return new Course(
                    resultSet.getInt("id"),
                    resultSet.getString("course_name"),
                    resultSet.getInt("credit"),
                    resultSet.getInt("nownum"),
                    resultSet.getInt("sumnum"),
                    resultSet.getString("teacher"),
                    resultSet.getString("course_time"),
                    resultSet.getString("course_place"),
                    resultSet.getString("course_id")
            );
        });
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

        StringBuilder sql = new StringBuilder("UPDATE courses SET ");
        List<Object> paramList = new ArrayList<>();
        boolean hasUpdate = false;
        if (credit != null) {
            sql.append("credit = ?, ");
            paramList.add(credit);
            hasUpdate = true;
        }
        if (!teacher.isEmpty()) {
            sql.append("teacher = ?, ");
            paramList.add(teacher);
            hasUpdate = true;
        }
        if (!courseTime.isEmpty()) {
            sql.append("course_time = ?, ");
            paramList.add(courseTime);
            hasUpdate = true;
        }
        if (!coursePlace.isEmpty()) {
            sql.append("course_place = ?, ");
            paramList.add(coursePlace);
            hasUpdate = true;
        }
        if (!hasUpdate) {
            System.out.println("未提供任何修改信息。");
            return;
        }

        // 移除最后一个逗号和空格
        sql.setLength(sql.length() - 2);
        sql.append(" WHERE course_id = ?");
        paramList.add(courseId);

        Object[] params = paramList.toArray();
        try {
            if (CrudUtil.executeUpdate(sql.toString(), params) > 0) {
                System.out.println("课程信息修改成功！");
            } else {
                System.out.println("课程信息修改失败，请检查课程编号是否正确。");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("数据库操作出现异常，请稍后重试。");
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
        Object[] params = {courseId};
        List<String> studentInfos = CrudUtil.executeQuery(sql, params, resultSet -> {
            StringBuilder info = new StringBuilder();
            info.append("学生学号: ").append(resultSet.getString("stu_id")).append("\n");
            info.append("学生姓名: ").append(resultSet.getString("stu_name")).append("\n");
            info.append("手机号: ").append(resultSet.getString("phone")).append("\n");
            info.append("-----------------------------");
            return info.toString();
        });
        if (studentInfos.isEmpty()) {
            System.out.println("该课程暂无学生选课。");
        } else {
            System.out.println("===== 选课学生名单 =====");
            for (String info : studentInfos) {
                System.out.println(info);
            }
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
        Object[] params = {stuId};
        List<String> courseInfos = CrudUtil.executeQuery(sql, params, resultSet -> {
            StringBuilder info = new StringBuilder();
            info.append("课程编号: ").append(resultSet.getString("course_id")).append("\n");
            info.append("课程名称: ").append(resultSet.getString("course_name")).append("\n");
            info.append("学分: ").append(resultSet.getInt("credit")).append("\n");
            info.append("授课教师: ").append(resultSet.getString("teacher")).append("\n");
            info.append("上课时间: ").append(resultSet.getString("course_time")).append("\n");
            info.append("上课地点: ").append(resultSet.getString("course_place")).append("\n");
            info.append("-----------------------------");
            return info.toString();
        });
        if (courseInfos.isEmpty()) {
            System.out.println("该学生暂无选课。");
        } else {
            System.out.println("===== 学生选课情况 =====");
            for (String info : courseInfos) {
                System.out.println(info);
            }
        }
    }
}