package controller.customerservice;

import dal.TicketDAO;
import model.SupportTicket;
import model.UserSession;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/customerservice/addticket")
public class AddTicketServlet extends HttpServlet {

    private TicketDAO ticketDAO;

    @Override
    public void init() throws ServletException {
        ticketDAO = new TicketDAO();
    }

    // ── GET: Load trang tạo ticket ───────────────────────────
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

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

        // FIX: Load danh sách customers + staffs cho dropdown
        // Version cũ không load → JSP phải nhập Customer ID thô
        List<String[]> customerList = ticketDAO.getCustomerList();
        List<String[]> staffList    = ticketDAO.getStaffList();

        request.setAttribute("customerList", customerList);
        request.setAttribute("staffList",    staffList);

        request.getRequestDispatcher("/customerservice/addticket.jsp")
               .forward(request, response);
    }

    // ── POST: Tạo ticket mới ─────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

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

        try {
            int    customerId   = Integer.parseInt(request.getParameter("customerId"));
            int    assignedTo   = Integer.parseInt(request.getParameter("assignedTo"));
            String title        = request.getParameter("title");
            String description  = request.getParameter("description");
            String priority     = request.getParameter("priority");

            // Validate bắt buộc
            if (title == null || title.trim().isEmpty()) {
                request.setAttribute("errorMsg", "Tiêu đề không được để trống.");
                doGet(request, response); // reload form
                return;
            }

            SupportTicket t = new SupportTicket();
            t.setCustomerId(customerId);
            t.setTitle(title.trim());
            t.setDescription(description != null ? description.trim() : "");
            t.setPriority(priority);
            t.setStatus("Open");
            t.setAssignedTo(assignedTo);

            ticketDAO.insertTicket(t);

            response.sendRedirect(
                request.getContextPath() + "/customerservice/ticketlist");

        } catch (NumberFormatException e) {
            request.setAttribute("errorMsg", "Dữ liệu không hợp lệ, vui lòng thử lại.");
            doGet(request, response);
        }
    }
}