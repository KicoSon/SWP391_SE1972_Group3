package controller.customer;

import dal.OrderDAO;
import dal.TicketDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import model.Order;
import model.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.SupportTicket;
import model.UserSession;

@WebServlet(name = "CustomerDashboardServlet", urlPatterns = {"/customer/dashboard"})
public class CustomerDashboardServlet extends HttpServlet {

    private OrderDAO orderDAO;
    private TicketDAO ticketDAO;

    @Override
    public void init() {
        orderDAO = new OrderDAO();
        ticketDAO = new TicketDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userSession") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserSession userSession = (UserSession) session.getAttribute("userSession");
        int customerId = userSession.getUserId();

        try {
            // ==============================
            // 1. Ticket Statistics (CHỈ LẤY FIELD CẦN)
            // ==============================
            Map<String, Integer> ticketStats = new HashMap<>();

            int open = ticketDAO.getTicketsByCustomerIdAndStatus(customerId, "Open").size();
            int inProgress = ticketDAO.getTicketsByCustomerIdAndStatus(customerId, "In Progress").size();
            int resolved = ticketDAO.getTicketsByCustomerIdAndStatus(customerId, "Resolved").size();

            int total = open + inProgress + resolved;

            ticketStats.put("OPEN", open);
            ticketStats.put("IN_PROGRESS", inProgress);
            ticketStats.put("RESOLVED", resolved);
            ticketStats.put("TOTAL", total);

            // ==============================
            // 2. Orders
            // ==============================
            int totalOrders = orderDAO.getOrderByCustomerId(customerId).size();
            List<Order> recentOrders = orderDAO.getOrderByCustomerId(customerId);

            // ==============================
            // 3. Tickets
            // ==============================
            List<SupportTicket> recentTickets = ticketDAO.getTicketsByCustomerId(customerId);

            // ==============================
            // 4. Set attributes
            // ==============================
            request.setAttribute("ticketStats", ticketStats);
            request.setAttribute("totalOrders", totalOrders);
            request.setAttribute("recentOrders", recentOrders);
            request.setAttribute("tickets", recentTickets);

            // ==============================
            // 5. Forward
            // ==============================
            request.getRequestDispatcher("/customer/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Không thể tải dashboard!");
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
}