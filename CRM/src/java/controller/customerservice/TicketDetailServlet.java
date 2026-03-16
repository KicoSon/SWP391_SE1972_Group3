package controller.customerservice;

import dal.TicketDAO;
import dal.TicketFeedbackDAO;
import model.SupportTicket;
import model.TicketFeedback;
import model.UserSession;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/customerservice/ticketdetail")
public class TicketDetailServlet extends HttpServlet {

    private TicketDAO ticketDAO;
    private TicketFeedbackDAO feedbackDAO;

    @Override
    public void init() {
        ticketDAO    = new TicketDAO();
        feedbackDAO  = new TicketFeedbackDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        // ── 1. Session ────────────────────────────────────────
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userSession") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        UserSession userSession = (UserSession) session.getAttribute("userSession");

        // ── 2. Validate id ────────────────────────────────────
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/customerservice/ticketlist");
            return;
        }

        int ticketId;
        try {
            ticketId = Integer.parseInt(idParam.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/customerservice/ticketlist");
            return;
        }

        // ── 3. Load ticket (JOIN đầy đủ) ──────────────────────
        SupportTicket ticket = ticketDAO.getTicketById(ticketId);
        if (ticket == null) {
            response.sendRedirect(request.getContextPath() + "/customerservice/ticketlist");
            return;
        }

        // ── 4. Phân quyền: customer chỉ xem ticket của mình ──
        if (userSession.isCustomer()) {
            if (ticket.getCustomerId() != userSession.getUserId()) {
                response.sendRedirect(request.getContextPath() + "/customerservice/ticketlist");
                return;
            }
        }

        // ── 5. Load feedback data (Bước 1) ────────────────────
        TicketFeedback existingFeedback = feedbackDAO.getFeedbackByTicketId(ticketId);

        // Customer có thể feedback khi:
        // ticket Resolved + đúng customer của ticket + chưa có feedback
        boolean canFeedback = "Resolved".equals(ticket.getStatus())
                && userSession.isCustomer()
                && ticket.getCustomerId() == userSession.getUserId()
                && existingFeedback == null;

        // ── 6. Load staff list cho re-assign dropdown (Bước 2) ─
        // Chỉ load khi là staff (customer không cần)
        List<String[]> staffList = null;
        if (userSession.isStaff()) {
            staffList = ticketDAO.getStaffList();
        }

        // ── 7. Đẩy vào request scope ──────────────────────────
        request.setAttribute("ticket",           ticket);
        request.setAttribute("userSession",      userSession);
        request.setAttribute("existingFeedback", existingFeedback);
        request.setAttribute("canFeedback",      canFeedback);
        request.setAttribute("staffList",        staffList);

        request.getRequestDispatcher("/customerservice/ticketdetail.jsp")
               .forward(request, response);
    }
}