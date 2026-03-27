package controller.customerservice;

import dal.CustomerDAO;
import dal.TicketDAO;
import model.Customer;
import model.SupportTicket;
import model.UserSession;
import util.EmailService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * UpdateTicketServlet — cập nhật status ticket + gửi email notification.
 *
 * Thay đổi so với version cũ:
 *   1. Thêm session check + validate status hợp lệ
 *   2. Load ticket trước khi update để lấy oldStatus
 *   3. Gọi EmailService.sendTicketStatusUpdate() sau khi update thành công
 *   4. Email gửi bất đồng bộ (không block response nếu email lỗi)
 */
@WebServlet("/customerservice/updateticket")
public class UpdateTicketServlet extends HttpServlet {

    private TicketDAO    ticketDAO;
    private CustomerDAO  customerDAO;

    @Override
    public void init() {
        ticketDAO   = new TicketDAO();
        customerDAO = new CustomerDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        // ── 1. Session check ─────────────────────────────────
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

        // ── 2. Validate params ────────────────────────────────
        String ticketIdParam = request.getParameter("ticketId");
        String newStatus     = request.getParameter("status");

        if (ticketIdParam == null || newStatus == null) {
            response.sendRedirect(
                request.getContextPath() + "/customerservice/ticketlist");
            return;
        }

        if (!newStatus.equals("Open") &&
            !newStatus.equals("In Progress") &&
            !newStatus.equals("Resolved")) {
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

        // ── 3. Load ticket TRƯỚC khi update (để lấy oldStatus) ──
        SupportTicket ticket = ticketDAO.getTicketById(ticketId);
        if (ticket == null) {
            response.sendRedirect(
                request.getContextPath() + "/customerservice/ticketlist");
            return;
        }

        String oldStatus = ticket.getStatus();

        // Không update nếu status không thay đổi
        if (oldStatus.equals(newStatus)) {
            response.sendRedirect(
                request.getContextPath()
                + "/customerservice/ticketdetail?id=" + ticketId);
            return;
        }

        // ── 4. Update status ──────────────────────────────────
        ticketDAO.updateTicketStatus(ticketId, newStatus);

        // ── 5. Gửi email notification (bất đồng bộ) ─────────
        // Chạy trong thread riêng — nếu email lỗi thì không ảnh hưởng
        // đến response trả về cho user
        final SupportTicket finalTicket = ticket;
        final String        finalNew    = newStatus;
        final String        finalOld    = oldStatus;

        new Thread(() -> {
            try {
                // Load email của customer
                Customer customer = customerDAO.getCustomerById(
                        finalTicket.getCustomerId());

                if (customer != null
                        && customer.getEmail() != null
                        && !customer.getEmail().trim().isEmpty()) {

                    EmailService.sendTicketStatusUpdate(
                            customer.getEmail(),
                            customer.getFullName() != null
                                    ? customer.getFullName() : "Khách hàng",
                            finalTicket.getId(),
                            finalTicket.getTitle(),
                            finalOld,
                            finalNew
                    );
                }
            } catch (Exception e) {
                System.err.println("Email thread error: " + e.getMessage());
            }
        }).start();

        // ── 6. Set flash message và redirect ─────────────────
        String msg = "Resolved".equals(newStatus)
                ? "Ticket đã được đánh dấu Resolved. Email thông báo đã gửi cho khách hàng."
                : "Trạng thái ticket đã cập nhật thành công.";
        request.getSession().setAttribute("statusUpdateSuccess", msg);

        response.sendRedirect(
            request.getContextPath()
            + "/customerservice/ticketdetail?id=" + ticketId);
    }
}