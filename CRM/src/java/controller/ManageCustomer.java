package controller;

import dal.CustomerDAO;
import model.Customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ManageCustomer", urlPatterns = {"/managecustomer"})
public class ManageCustomer extends HttpServlet {

    private static final int PAGE_SIZE = 8; // số dòng / trang

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        CustomerDAO dao = new CustomerDAO();

        // Lấy param
        String search = request.getParameter("search");
        String pageParam = request.getParameter("page");

        int currentPage = 1;

        if (pageParam != null) {
            try {
                currentPage = Integer.parseInt(pageParam);
            } catch (Exception e) {
                currentPage = 1;
            }
        }

        // Lấy toàn bộ customer
        List<Customer> allCustomers = dao.getAllCustomers();

        // Search (filter tại Java cho đơn giản)
        List<Customer> filtered = new ArrayList<>();

        if (search != null && !search.trim().isEmpty()) {

            String keyword = search.toLowerCase();

            for (Customer c : allCustomers) {

                if ((c.getFullName() != null && c.getFullName().toLowerCase().contains(keyword))
                        || (c.getEmail() != null && c.getEmail().toLowerCase().contains(keyword))
                        || (c.getPhone() != null && c.getPhone().contains(keyword))
                        || (c.getAddress() != null && c.getAddress().toLowerCase().contains(keyword))) {

                    filtered.add(c);
                }
            }

        } else {
            filtered = allCustomers;
        }

        // Pagination
        int totalCustomers = filtered.size();
        int totalPages = (int) Math.ceil((double) totalCustomers / PAGE_SIZE);

        int start = (currentPage - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, totalCustomers);

        List<Customer> pageList = new ArrayList<>();

        if (start < totalCustomers) {
            pageList = filtered.subList(start, end);
        }

        // Set attribute cho JSP
        request.setAttribute("customers", pageList);
        request.setAttribute("totalCustomers", totalCustomers);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", currentPage);

        // Quyền demo (sau gắn role)
        request.setAttribute("canCreate", true);
        request.setAttribute("canEdit", true);
        request.setAttribute("canDelete", true);

        // Forward
        request.getRequestDispatcher("/admin/customers.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        CustomerDAO dao = new CustomerDAO();

        String action = request.getParameter("action");
        String idParam = request.getParameter("customerId");

        HttpSession session = request.getSession();

        if (action != null && idParam != null) {

            try {

                int id = Integer.parseInt(idParam);

                boolean result = false;

                if ("ban".equals(action)) {
                    result = dao.updateStatus(id, "Inactive");

                    if (result) {
                        session.setAttribute("successMessage", "Đã khóa tài khoản thành công!");
                    } else {
                        session.setAttribute("errorMessage", "Khóa tài khoản thất bại!");
                    }

                } else if ("unban".equals(action)) {
                    result = dao.updateStatus(id, "Active");

                    if (result) {
                        session.setAttribute("successMessage", "Đã mở khóa tài khoản!");
                    } else {
                        session.setAttribute("errorMessage", "Mở khóa thất bại!");
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("errorMessage", "Dữ liệu không hợp lệ!");
            }
        }

        // Quay lại trang list
        response.sendRedirect(request.getContextPath() + "/managecustomer");
    }
}
