package controller.customerservice;

import dal.CustomerFeedbackDAO;
import dal.TicketFeedbackDAO;
import model.CustomerFeedback;
import model.TicketFeedback;
import model.UserSession;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * FeedbackManagementServlet — tách riêng 2 section:
 *   Section 1: customer_feedback  (feedback chung)
 *   Section 2: ticket_feedback    (feedback theo ticket)
 *
 * Cả 2 section đều hỗ trợ filter theo rating độc lập qua query param:
 *   ?ratingCf=1..5  → filter customer_feedback
 *   ?ratingTf=1..5  → filter ticket_feedback
 */
@WebServlet("/customerservice/feedbackmanagement")
public class FeedbackManagementServlet extends HttpServlet {

    private CustomerFeedbackDAO customerFeedbackDAO;
    private TicketFeedbackDAO   ticketFeedbackDAO;

    @Override
    public void init() {
        customerFeedbackDAO = new CustomerFeedbackDAO();
        ticketFeedbackDAO   = new TicketFeedbackDAO();
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

        // ── Filter rating độc lập cho từng section ────────────
        int ratingCf = parseRating(req.getParameter("ratingCf")); // customer_feedback
        int ratingTf = parseRating(req.getParameter("ratingTf")); // ticket_feedback

        // ── Section 1: Customer feedback ──────────────────────
        List<CustomerFeedback> customerFeedbackList =
                customerFeedbackDAO.getFeedbacksByRating(ratingCf);
        Map<String, Object> customerStats =
                customerFeedbackDAO.getFeedbackStats();

        // ── Section 2: Ticket feedback ────────────────────────
        List<TicketFeedback> ticketFeedbackList =
                ticketFeedbackDAO.getFeedbacksByRating(ratingTf);
        Map<String, Object> ticketStats =
                ticketFeedbackDAO.getFeedbackStats();

        // ── Đẩy vào request scope ─────────────────────────────
        req.setAttribute("customerFeedbackList", customerFeedbackList);
        req.setAttribute("customerStats",        customerStats);
        req.setAttribute("ratingCf",             ratingCf);

        req.setAttribute("ticketFeedbackList",   ticketFeedbackList);
        req.setAttribute("ticketStats",          ticketStats);
        req.setAttribute("ratingTf",             ratingTf);

        req.getRequestDispatcher("/customerservice/feedbackManagement.jsp")
           .forward(req, resp);
    }

    // ── Helper: parse và validate rating param ────────────────
    private int parseRating(String param) {
        if (param == null || param.isEmpty()) return 0;
        try {
            int r = Integer.parseInt(param);
            return (r >= 1 && r <= 5) ? r : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}