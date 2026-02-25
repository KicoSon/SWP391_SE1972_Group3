package model;

import java.sql.Timestamp;

public class Customer {
    private int id;
    private String fullName;      // Map với full_name
    private String email;
    private String password;      // Có lệnh ALTER TABLE ADD password trong SQL
    private String phone;
    private String address;
    private int tierId;           // Map với tier_id (Default 1)
    private String status;        // Active/Inactive
    private String profilePicUrl; // Map với profile_pic_url
    private int ownerId;          // Map với owner_id (Liên kết với bảng users)
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // 1. Constructor rỗng (Bắt buộc cho JavaBean)
    public Customer() {
    }

    // 2. Constructor đầy đủ (Dùng khi lấy dữ liệu full từ DB)
    public Customer(int id, String fullName, String email, String password, String phone, String address, int tierId, String status, String profilePicUrl, int ownerId, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.tierId = tierId;
        this.status = status;
        this.profilePicUrl = profilePicUrl;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // 3. Constructor rút gọn (Tương thích với AuthDAO cũ nếu cần)
    public Customer(int id, String fullName, String email, String password, String phone, String address, String status) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.status = status;
    }

    // --- GETTERS AND SETTERS ---

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public int getTierId() {
        return tierId;
    }

    public void setTierId(int tierId) {
        this.tierId = tierId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}