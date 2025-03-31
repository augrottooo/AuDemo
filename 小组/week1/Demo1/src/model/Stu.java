package model;

public class Stu {
    private String stuId;
    private String name;
    private String phoneNumber;
    private String password;

    // 有参构造函数，用于初始化用户信息
    public Stu(String stuId, String name, String phoneNumber, String password) {
        this.stuId = stuId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    // Getter 和 Setter 方法
    public String getStuId() {
        return stuId;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}