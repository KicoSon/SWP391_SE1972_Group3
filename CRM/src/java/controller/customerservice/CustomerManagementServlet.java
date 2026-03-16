package controller.customerservice;

import dal.CustomerDAO;
import model.Customer;
import model.UserSession;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/customerservice/customerlist")
public class CustomerManagementServlet extends HttpServlet {

    private CustomerDAO customerDAO;

    @Override
    public void init() throws ServletException {
        customerDAO = new CustomerDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // ── Session check ────────────────────────────────────
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userSession") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserSession userSession = (UserSession) session.getAttribute("userSession");
        if (!userSession.isStaff()) {
            response.sendRedirect(request.getContextPath() + "/customerservice/ticketlist");
            return;
        }

        String search       = request.getParameter("search");
        String statusFilter = request.getParameter("statusFilter");

        try {
            List<Customer> customerList =
                    customerDAO.filterCustomers(search, statusFilter);

            request.setAttribute("customerList",  customerList);
            request.setAttribute("search",        search);
            request.setAttribute("statusFilter",  statusFilter);

            request.getRequestDispatcher("/customerservice/customerlist.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi tải danh sách khách hàng!");
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