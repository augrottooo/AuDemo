package com.au.servlet;

import com.au.dao.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
/*
/**
 * 用户注册Servlet
 * 处理用户注册请求

@WebServlet("/register")  // 将该Servlet映射到"/register"路径
public class RegisterServlet extends HttpServlet {

    /**
     * 处理HTTP POST请求
     * @param request HTTP请求对象
     * @param response HTTP响应对象

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 从请求中获取用户注册参数
        String username = request.getParameter("username");  // 用户名
        String password = request.getParameter("password");   // 密码
        String role = request.getParameter("role");           // 用户角色
        String name = request.getParameter("name");           // 真实姓名
        String phoneNumber = request.getParameter("phoneNumber"); // 电话号码

        // 调用UserService进行用户注册
        boolean result = UserService.registerUser(username, password, role, name, phoneNumber);

        // 设置响应内容类型为JSON格式
        response.setContentType("application/json");

        // 获取响应输出流
        PrintWriter out = response.getWriter();

        // 根据注册结果返回JSON响应
        if (result) {
            out.println("{\"success\": true}");  // 注册成功
        } else {
            out.println("{\"success\": false}");  // 注册失败
        }
        out.close();  // 关闭输出流
    }
}
*/


// 注册接口
@WebServlet("/api/register")
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        String name = request.getParameter("name");
        String phoneNumber = request.getParameter("phoneNumber");
        boolean isRegistered = UserService.registerUser(username, password, role, name, phoneNumber);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        if (isRegistered) {
            // 注册成功
            out.println("{\"success\": true, \"message\": \"注册成功\"}");
        } else {
            // 注册失败
            out.println("{\"success\": false, \"message\": \"注册失败，请稍后重试\"}");
        }
        out.close();
    }
}