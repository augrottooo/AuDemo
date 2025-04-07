package com.au.dao;

import com.au.model.Course;
import com.au.util.CrudUtil;

import java.util.List;

public class CourseService {
    public static List<Course> searchCourses(String keyword) {
        String sql = "SELECT * FROM courses WHERE course_name LIKE ?";
        Object[] params = {"%" + keyword + "%"};
        return CrudUtil.executeQuery(sql, params, resultSet -> {
            try {
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
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }
}
