package controller.customerservice;

import dal.TicketDAO;
import model.UserSession;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * Xử lý staff re-assign ticket sang người xử lý khác.
 *
 * URL : /customerservice/reassignticket
 * POST params: ticketId, assignedTo (users.id)
 * Quyền: chỉ staff
 */
@WebServlet("/customerservice/reassignticket")
public class ReassignTicketServlet extends HttpServlet {

    private TicketDAO ticketDAO;

    @Override
    public void init() {
        ticketDAO = new TicketDAO();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // ── 1. Auth ───────────────────────────────────────────
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userSession") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        UserSession userSession = (UserSession) session.getAttribute("userSession");
        if (!userSession.isStaff()) {
            resp.sendRedirect(req.getContextPath() + "/customerservice/ticketlist");
            return;
        }

        // ── 2. Validate params ────────────────────────────────
        String ticketIdParam   = req.getParameter("ticketId");
        String assignedToParam = req.getParameter("assignedTo");

        if (ticketIdParam == null || assignedToParam == null) {
            resp.sendRedirect(req.getContextPath() + "/customerservice/ticketlist");
            return;
        }

        int ticketId, assignedTo;
        try {
            ticketId   = Integer.parseInt(ticketIdParam);
            assignedTo = Integer.parseInt(assignedToParam);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/customerservice/ticketlist");
            return;
        }

        // ── 3. Update ─────────────────────────────────────────
        ticketDAO.updateAssignedTo(ticketId, assignedTo);

        resp.sendRedirect(
            req.getContextPath() + "/customerservice/ticketdetail?id=" + ticketId);
    }
}