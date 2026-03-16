package controller.customerservice;

import dal.TicketDAO;
import model.UserSession;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/customerservice/updateticket")
public class UpdateTicketServlet extends HttpServlet {

    private TicketDAO ticketDAO;

    @Override
    public void init() {
        ticketDAO = new TicketDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        // ── 1. Kiểm tra session ──────────────────────────────
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userSession") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserSession userSession = (UserSession) session.getAttribute("userSession");
        if (!userSession.isStaff()) {
            response.sendRedirect(
                request.getContextPath() + "/customerservice/ticketlist");
            return;
        }

        // ── 2. Validate params ───────────────────────────────
        String ticketIdParam = request.getParameter("ticketId");
        String status        = request.getParameter("status");

        if (ticketIdParam == null || status == null) {
            response.sendRedirect(
                request.getContextPath() + "/customerservice/ticketlist");
            return;
        }

        // Validate status chỉ nhận 3 giá trị hợp lệ
        if (!status.equals("Open") &&
            !status.equals("In Progress") &&
            !status.equals("Resolved")) {
            response.sendRedirect(
                request.getContextPath() + "/customerservice/ticketlist");
            return;
        }

        int ticketId;
        try {
            ticketId = Integer.parseInt(ticketIdParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(
                request.getContextPath() + "/customerservice/ticketlist");
            return;
        }

        // ── 3. Update (đã fix tên bảng + update_at trong DAO) ─
        ticketDAO.updateTicketStatus(ticketId, status);

        response.sendRedirect(
            request.getContextPath() + "/customerservice/ticketdetail?id=" + ticketId);
    }
}