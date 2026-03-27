package controller.customerservice;

import dal.TicketFeedbackDAO;
import model.TicketFeedback;
import model.UserSession;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * SubmitFeedbackServlet
 * URL: /customerservice/submitfeedback
 *
 * Cập nhật: đọc param "redirectUrl" để sau submit redirect về
 * đúng trang gốc (customer/tickets hoặc customerservice/ticketdetail).
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

        // ── 1. Session ─────────────────────────────────────
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userSession") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        UserSession userSession = (UserSession) session.getAttribute("userSession");

        // ── 2. Chỉ customer được gửi feedback ──────────────
        if (!"CUSTOMER".equals(userSession.getUserType())) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        // ── 3. Đọc params ───────────────────────────────────
        String ticketIdParam = req.getParameter("ticketId");
        String ratingParam   = req.getParameter("rating");
        String comments      = req.getParameter("comments");
        String redirectUrl   = req.getParameter("redirectUrl");

        // Default redirect nếu không truyền
        if (redirectUrl == null || redirectUrl.trim().isEmpty()) {
            redirectUrl = req.getContextPath() + "/customer/tickets";
        }

        if (ticketIdParam == null || ratingParam == null) {
            resp.sendRedirect(redirectUrl);
            return;
        }

        int ticketId, rating;
        try {
            ticketId = Integer.parseInt(ticketIdParam);
            rating   = Integer.parseInt(ratingParam);
        } catch (NumberFormatException e) {
            resp.sendRedirect(redirectUrl);
            return;
        }

        if (rating < 1 || rating > 5) {
            session.setAttribute("feedbackError", "Rating không hợp lệ.");
            resp.sendRedirect(redirectUrl);
            return;
        }

        // ── 4. Kiểm tra đã feedback chưa ───────────────────
        if (feedbackDAO.hasFeedback(ticketId)) {
            session.setAttribute("feedbackError", "Phiếu này đã được đánh giá rồi.");
            resp.sendRedirect(redirectUrl);
            return;
        }

        // ── 5. Lưu feedback ─────────────────────────────────
        TicketFeedback fb = new TicketFeedback();
        fb.setTicketId(ticketId);
        fb.setCustomerId(userSession.getUserId());
        fb.setRating(rating);
        fb.setComments(comments != null ? comments.trim() : "");

        boolean ok = feedbackDAO.submitFeedback(fb);

        if (ok) {
            session.setAttribute("feedbackSuccess",
                "Cảm ơn bạn đã đánh giá! Phản hồi của bạn giúp chúng tôi cải thiện dịch vụ.");
        } else {
            session.setAttribute("feedbackError", "Có lỗi xảy ra, vui lòng thử lại.");
        }

        resp.sendRedirect(redirectUrl);
    }
}