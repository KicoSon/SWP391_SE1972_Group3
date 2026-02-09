package filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.UserSession;

/**
 * Filter để kiểm tra quyền truy cập (Authorization)
 */
public class AuthorizationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Khởi tạo filter nếu cần (thường để trống)
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 1. Ép kiểu sang HttpServlet để xử lý HTTP
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 2. Lấy đường dẫn (Path)
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        // Lấy phần đuôi sau tên dự án (ví dụ: /sale/dashboard)
        String path = requestURI.substring(contextPath.length());

        // 3. Bỏ qua các trang không cần kiểm tra (Login, Assets, Logout)
        if (path.equals("/login") || path.startsWith("/assets/") 
            || path.equals("/") || path.equals("/logout")) {
            chain.doFilter(request, response);
            return;
        }

        // 4. Lấy Session
        HttpSession session = httpRequest.getSession(false);
        UserSession userSession = null;
        if (session != null) {
            userSession = (UserSession) session.getAttribute("userSession");
        }

        // 5. Kiểm tra đăng nhập
        if (userSession == null) {
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }

        // 6. Phân quyền dựa trên Role và URL
        boolean isAllowed = true; // Mặc định cho qua nếu không vi phạm các case dưới

        if (path.startsWith("/admin/")) {
            if (!userSession.isStaff() || !userSession.isAdmin()) {
                isAllowed = false;
            }
        } else if (path.startsWith("/support/")) {
            if (!userSession.isStaff() || (!userSession.isSupportStaff() && !userSession.isAdmin())) {
                isAllowed = false;
            }
        } else if (path.startsWith("/sale/")) {
            // Logic cho Sale Staff
            if (!userSession.isStaff() || (!userSession.isSaleStaff() && !userSession.isAdmin())) {
                isAllowed = false;
            }
        } else if (path.startsWith("/marketing/")) {
            if (!userSession.isStaff() || (!userSession.isMarketingStaff() && !userSession.isAdmin())) {
                isAllowed = false;
            }
        } else if (path.startsWith("/customer/")) {
            if (!userSession.isCustomer()) {
                isAllowed = false;
            }
        } else if (path.startsWith("/staff/")) {
            if (!userSession.isStaff()) {
                isAllowed = false;
            }
        }

        // 7. Xử lý kết quả phân quyền
        if (isAllowed) {
            chain.doFilter(request, response); // Cho phép đi tiếp
        } else {
            // Báo lỗi 403 (Forbidden) hoặc chuyển hướng
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Bạn không có quyền truy cập trang này.");
        }
    }

    @Override
    public void destroy() {
        // Hủy filter (thường để trống)
    }
}