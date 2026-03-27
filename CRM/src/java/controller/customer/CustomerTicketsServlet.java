package controller.customer;

import dal.TicketDAO;
import dal.TicketFeedbackDAO;
import model.SupportTicket;
import model.TicketFeedback;
import model.UserSession;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CustomerTicketsServlet
 * URL: /customer/tickets
 * Quyền: chỉ customer
 *
 * GET params (tùy chọn):
 *   statusFilter — lọc theo status (Open | In Progress | Resolved | "")
 *
 * Attributes trả về:
 *   ticketList      — List<SupportTicket> danh sách ticket
 *   feedbackMap     — Map<Integer, TicketFeedback> ticketId → feedback (null nếu chưa có)
 *   statusFilter    — giá trị filter hiện tại
 *   countOpen       — số ticket Open
 *   countProgress   — số ticket In Progress
 *   countResolved   — số ticket Resolved
 */
@WebServlet(name = "CustomerTicketsServlet", urlPatterns = {"/customer/tickets"})
public class CustomerTicketsServlet extends HttpServlet {

    private TicketDAO         ticketDAO;
    private TicketFeedbackDAO feedbackDAO;

    @Override
    public void init() {
        ticketDAO   = new TicketDAO();
        feedbackDAO = new TicketFeedbackDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ── 1. Session check ──────────────────────────────────
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userSession") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserSession userSession = (UserSession) session.getAttribute("userSession");

        // Chỉ customer được vào trang này
        if (!"CUSTOMER".equals(userSession.getUserType())) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        int customerId = userSession.getUserId();

        // ── 2. Đọc filter ─────────────────────────────────────
        String statusFilter = request.getParameter("statusFilter");
        if (statusFilter == null) statusFilter = "";

        try {
            // ── 3. Load danh sách ticket ─────────────────────
            List<SupportTicket> ticketList;
            if (statusFilter.isEmpty()) {
                ticketList = ticketDAO.getTicketsByCustomerId(customerId);
            } else {
                ticketList = ticketDAO.getTicketsByCustomerIdAndStatus(customerId, statusFilter);
            }

            // ── 4. Đếm theo status (luôn dùng tổng, không theo filter) ──
            List<SupportTicket> allTickets = ticketDAO.getTicketsByCustomerId(customerId);
            int countOpen     = 0;
            int countProgress = 0;
            int countResolved = 0;
            for (SupportTicket t : allTickets) {
                if ("Open".equals(t.getStatus()))        countOpen++;
                else if ("In Progress".equals(t.getStatus())) countProgress++;
                else if ("Resolved".equals(t.getStatus()))    countResolved++;
            }

            // ── 5. Load feedback map cho các ticket Resolved ──
            // key = ticketId, value = TicketFeedback (null nếu chưa có)
            Map<Integer, TicketFeedback> feedbackMap = new HashMap<>();
            for (SupportTicket t : ticketList) {
                if ("Resolved".equals(t.getStatus())) {
                    TicketFeedback fb = feedbackDAO.getFeedbackByTicketId(t.getId());
                    feedbackMap.put(t.getId(), fb); // null nếu chưa feedback
                }
            }

            // ── 6. Set attributes ─────────────────────────────
            request.setAttribute("ticketList",    ticketList);
            request.setAttribute("feedbackMap",   feedbackMap);
            request.setAttribute("statusFilter",  statusFilter);
            request.setAttribute("countOpen",     countOpen);
            request.setAttribute("countProgress", countProgress);
            request.setAttribute("countResolved", countResolved);
            request.setAttribute("totalTickets",  allTickets.size());

            request.getRequestDispatcher("/customer/customer-tickets.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Không thể tải danh sách phiếu hỗ trợ!");
            response.sendRedirect(request.getContextPath() + "/customer/dashboard");
        }
    }
}