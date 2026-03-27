package controller.customerservice;

import dal.CSDashboardDAO;
import model.UserSession;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Map;

@WebServlet("/customerservice/dashboard")
public class DashboardServlet extends HttpServlet {

    private CSDashboardDAO dashboardDAO;

    @Override
    public void init() {
        dashboardDAO = new CSDashboardDAO();
    }

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
        Map<String, Object> overdueStats  = dashboardDAO.getOverdueStats(); // THÊM MỚI

        req.setAttribute("ticketStats",   ticketStats);
        req.setAttribute("feedbackStats", feedbackStats);
        req.setAttribute("overdueStats",  overdueStats);

        req.getRequestDispatcher("/customerservice/dashboard.jsp")
           .forward(req, resp);
    }
}