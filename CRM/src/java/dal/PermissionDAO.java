package dal;

import model.Permission;
import model.Role;
import model.RolePermission;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * DAO for Permission and Role-Permission management
 */
public class PermissionDAO extends DBContext {
    
    /**
     * Get all permissions
     */
    public List<Permission> getAllPermissions() {
        List<Permission> permissions = new ArrayList<>();
        String sql = "SELECT * FROM permissions ORDER BY resource, action";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Permission permission = new Permission();
                permission.setId(rs.getInt("id"));
                permission.setResource(rs.getString("resource"));
                permission.setAction(rs.getString("action"));
                permission.setDescription(rs.getString("description"));
                permissions.add(permission);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return permissions;
    }
    
    /**
     * Get permission by ID
     */
    public Permission getPermissionById(int id) {
        String sql = "SELECT * FROM permissions WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Permission permission = new Permission();
                permission.setId(rs.getInt("id"));
                permission.setResource(rs.getString("resource"));
                permission.setAction(rs.getString("action"));
                permission.setDescription(rs.getString("description"));
                return permission;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get roles assigned to a specific permission
     */
    public List<Role> getPermissionRoles(int permissionId) {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT r.* FROM roles r " +
                    "INNER JOIN role_permission rp ON r.id = rp.role_id " +
                    "WHERE rp.permission_id = ? AND r.name != 'ADMIN'";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, permissionId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Role role = new Role();
                role.setId(rs.getInt("id"));
                role.setName(rs.getString("name"));
                role.setDescription(rs.getString("description"));
                roles.add(role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return roles;
    }
    
    /**
     * Get permissions for a specific role
     */
    public List<Permission> getRolePermissions(int roleId) {
        List<Permission> permissions = new ArrayList<>();
        String sql = "SELECT p.* FROM permissions p " +
                    "INNER JOIN role_permission rp ON p.id = rp.permission_id " +
                    "WHERE rp.role_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, roleId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Permission permission = new Permission();
                permission.setId(rs.getInt("id"));
                permission.setResource(rs.getString("resource"));
                permission.setAction(rs.getString("action"));
                permission.setDescription(rs.getString("description"));
                permissions.add(permission);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return permissions;
    }
    
    /**
     * Get all permissions for a staff member (through their roles)
     */
    public List<Permission> getStaffPermissions(int staffId) {
        List<Permission> permissions = new ArrayList<>();
        String sql = "SELECT DISTINCT p.* FROM permissions p " +
                    "INNER JOIN role_permission rp ON p.id = rp.permission_id " +
                    "INNER JOIN staff_role sr ON rp.role_id = sr.role_id " +
                    "WHERE sr.staff_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, staffId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Permission permission = new Permission();
                permission.setId(rs.getInt("id"));
                permission.setResource(rs.getString("resource"));
                permission.setAction(rs.getString("action"));
                permission.setDescription(rs.getString("description"));
                permissions.add(permission);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return permissions;
    }
    
    /**
     * Check if a staff has specific permission
     */
    public boolean hasPermission(int staffId, String resource, String action) {
        String sql = "SELECT COUNT(*) FROM permissions p " +
                    "INNER JOIN role_permission rp ON p.id = rp.permission_id " +
                    "INNER JOIN staff_role sr ON rp.role_id = sr.role_id " +
                    "WHERE sr.staff_id = ? AND p.resource = ? AND p.action = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, staffId);
            stmt.setString(2, resource);
            stmt.setString(3, action);
            
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
     * Get all role-permission mappings
     */
    public Map<Integer, List<Integer>> getAllRolePermissions() {
        Map<Integer, List<Integer>> rolePermissions = new HashMap<>();
        String sql = "SELECT role_id, permission_id FROM role_permission";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int roleId = rs.getInt("role_id");
                int permissionId = rs.getInt("permission_id");
                
                rolePermissions.computeIfAbsent(roleId, k -> new ArrayList<>()).add(permissionId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return rolePermissions;
    }
    
    /**
     * Update permissions for a role
     */
    public void updateRolePermissions(int roleId, List<Integer> permissionIds) throws SQLException {
        // Remove all existing permissions
        String deleteSql = "DELETE FROM role_permission WHERE role_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteSql)) {
            stmt.setInt(1, roleId);
            stmt.executeUpdate();
        }
        
        // Add new permissions
        if (permissionIds != null && !permissionIds.isEmpty()) {
            String insertSql = "INSERT INTO role_permission (role_id, permission_id) VALUES (?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
                for (Integer permissionId : permissionIds) {
                    stmt.setInt(1, roleId);
                    stmt.setInt(2, permissionId);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }
    }
    
    /**
     * Update roles for a permission (assign permission to roles)
     */
    public void updatePermissionRoles(int permissionId, List<Integer> roleIds) throws SQLException {
        // Remove all existing role assignments for this permission
        String deleteSql = "DELETE FROM role_permission WHERE permission_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteSql)) {
            stmt.setInt(1, permissionId);
            stmt.executeUpdate();
        }
        
        // Add new role assignments
        if (roleIds != null && !roleIds.isEmpty()) {
            String insertSql = "INSERT INTO role_permission (role_id, permission_id) VALUES (?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
                for (Integer roleId : roleIds) {
                    stmt.setInt(1, roleId);
                    stmt.setInt(2, permissionId);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }
    }
    
    /**
     * Create a new permission
     */
    public int createPermission(Permission permission) throws SQLException {
        String sql = "INSERT INTO permissions (resource, action, description) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, permission.getResource());
            stmt.setString(2, permission.getAction());
            stmt.setString(3, permission.getDescription());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return -1;
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        return -1;
    }
    
    /**
     * Update permission
     */
    public boolean updatePermission(Permission permission) throws SQLException {
        String sql = "UPDATE permissions SET resource = ?, action = ?, description = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, permission.getResource());
            stmt.setString(2, permission.getAction());
            stmt.setString(3, permission.getDescription());
            stmt.setInt(4, permission.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete permission
     */
    public boolean deletePermission(int permissionId) throws SQLException {
        // First delete role_permission associations
        String deleteRolePermissions = "DELETE FROM role_permission WHERE permission_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteRolePermissions)) {
            stmt.setInt(1, permissionId);
            stmt.executeUpdate();
        }
        
        // Then delete permission
        String sql = "DELETE FROM permissions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, permissionId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Get all permissions with pagination
     */
    public List<Permission> getAllPermissions(int page, int pageSize) {
        List<Permission> permissions = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        
        String sql = "SELECT * FROM permissions ORDER BY id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, offset);
            stmt.setInt(2, pageSize);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Permission permission = new Permission();
                permission.setId(rs.getInt("id"));
                permission.setResource(rs.getString("resource"));
                permission.setAction(rs.getString("action"));
                permission.setDescription(rs.getString("description"));
                permissions.add(permission);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return permissions;
    }
    
    /**
     * Get total count
     */
    public int getTotalPermissionsCount() {
        String sql = "SELECT COUNT(*) FROM permissions";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Search permissions
     */
    public List<Permission> searchPermissions(String keyword, int page, int pageSize) {
        List<Permission> permissions = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        
        String sql = "SELECT * FROM permissions WHERE resource LIKE ? OR action LIKE ? OR description LIKE ? " +
                    "ORDER BY id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setInt(4, offset);
            stmt.setInt(5, pageSize);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Permission permission = new Permission();
                permission.setId(rs.getInt("id"));
                permission.setResource(rs.getString("resource"));
                permission.setAction(rs.getString("action"));
                permission.setDescription(rs.getString("description"));
                permissions.add(permission);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return permissions;
    }
    
    /**
     * Get search count
     */
    public int getSearchResultsCount(String keyword) {
        String sql = "SELECT COUNT(*) FROM permissions WHERE resource LIKE ? OR action LIKE ? OR description LIKE ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

