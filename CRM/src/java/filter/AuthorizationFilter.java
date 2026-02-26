package filter;

import model.UserSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class AuthorizationFilter {
    
    public void doFilter(Object request, Object response, Object chain) throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        try {
            String requestURI = (String) request.getClass().getMethod("getRequestURI").invoke(request);
            String contextPath = (String) request.getClass().getMethod("getContextPath").invoke(request);
            
            // Remove context path from request URI
            String path = requestURI.substring(contextPath.length());
            
            // Allow access to login page and assets
            if (path.equals("/login") || path.startsWith("/assets/") || 
                path.equals("/") || path.equals("/logout")) {
                chain.getClass().getMethod("doFilter", Object.class, Object.class).invoke(chain, request, response);
                return;
            }
            
            // Get user session
            Object session = request.getClass().getMethod("getSession", boolean.class).invoke(request, false);
            UserSession userSession = null;
            
            if (session != null) {
                userSession = (UserSession) session.getClass().getMethod("getAttribute", String.class).invoke(session, "userSession");
            }
            
            // Check if user is logged in
            if (userSession == null) {
                response.getClass().getMethod("sendRedirect", String.class).invoke(response, contextPath + "/login");
                return;
            }
            
            // Check role-based access
            if (path.startsWith("/admin/")) {
                if (!userSession.isStaff() || !userSession.isAdmin()) {
                    response.getClass().getMethod("sendError", int.class, String.class).invoke(response, 403, "Access Denied");
                    return;
                }
            } else if (path.startsWith("/support/")) {
                if (!userSession.isStaff() || (!userSession.isSupportStaff() && !userSession.isAdmin())) {
                    response.getClass().getMethod("sendError", int.class, String.class).invoke(response, 403, "Access Denied");
                    return;
                }
            } else if (path.startsWith("/sale/")) {
                if (!userSession.isStaff() || (!userSession.isSaleStaff() && !userSession.isAdmin())) {
                    response.getClass().getMethod("sendError", int.class, String.class).invoke(response, 403, "Access Denied");
                    return;
                }
            } else if (path.startsWith("/marketing/")) {
                if (!userSession.isStaff() || (!userSession.isMarketingStaff() && !userSession.isAdmin())) {
                    response.getClass().getMethod("sendError", int.class, String.class).invoke(response, 403, "Access Denied");
                    return;
                }
            } else if (path.startsWith("/customer/")) {
                if (!userSession.isCustomer()) {
                    response.getClass().getMethod("sendError", int.class, String.class).invoke(response, 403, "Access Denied");
                    return;
                }
            } else if (path.startsWith("/staff/")) {
                if (!userSession.isStaff()) {
                    response.getClass().getMethod("sendError", int.class, String.class).invoke(response, 403, "Access Denied");
                    return;
                }
            }
            
            // Continue with the request
            chain.getClass().getMethod("doFilter", Object.class, Object.class).invoke(chain, request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.getClass().getMethod("sendRedirect", String.class).invoke(response, "/login");
        }
    }
}