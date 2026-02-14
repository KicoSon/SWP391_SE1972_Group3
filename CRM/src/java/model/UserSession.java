package model;

import java.util.List;

/**
 * User session model for managing login session
 */
public class UserSession {
    private String userType; // "CUSTOMER" or "STAFF"
    private Customer customer;
    private Staff staff;
    private List<Role> roles;
    private List<Permission> permissions;
    
    // Constructor for customer session
    public UserSession(Customer customer) {
        this.userType = "CUSTOMER";
        this.customer = customer;
    }
    
    // Constructor for staff session
    public UserSession(Staff staff, List<Role> roles, List<Permission> permissions) {
        this.userType = "STAFF";
        this.staff = staff;
        this.roles = roles;
        this.permissions = permissions;
    }
    
    // Getters and Setters
    public String getUserType() {
        return userType;
    }
    
    public void setUserType(String userType) {
        this.userType = userType;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    // Alternative getter to avoid JSP EL conflicts
    public Customer getCustomerInfo() {
        return customer;
    }
    
    public Staff getStaff() {
        return staff;
    }
    
    public void setStaff(Staff staff) {
        this.staff = staff;
    }
    
    // Alternative getter to avoid JSP EL conflicts
    public Staff getStaffInfo() {
        return staff;
    }
        
    public List<Role> getRoles() {
        return roles;
    }
    
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
    
    public List<Permission> getPermissions() {
        return permissions;
    }
    
    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
    
    // Helper method to get user ID
    public int getUserId() {
        if (isCustomerUser() && customer != null) {
            return customer.getId();
        } else if (isStaffUser() && staff != null) {
            return staff.getId();
        }
        return -1;
    }
    
    // Helper methods
// Đổi tên từ isStaff thành checkIsStaff hoặc isStaffUser
public boolean isStaffUser() { 
    return "STAFF".equals(userType);
}

// Đổi tên từ isCustomer thành checkIsCustomer hoặc isCustomerUser
public boolean isCustomerUser() {
    return "CUSTOMER".equals(userType);
}
    public String getDisplayName() {
        if (isCustomerUser() && customer != null) {
            return customer.getFullName();
        } else if (isStaffUser() && staff != null) {
            return staff.getFullName();
        }
        return "Unknown User";
    }
    
    public String getEmail() {
        if (isCustomerUser() && customer != null) {
            return customer.getEmail();
        } else if (isStaffUser() && staff != null) {
            return staff.getEmail();
        }
        return null;
    }
    
    public boolean hasPermission(String permissionName) {
        if (permissions == null) return false;
        return permissions.stream().anyMatch(p -> p.getName().equals(permissionName));
    }
    
    public boolean hasRole(String roleName) {
        if (roles == null) return false;
        return roles.stream().anyMatch(r -> r.getName().equals(roleName));
    }
    
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }
    
    public boolean isSupportStaff() {
        return hasRole("SUPPORT_STAFF");
    }
    
    public boolean isSaleStaff() {
        return hasRole("SALE_STAFF");
    }    
    
    public boolean isMarketingStaff() {
        return hasRole("MARKETING_STAFF");
    }
    
}