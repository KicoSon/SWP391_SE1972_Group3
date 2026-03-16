package controller.customerservice;

import dal.TicketFeedbackDAO;
import model.TicketFeedback;
import model.UserSession;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * Xử lý customer gửi feedback cho ticket đã Resolved.
 *
 * URL : /customerservice/submitfeedback
 * POST params: ticketId, rating (1–5), comments (optional)
 * Quyền: chỉ customer, đúng ticket của họ, ticket Resolved, chưa feedback
 */
@WebServlet("/customerservice/submitfeedback")
public class SubmitFeedbackServlet extends HttpServlet {

    private TicketFeedbackDAO feedbackDAO;

    @Override
    public void init() {
        feedbackDAO = new TicketFeedbackDAO();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        // ── 1. Kiểm tra session ───────────────────────────────
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userSession") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        UserSession userSession = (UserSession) session.getAttribute("userSession");

        // ── 2. Chỉ customer được gửi feedback ─────────────────
        if (!userSession.isCustomer()) {
            resp.sendRedirect(req.getContextPath() + "/customerservice/ticketlist");
            return;
        }

        // ── 3. Lấy và validate params ─────────────────────────
        String ticketIdParam = req.getParameter("ticketId");
        String ratingParam   = req.getParameter("rating");
        String comments      = req.getParameter("comments");

        if (ticketIdParam == null || ratingParam == null) {
            resp.sendRedirect(req.getContextPath() + "/customerservice/ticketlist");
            return;
        }

        int ticketId, rating;
        try {
            ticketId = Integer.parseInt(ticketIdParam);
            rating   = Integer.parseInt(ratingParam);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/customerservice/ticketlist");
            return;
        }

        String redirectUrl = req.getContextPath() +
                "/customerservice/ticketdetail?id=" + ticketId;

        if (rating < 1 || rating > 5) {
            session.setAttribute("feedbackError", "Rating không hợp lệ.");
            resp.sendRedirect(redirectUrl);
            return;
        }

        // ── 4. Kiểm tra chưa feedback ─────────────────────────
        if (feedbackDAO.hasFeedback(ticketId)) {
            session.setAttribute("feedbackError", "Ticket này đã được đánh giá rồi.");
            resp.sendRedirect(redirectUrl);
            return;
        }

        // ── 5. Lưu feedback ───────────────────────────────────
        TicketFeedback fb = new TicketFeedback();
        fb.setTicketId(ticketId);
        fb.setCustomerId(userSession.getUserId()); // customers.id
        fb.setRating(rating);
        fb.setComments(comments != null ? comments.trim() : "");

        boolean ok = feedbackDAO.submitFeedback(fb);

        if (ok) {
            session.setAttribute("feedbackSuccess", "Cảm ơn bạn đã đánh giá!");
        } else {
            session.setAttribute("feedbackError", "Có lỗi xảy ra, vui lòng thử lại.");
        }

        resp.sendRedirect(redirectUrl);
    }
}