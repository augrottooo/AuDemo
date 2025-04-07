package com.au.servlet;

import com.au.dao.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

//
//@WebServlet("/Servlet01")
//public class Servlet01 extends HttpServlet {
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        String referer = req.getHeader("referer");
//        System.out.println(referer);
//
//        if (referer != null) {
//            if (referer.contains("/servlet_war_exploded")) {
//                System.out.println("播放电影");
//            } else {
//                System.out.println("想看电影吗？来优酷吧！");
//            }
//        }
//    }
//}

/*
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取请求对象
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = UserService.login(username, password);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        if (role   != null) {
            // 登录成功
            out.println("{\"success\": true, \"role\": \"" + role + "\"}");
            // 存储数据
            request.setAttribute("role", role);
            //转发
            request.getRequestDispatcher("/successServlet").forward(request, response);
        } else {
            // 登录失败
            out.println("{\"success\": false}");
        }
        out.close();
    }
}
*/
// 登录接口
@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取请求对象
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = UserService.login(username, password);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        if (role != null) {
            // 登录成功
            out.println("{\"success\": true, \"role\": \"" + role + "\"}");
        } else {
            // 登录失败
            out.println("{\"success\": false, \"message\": \"用户名或密码错误\"}");
        }
        out.close();
    }
}