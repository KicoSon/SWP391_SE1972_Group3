package controller.customerservice;

import dal.CSDashboardDAO;
import model.UserSession;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

/**
 * Servlet cho trang Dashboard — hiển thị thống kê ticket + feedback.
 *
 * URL  : /customerservice/dashboard
 * Quyền: chỉ staff
 */
@WebServlet("/customerservice/dashboard")
public class DashboardServlet extends HttpServlet {

    private final CSDashboardDAO dashboardDAO = new CSDashboardDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userSession") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        UserSession user = (UserSession) session.getAttribute("userSession");
        if (!user.isStaff()) {
            resp.sendRedirect(req.getContextPath() + "/customerservice/ticketlist");
            return;
        }

        Map<String, Object> ticketStats   = dashboardDAO.getTicketStats();
        Map<String, Object> feedbackStats = dashboardDAO.getFeedbackStats();

        req.setAttribute("ticketStats",   ticketStats);
        req.setAttribute("feedbackStats", feedbackStats);

        req.getRequestDispatcher("/customerservice/dashboard.jsp")
           .forward(req, resp);
    }
}