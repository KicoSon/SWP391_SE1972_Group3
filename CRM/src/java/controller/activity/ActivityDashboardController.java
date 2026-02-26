package controller.activity;

import dal.ActivityDAO;
import model.activity.Activity;
import model.UserSession;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// Map đường dẫn này bắt đầu bằng /sale/ để tận dụng AuthorizationFilter có sẵn
@WebServlet(name = "ActivityDashboardController", urlPatterns = {"/sale/dashboard"})
public class ActivityDashboardController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Kiểm tra session đăng nhập
        HttpSession session = request.getSession();
        UserSession userSession = (UserSession) session.getAttribute("userSession");

        if (userSession == null || userSession.getStaff() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // ========================================================
        // 2. LOGIC PHÂN QUYỀN (CHUẨN CRM)
        // ========================================================
        Integer filterUserId = null;

        // Nếu là Sale thông thường (không có quyền Admin/Manager) 
        // -> Gán filterUserId = ID của chính họ để SQL chỉ lọc việc của họ
        if (userSession.isSaleStaff() && !userSession.isAdmin()) {
            filterUserId = userSession.getStaff().getId();
        }
        // Nếu là Sếp/Admin -> filterUserId giữ nguyên là null (SQL sẽ lấy tất cả)

        // 3. Gọi DAO lấy danh sách Activity đã được Join với Customer/Lead
        ActivityDAO dao = new ActivityDAO();
        List<Activity> activityList = dao.getActivitiesForDashboard(filterUserId);

        // 4. Gắn danh sách vào request để gửi sang giao diện (JSP)
        request.setAttribute("activities", activityList);
        
        // Code này forward request vào bên trong thư mục chứa file JSP
        request.getRequestDispatcher("/activities/activity-dashboard.jsp")
               .forward(request, response);
    }
}