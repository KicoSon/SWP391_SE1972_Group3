package controller.customerservice;

import dal.TicketFeedbackDAO;
import model.TicketFeedback;
import model.UserSession;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Staff xem danh sách feedback từ bảng ticket_feedback.
 * URL: /customerservice/feedbackmanagement
 */
@WebServlet("/customerservice/feedbackmanagement")
public class FeedbackManagementServlet extends HttpServlet {

    private TicketFeedbackDAO feedbackDAO;

    @Override
    public void init() {
        feedbackDAO = new TicketFeedbackDAO();
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

        // Đọc filter rating
        int ratingFilter = 0;
        String ratingParam = req.getParameter("rating");
        if (ratingParam != null && !ratingParam.isEmpty()) {
            try {
                ratingFilter = Integer.parseInt(ratingParam);
                if (ratingFilter < 1 || ratingFilter > 5) ratingFilter = 0;
            } catch (NumberFormatException e) {
                ratingFilter = 0;
            }
        }

        List<TicketFeedback> feedbackList = feedbackDAO.getFeedbacksByRating(ratingFilter);
        Map<String, Object>  stats        = feedbackDAO.getFeedbackStats();

        req.setAttribute("feedbackList",  feedbackList);
        req.setAttribute("stats",         stats);
        req.setAttribute("ratingFilter",  ratingFilter);

        req.getRequestDispatcher("/customerservice/feedbackManagement.jsp")
           .forward(req, resp);
    }
}