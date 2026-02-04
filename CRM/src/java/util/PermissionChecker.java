package util;

import dal.PermissionDAO;
import model.Permission;
import model.UserSession;
import java.util.List;

/**
 * Utility class for checking user permissions
 */
public class PermissionChecker {
    
    /**
     * Check if user has specific permission
     * MANAGE permission grants all actions (VIEW, CREATE, EDIT, DELETE) on a resource
     */
    public static boolean hasPermission(UserSession userSession, String resource, String action) {
        if (userSession == null || userSession.getStaff() == null) {
            return false;
        }
        
        // ADMIN role has full access to everything
        if (userSession.isAdmin()) {
            return true;
        }
        
        try {
            PermissionDAO permissionDAO = new PermissionDAO();
            int staffId = userSession.getStaff().getId();
            
            // Get all permissions for this staff (through their roles)
            List<Permission> permissions = permissionDAO.getStaffPermissions(staffId);
            
            // Check if staff has the required permission
            for (Permission perm : permissions) {
                if (perm.getResource().equals(resource)) {
                    // Exact match for the requested action
                    if (perm.getAction().equals(action)) {
                        return true;
                    }
                    
                    // MANAGE permission grants all other actions on the resource
                    if (perm.getAction().equals("MANAGE") && 
                        (action.equals("VIEW") || action.equals("CREATE") || 
                         action.equals("EDIT") || action.equals("DELETE"))) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Check if user has VIEW permission on resource
     */
    public static boolean canView(UserSession userSession, String resource) {
        return hasPermission(userSession, resource, "VIEW");
    }
    
    /**
     * Check if user has CREATE permission on resource
     */
    public static boolean canCreate(UserSession userSession, String resource) {
        return hasPermission(userSession, resource, "CREATE");
    }
    
    /**
     * Check if user has EDIT permission on resource
     */
    public static boolean canEdit(UserSession userSession, String resource) {
        return hasPermission(userSession, resource, "EDIT");
    }
    
    /**
     * Check if user has DELETE permission on resource
     */
    public static boolean canDelete(UserSession userSession, String resource) {
        return hasPermission(userSession, resource, "DELETE");
    }
    
    /**
     * Check if user has MANAGE permission on resource
     */
    public static boolean canManage(UserSession userSession, String resource) {
        return hasPermission(userSession, resource, "MANAGE");
    }
}
