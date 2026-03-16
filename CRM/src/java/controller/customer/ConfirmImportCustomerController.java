package controller.customer;

import dal.CustomerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.Customer;

@WebServlet("/admin/customer-import-confirm")
public class ConfirmImportCustomerController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        int size = Integer.parseInt(request.getParameter("size"));

        CustomerDAO dao = new CustomerDAO();

        for (int i = 0; i < size; i++) {

            Customer c = new Customer();

            c.setFullName(request.getParameter("name" + i));
            c.setEmail(request.getParameter("email" + i));
            c.setPhone(request.getParameter("phone" + i));
            c.setAddress(request.getParameter("address" + i));
            c.setPassword(request.getParameter("password" + i));

            // Tier
            String tierStr = request.getParameter("tier" + i);
            if (tierStr != null && !tierStr.isEmpty()) {
                c.setTierId(Integer.parseInt(tierStr));
            }

            // Owner
            String ownerStr = request.getParameter("owner" + i);
            if (ownerStr != null && !ownerStr.isEmpty()) {
                c.setOwnerId(Integer.parseInt(ownerStr));
            }

            // Status
            c.setStatus(request.getParameter("status" + i));

            dao.insert(c);
        }

        response.sendRedirect(request.getContextPath() + "/managecustomer");
    }
}