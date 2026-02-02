/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Admin
 */
public class Staff {
    private int id;
    private int roleId;
    private String email;
    private String password;
    private String fullName;
    private String department;
    private String createAt;
    private boolean isActive;

    public Staff() {
    }

    public Staff(int id, int roleId, String email, String password, String fullName, String department, String createAt, boolean isActive) {
        this.id = id;
        this.roleId = roleId;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.department = department;
        this.createAt = createAt;
        this.isActive = isActive;
    }

    public Staff(int roleId, String email, String password, String fullName, String department, String createAt, boolean isActive) {
        this.roleId = roleId;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.department = department;
        this.createAt = createAt;
        this.isActive = isActive;
    }

    public Staff(int id, int roleId, String email, String password, String fullName, String department, boolean isActive) {
        this.id = id;
        this.roleId = roleId;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.department = department;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "Staff{" + "id=" + id + ", roleId=" + roleId + ", email=" + email + ", password=" + password + ", fullName=" + fullName + ", department=" + department + ", isActive=" + isActive + '}';
    }
    
}
