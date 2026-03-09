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
        
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        UserSession userSession = (UserSession) session.getAttribute("userSession");

        if (userSession == null || userSession.getStaff() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 1. LẤY THAM SỐ TỪ URL (Search, Type, Page...)
        String keyword = request.getParameter("search");
        String type = request.getParameter("type");
        String fromDate = request.getParameter("from");
        String toDate = request.getParameter("to");
        
        int pageIndex = 1;
        int pageSize = 10; // Số dòng mỗi trang
        try {
            if (request.getParameter("page") != null) {
                pageIndex = Integer.parseInt(request.getParameter("page"));
            }
        } catch (NumberFormatException e) {
            pageIndex = 1;
        }

        // 2. PHÂN QUYỀN
        Integer filterUserId = null;
        if (userSession.isSaleStaff() && !userSession.isAdmin()) {
            filterUserId = userSession.getStaff().getId();
        }

        ActivityDAO dao = new ActivityDAO();

        // 3. GỌI DB ĐỂ LẤY DỮ LIỆU
        List<Activity> list = dao.searchActivities(filterUserId, keyword, type, fromDate, toDate, pageIndex, pageSize);
        int totalRecords = dao.countActivities(filterUserId, keyword, type, fromDate, toDate);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        // 4. GỬI DỮ LIỆU SANG JSP
        request.setAttribute("activities", list);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", pageIndex);

        request.getRequestDispatcher("/activities/activity-dashboard.jsp").forward(request, response);
    }
}