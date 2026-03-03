package controller.customerservice;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import dal.TicketDAO;
import model.SupportTicket;
import model.UserSession;

import java.io.IOException;

@WebServlet("/customerservice/addticket")
public class AddTicketServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private TicketDAO ticketDAO;

    @Override
    public void init() throws ServletException {
        ticketDAO = new TicketDAO();
    }

    // ======================
    // LOAD ADD PAGE
    // ======================
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {

            UserSession userSession =
                    (UserSession) request.getSession()
                            .getAttribute("userSession");

            if (userSession == null || !userSession.isStaff()) {
                response.sendRedirect(request.getContextPath()
                        + "/customerservice/ticketlist");
                return;
            }

            request.getRequestDispatcher(
                    "/customerservice/addticket.jsp")
                    .forward(request, response);

        } catch (Exception e) {

            e.printStackTrace();

            request.setAttribute("errorMessage",
                    "Không thể mở trang tạo ticket!");

            request.getRequestDispatcher("/error.jsp")
                    .forward(request, response);
        }
    }

    // ======================
    // CREATE TICKET
    // ======================
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        try {

            UserSession userSession =
                    (UserSession) request.getSession()
                            .getAttribute("userSession");

            if (userSession == null || !userSession.isStaff()) {
                response.sendRedirect("ticketlist");
                return;
            }

            int customerId =
                    Integer.parseInt(request.getParameter("customerId"));

            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String priority = request.getParameter("priority");

            SupportTicket t = new SupportTicket();
            t.setCustomerId(customerId);
            t.setTitle(title);
            t.setDescription(description);
            t.setPriority(priority);
            t.setStatus("Open");
            t.setAssignedTo(userSession.getStaff().getId());

            ticketDAO.insertTicket(t);

            response.sendRedirect(
                request.getContextPath()
                + "/customerservice/ticketlist");

        } catch (Exception e) {

            e.printStackTrace();

            request.setAttribute("errorMessage",
                    "Lỗi khi tạo ticket!");

            request.getRequestDispatcher("/error.jsp")
                    .forward(request, response);
        }
    }
}
