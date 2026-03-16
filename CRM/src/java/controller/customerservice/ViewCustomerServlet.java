package controller.customerservice;

import dal.CustomerDAO;
import model.Customer;
import model.UserSession;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/customerservice/viewCustomer")
public class ViewCustomerServlet extends HttpServlet {

    private CustomerDAO customerDAO;

    @Override
    public void init() {
        customerDAO = new CustomerDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

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

        // ── Validate id ──────────────────────────────────────
        String idRaw = request.getParameter("id");
        if (idRaw == null || idRaw.trim().isEmpty()) {
            response.sendRedirect(
                request.getContextPath() + "/customerservice/customerlist");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idRaw.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(
                request.getContextPath() + "/customerservice/customerlist");
            return;
        }

        // ── Load customer ────────────────────────────────────
        Customer customer = customerDAO.getCustomerById(id);
        if (customer == null) {
            response.sendRedirect(
                request.getContextPath() + "/customerservice/customerlist");
            return;
        }

        request.setAttribute("customer", customer);
        request.getRequestDispatcher("/customerservice/customerdetail.jsp")
               .forward(request, response);
    }
}