/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Admin
 */
public class Customer {
    private int id;
    private int tier;
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String profileURL;
    private String createAt;
    private String updateAt;
    private boolean isActive;

    public Customer() {
    }

    public Customer(int id, String fullName, String email, String phone, String address, String profileURL, String createAt, String updateAt, boolean isActive) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.profileURL = profileURL;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.isActive = isActive;
    }

    public Customer(String fullName, String email, String phone, String address, String profileURL, String createAt, String updateAt, boolean isActive) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.profileURL = profileURL;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.isActive = isActive;
    }

    public Customer(int id, String fullName, String email, String password, String phone, String address, boolean isActive) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.isActive = isActive;
    }

    

    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }


    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    @Override
    public String toString() {
        return "Customer{" + "id=" + id + ", fullName=" + fullName + ", email=" + email + ", phone=" + phone + ", address=" + address + ", profileURL=" + profileURL + ", isActive=" + isActive + '}';
    }
    
    
}
