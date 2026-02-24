package model;

import java.time.LocalDateTime;

public class Customer {

    private int id;
    private String fullName;
    private String email;
    private String phone;
    private String password;
    private String address;
    private int tierId;
    private String status;
    private String profilePicUrl;
    private int ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String tierName;

    public Customer() {
    }

    // Constructor đầy đủ
    public Customer(int id, String fullName, String email, String phone, String password,
            String address, int tierId, String status, String profilePicUrl,
            int ownerId, LocalDateTime createdAt, LocalDateTime updatedAt) {

        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.address = address;
        this.tierId = tierId;
        this.status = status;
        this.profilePicUrl = profilePicUrl;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Constructor khi insert (chưa có id, time)
    public Customer(String fullName, String email, String phone, String password,
            String address, int tierId, String status,
            String profilePicUrl, int ownerId) {

        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.address = address;
        this.tierId = tierId;
        this.status = status;
        this.profilePicUrl = profilePicUrl;
        this.ownerId = ownerId;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTierName() {
        return tierName;
    }

    public void setTierName(String tierName) {
        this.tierName = tierName;
    }

    @Override
    public String toString() {
        return "Customer{" + "id=" + id + ", fullName=" + fullName + ", email=" + email + ", phone=" + phone + ", password=" + password + ", address=" + address + ", tierId=" + tierId + ", status=" + status + ", profilePicUrl=" + profilePicUrl + ", ownerId=" + ownerId + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + '}';
    }

}
