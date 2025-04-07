package com.au.servlet;

import com.au.model.Course;
import com.au.dao.CourseService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/searchCourses")
public class SearchCoursesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        List<Course> courses = CourseService.searchCourses(keyword);

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);
            json.append("{");
            json.append("\"courseId\": \"").append(course.getCourseId()).append("\",");
            json.append("\"courseName\": \"").append(course.getCourseName()).append("\",");
            json.append("\"credit\": ").append(course.getCredit()).append(",");
            json.append("\"teacher\": \"").append(course.getTeacher()).append("\",");
            json.append("\"courseTime\": \"").append(course.getTime()).append("\",");
            json.append("\"coursePlace\": \"").append(course.getPlace()).append("\"");
            json.append("}");
            if (i < courses.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        out.println(json.toString());
        out.close();
    }
}