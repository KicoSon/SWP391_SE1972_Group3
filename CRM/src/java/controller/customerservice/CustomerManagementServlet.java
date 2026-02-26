package controller.customerservice;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dal.CustomerDAO;
import model.Customer;

import java.io.IOException;
import java.util.List;

@WebServlet("/customerservice/customerlist")
public class CustomerManagementServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

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

        String search = request.getParameter("search");
        String statusFilter = request.getParameter("statusFilter");

        try {

            List<Customer> customerList
                    = customerDAO.filterCustomers(search, statusFilter);

            request.setAttribute("customerList", customerList);

            request.getRequestDispatcher("/customerservice/customerlist.jsp")
                    .forward(request, response);

        } catch (Exception e) {

            e.printStackTrace();

            request.setAttribute("errorMessage",
                    "Lỗi khi tải danh sách khách hàng!");

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
