package controller.customerservice;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dal.TicketDAO;
import model.SupportTicket;
import model.UserSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/customerservice/ticketlist")
public class TicketManagementServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

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

        String search = request.getParameter("search");
        String statusFilter = request.getParameter("statusFilter");

        try {

            UserSession userSession =
                    (UserSession) request.getSession()
                            .getAttribute("userSession");

            if (userSession == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            List<SupportTicket> ticketList;

            // CUSTOMER chỉ xem ticket của mình
            if (userSession.isCustomer()) {

                int customerId =
                        userSession.getCustomer().getId();

                ticketList =
                        ticketDAO.getTicketsByCustomerId(customerId);

            }
            // STAFF xem tất cả + filter
            else {

                ticketList =
                        ticketDAO.filterTickets(search, statusFilter);
            }

            request.setAttribute("ticketList", ticketList);

            request.getRequestDispatcher(
                    "/customerservice/ticketlist.jsp")
                    .forward(request, response);

        } catch (Exception e) {

            e.printStackTrace();

            request.setAttribute("errorMessage",
                    "Lỗi khi tải danh sách ticket!");

            request.getRequestDispatcher("/error.jsp")
                    .forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        doGet(request, response);

    }
}