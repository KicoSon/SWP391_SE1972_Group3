package filter;

import model.UserSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class AuthorizationFilter {
    
    public void doFilter(Object request, Object response, Object chain) throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        try {
            String requestURI = (String) request.getClass().getMethod("getRequestURI").invoke(request);
            String contextPath = (String) request.getClass().getMethod("getContextPath").invoke(request);
            
            // Cắt context path để còn lại route nội bộ dùng cho phân quyền.
            String path = requestURI.substring(contextPath.length());
            
            // Cho phép truy cập tự do vào login/logout và tài nguyên tĩnh.
            if (path.equals("/login") || path.startsWith("/assets/") || 
                path.equals("/") || path.equals("/logout")) {
                chain.getClass().getMethod("doFilter", Object.class, Object.class).invoke(chain, request, response);
                return;
            }
            
            // Lấy userSession hiện tại từ HttpSession.
            Object session = request.getClass().getMethod("getSession", boolean.class).invoke(request, false);
            UserSession userSession = null;
            
            if (session != null) {
                userSession = (UserSession) session.getClass().getMethod("getAttribute", String.class).invoke(session, "userSession");
            }
            
            // Chưa đăng nhập thì buộc quay lại login.
            if (userSession == null) {
                response.getClass().getMethod("sendRedirect", String.class).invoke(response, contextPath + "/login");
                return;
            }
            
            // Phân quyền theo prefix URL.
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
            
            // Hợp lệ thì cho request đi tiếp chuỗi filter/controller.
            chain.getClass().getMethod("doFilter", Object.class, Object.class).invoke(chain, request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.getClass().getMethod("sendRedirect", String.class).invoke(response, "/login");
        }
    }
}