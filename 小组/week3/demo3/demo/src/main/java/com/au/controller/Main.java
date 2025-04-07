package com.au.controller;

import com.au.dao.UserService;

import java.util.Scanner;


public class Main {
    // 最大登录尝试次数
    private static final int MAX_LOGIN_ATTEMPTS = 3;

    public static void main(String[] args) {
        // 创建 Scanner 对象用于读取用户输入
        Scanner scanner = new Scanner(System.in);
        while (true) {
            // 显示主菜单选项
            System.out.println("===========================");
            System.out.println("学生选课管理系统");
            System.out.println("===========================");
            System.out.println("1. 登录");
            System.out.println("2. 注册");
            System.out.println("3. 退出");
            System.out.print("请选择操作(输入1-3): ");
            // 读取用户选择的操作
            int choice = scanner.nextInt();
            // 消耗换行符，清除缓冲区
            scanner.nextLine();
            switch (choice) {
                case 1:
                    //调用登录方法
                    login(scanner);
                    break;
                case 2:
                    // 调用注册方法
                    register(scanner);
                    break;
                case 3:
                    // 退出程序
                    System.out.println("退出系统，再见！");
                    return;
                default:
                    // 输入无效选项时提示用户重新输入
                    System.out.println("无效的选择，请重新输入。");
            }
        }
    }

    // 登录方法
    private static boolean login(Scanner scanner) {
        int attempts = 0;
        while (attempts < MAX_LOGIN_ATTEMPTS) {
            System.out.println("===== 用户登录 =====");
            System.out.print("请输入用户名(学生为学号，管理员为姓名)：");
            String username = scanner.nextLine();
            System.out.print("请输入密码(6位数字)：");
            String password = scanner.nextLine();
            String role = UserService.login(username, password);
            if (role != null) {
                System.out.println("登录成功！你的角色是：" + role);
                if (role.equals("student")) {
                    // 进入学生菜单
                    Student.stuMenu(scanner, username);
                } else if (role.equals("admin")) {
                    // 进入管理员菜单
                    Admin.adminMenu(scanner, username);
                }
            } else {
                attempts++;
                System.out.println("登录失败，用户名或密码错误！");
                System.out.println("你还有 " + (MAX_LOGIN_ATTEMPTS - attempts) + " 次尝试机会。");
            }
        }
        System.out.println("登录尝试次数过多，账户已锁定。");
        return false;
    }

    // 注册方法
    private static void register(Scanner scanner) {
        System.out.println("===== 用户注册 =====");
        System.out.print("请选择角色（输入 1 代表学生，2 代表管理员）: ");
        String newRole = scanner.nextLine();
        String newName = null;
        String newPhone = null;
        if (newRole.equals("1")) {
            System.out.println("请输入姓名：");
            newName = scanner.nextLine();
            System.out.println("请输入手机号：");
            newPhone = scanner.nextLine();
        }
        System.out.print("请输入用户名(学生为学号，管理员为姓名): ");
        String newUsername = scanner.nextLine();
        System.out.print("请输入密码: ");
        String password1 = scanner.nextLine();
        System.out.print("请确认密码: ");
        String password2 = scanner.nextLine();
        String newPassword;
        if (!password1.equals(password2)) {
            System.out.println("两次输入的密码不一致，请重新注册。");
            return;
        } else {
            newPassword = password2;
        }
        // 调用用户服务类的注册方法
        if (UserService.registerUser(newUsername, newPassword, newRole, newName, newPhone)) {
            System.out.println("注册成功！请返回主界面登录。");
        } else {
            System.out.println("注册失败，请检查输入信息。");
        }
    }

    // 修改密码方法
    public static void changePasswordMenu(Scanner scanner, String username) {
        System.out.println("===== 修改密码 =====");
        System.out.print("请输入旧密码：");
        String oldPassword = scanner.nextLine();
        System.out.print("请输入新密码：");
        String newPassword = scanner.nextLine();
        System.out.print("请确认新密码：");
        String confirmPassword = scanner.nextLine();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("两次输入的新密码不一致，请重试。");
            return;
        }

        if (UserService.changePassword(username, oldPassword, newPassword)) {
            System.out.println("密码修改成功！");
        } else {
            System.out.println("密码修改失败，请检查输入信息。");
        }
    }
}
