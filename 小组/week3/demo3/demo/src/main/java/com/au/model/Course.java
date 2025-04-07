package com.au.model;

public class Course {
    private int id;//
    private String courseName;  // 课程名称
    private int credit;        // 学分
    private int nowNum; // 当前已选人数
    private int sumNum; // 最大选课人数
    private String teacher; // 授课教师
    private String time; // 上课时间
    private String place; // 上课地点
    private String courseId; // 课程编号

    // 构造函数
    public Course(int id, String courseName, int credit, int nowNum, int sumNum, String teacher, String time, String place, String courseId) {
        this.id = id;
        this.courseName = courseName;
        this.credit = credit;
        this.nowNum = nowNum;
        this.sumNum = sumNum;
        this.teacher = teacher;
        this.time = time;
        this.place = place;
        this.courseId = courseId;
    }

    // Getter 和 Setter 方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public int getNowNum() {
        return nowNum;
    }

    public void setNowNum(int nowNum) {
        this.nowNum = nowNum;
    }

    public int getSumNum() {
        return sumNum;
    }

    public void setSumNum(int sumNum) {
        this.sumNum = sumNum;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}