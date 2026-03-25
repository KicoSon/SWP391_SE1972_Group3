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

        String keyword = request.getParameter("search");
        String type = request.getParameter("type");
        String fromDate = request.getParameter("from");
        String toDate = request.getParameter("to");

        int pageIndex = 1;
        int pageSize = 10;
        try {
            if (request.getParameter("page") != null) {
                pageIndex = Integer.parseInt(request.getParameter("page"));
            }
        } catch (NumberFormatException e) {
            pageIndex = 1;
        }

        // Admin xem toàn bộ; staff chỉ xem dữ liệu theo userId của mình.
        Integer filterUserId = null;
        if (!userSession.isAdmin()) {
            filterUserId = userSession.getStaff().getId();
        }

        ActivityDAO dao = new ActivityDAO();

        // Bảng chính dashboard.
        List<Activity> list = dao.searchActivities(filterUserId, keyword, type, fromDate, toDate, pageIndex, pageSize);
        int totalRecords = dao.countActivities(filterUserId, keyword, type, fromDate, toDate);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        // 5 chỉ số tổng hợp cho Overview.
        int[] stats = dao.getActivityStats(filterUserId);

        request.setAttribute("statTotal", stats[0]);
        request.setAttribute("statPlanned", stats[1]);
        request.setAttribute("statInProgress", stats[2]);
        request.setAttribute("statCompleted", stats[3]);
        request.setAttribute("statOverdue", stats[4]);

        request.setAttribute("activities", list);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", pageIndex);
        
        request.setAttribute("searchMsg", keyword != null ? keyword : "");
        request.setAttribute("typeMsg", type != null ? type : "All");
        request.setAttribute("fromMsg", fromDate != null ? fromDate : "");
        request.setAttribute("toMsg", toDate != null ? toDate : "");

        request.getRequestDispatcher("/activities/activity-dashboard.jsp").forward(request, response);
    }
}
