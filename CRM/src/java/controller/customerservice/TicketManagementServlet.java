package controller.customerservice;

import dal.TicketDAO;
import model.SupportTicket;
import model.UserSession;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/customerservice/ticketlist")
public class TicketManagementServlet extends HttpServlet {

    private TicketDAO ticketDAO;

    @Override
    public void init() throws ServletException {
        ticketDAO = new TicketDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userSession") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserSession userSession = (UserSession) session.getAttribute("userSession");

        try {
            if (userSession.isCustomer()) {
                // ── CUSTOMER: chỉ xem ticket của mình ──────────
                int customerId = userSession.getCustomer().getId();
                List<SupportTicket> myTickets =
                        ticketDAO.getTicketsByCustomerId(customerId);

                request.setAttribute("myTickets", myTickets);

            } else {
                // ── STAFF: xem tất cả + filter + my tickets ─────
                String search       = request.getParameter("search");
                String statusFilter = request.getParameter("statusFilter");

                List<SupportTicket> ticketList =
                        ticketDAO.filterTickets(search, statusFilter);

                // FIX: dùng getUserId() thay vì getStaff().getId()
                // để khớp với users.id trong DB
                int staffId = userSession.getUserId();
                List<SupportTicket> myTickets =
                        ticketDAO.getTicketsByStaffId(staffId);

                request.setAttribute("ticketList",    ticketList);
                request.setAttribute("myTickets",     myTickets);
                request.setAttribute("search",        search);
                request.setAttribute("statusFilter",  statusFilter);
            }

            request.setAttribute("userSession", userSession);

            request.getRequestDispatcher("/customerservice/ticketlist.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi tải danh sách ticket!");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}