/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Permission;
import model.Role;
import model.Staff;

/**
 * DAO class for authentication and authorization
 */
public class AuthDAO extends DBContext {
    
    /**
     * Check if customer account exists and get its status
     * Returns: null if not found, Customer object with is_active status
     */
    public Customer checkCustomerAccount(String email, String password) {
        String sql = "SELECT * FROM customers WHERE email = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Customer(
                    rs.getInt("id"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("full_name"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("city"),
                    rs.getString("province"),
                    rs.getBoolean("is_active")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Authenticate customer login
     */
    public Customer loginCustomer(String email, String password) {
        String sql = "SELECT * FROM customers WHERE email = ? AND password = ? AND is_active = 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Customer(
                    rs.getInt("id"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getBoolean("is_active")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Check if staff account exists and get its status
     * Returns: null if not found, Staff object with is_active status
     */
    public Staff checkStaffAccount(String email, String password) {
        String sql = "SELECT s.*, d.name as department_name FROM staffs s " +
                    "JOIN departments d ON s.department_id = d.id " +
                    "WHERE s.email = ? AND s.password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Staff(
                    rs.getInt("id"),
                    rs.getInt("department_id"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("full_name"),
                    rs.getString("phone"),
                    rs.getBoolean("is_active")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Authenticate staff login
     */
    public Staff loginStaff(String email, String password) {
        String sql = "SELECT s.*, d.name as department_name FROM staffs s " +
                    "JOIN departments d ON s.department_id = d.id " +
                    "WHERE s.email = ? AND s.password = ? AND s.is_active = 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Staff(
                    rs.getInt("id"),
                    rs.getInt("role_id"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("full_name"),
                    rs.getString("department"),
                    rs.getBoolean("is_active")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get staff roles
     */
    public List<Role> getStaffRoles(int staffId) {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT r.* FROM roles r " +
                    "JOIN staff_role sr ON r.id = sr.role_id " +
                    "WHERE sr.staff_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, staffId);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                roles.add(new Role(
                    rs.getInt("id"),
                    rs.getString("name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }
    
    /**
     * Get role permissions
     */
    public List<Permission> getRolePermissions(int roleId) {
        List<Permission> permissions = new ArrayList<>();
        String sql = "SELECT p.* FROM permissions p " +
                    "JOIN role_permission rp ON p.id = rp.permission_id " +
                    "WHERE rp.role_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, roleId);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                permissions.add(new Permission(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return permissions;
    }
    
    /**
     * Get all permissions for a staff member
     */
    public List<Permission> getStaffPermissions(int staffId) {
        List<Permission> permissions = new ArrayList<>();
        String sql = "SELECT DISTINCT p.* FROM permissions p " +
                    "JOIN role_permission rp ON p.id = rp.permission_id " +
                    "JOIN staff_role sr ON rp.role_id = sr.role_id " +
                    "WHERE sr.staff_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, staffId);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                permissions.add(new Permission(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return permissions;
    }
    
    /**
     * Check if staff has specific permission
     */
    public boolean hasPermission(int staffId, String permissionName) {
        String sql = "SELECT COUNT(*) FROM permissions p " +
                    "JOIN role_permission rp ON p.id = rp.permission_id " +
                    "JOIN staff_role sr ON rp.role_id = sr.role_id " +
                    "WHERE sr.staff_id = ? AND p.name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, staffId);
            stmt.setString(2, permissionName);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
        
    /**
     * Find customer by email only (for forgot password)
     */
    public Customer findCustomerByEmail(String email) {
        String sql = "SELECT * FROM customers WHERE email = ? AND is_active = 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Customer(
                    rs.getInt("id"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("full_name"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("city"),
                    rs.getString("province"),
                    rs.getBoolean("is_active")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Find staff by email only (for forgot password)
     */
    public Staff findStaffByEmail(String email) {
        String sql = "SELECT * FROM staffs WHERE email = ? AND is_active = 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Staff(
                    rs.getInt("id"),
                    rs.getInt("role_id"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("full_name"),
                    rs.getString("department"),
                    rs.getBoolean("is_active")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Update customer password
     */
    public boolean updateCustomerPassword(String email, String newPassword) {
        String sql = "UPDATE customers SET password = ? WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, email);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Update staff password
     */
    public boolean updateStaffPassword(String email, String newPassword) {
        String sql = "UPDATE staffs SET password = ? WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, email);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}