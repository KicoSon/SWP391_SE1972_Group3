package controller;

import dal.CustomerDAO;
import dal.UserDAO;
import model.Customer;
import model.User;

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

        try {
            //Xu li View + EDIT
            CustomerDAO dao = new CustomerDAO();
            String action = request.getParameter("action");
            if ("view".equals(action)) {
                int customerID = Integer.parseInt(request.getParameter("id"));
                Customer customer = dao.getCustomerByID(customerID);
                request.setAttribute("customer", customer);

                /* ===== Forward ===== */
                request.getRequestDispatcher("/admin/customer-details.jsp")
                        .forward(request, response);
                return;
            } else if ("edit".equals(action)) {
                int customerID = Integer.parseInt(request.getParameter("id"));
                UserDAO userDao = new UserDAO();
                Customer customer = dao.getCustomerByID(customerID);
                List<User> owners = userDao.getAllUsers();

                request.setAttribute("customer", customer);
                request.setAttribute("owners", owners);

                request.getRequestDispatcher("/admin/customer-form.jsp")
                        .forward(request, response);
                return;
            } else if ("add".equals(action)) {
                UserDAO userDao = new UserDAO();
                List<User> owners = userDao.getAllUsers();
//
//                request.removeAttribute("customer");
                request.setAttribute("owners", owners);

                request.getRequestDispatcher("/admin/customer-form.jsp")
                        .forward(request, response);
                return;
            }

            List<Customer> allCustomers = dao.getAllCustomers();

            //get all cus num
            int totalAll = allCustomers.size();

            // Count active + inactive acc
            int activeCount = 0;

            for (Customer c : allCustomers) {
                if ("Active".equals(c.getStatus())) {
                    activeCount++;
                }
            }

            int inactiveCount = totalAll - activeCount;

            // Fix NULL
            if (allCustomers == null) {
                allCustomers = new ArrayList<>();
            }

            /* ===== Params ===== */
            String search = request.getParameter("search");
            String pageParam = request.getParameter("page");
            String[] ranks = request.getParameterValues("rank");
            String[] statuses = request.getParameterValues("status");

            int currentPage = 1;

            if (pageParam != null) {
                try {
                    currentPage = Integer.parseInt(pageParam);
                } catch (Exception e) {
                    currentPage = 1;
                }
            }

            /* ===== Filter ===== */
            List<Customer> filtered = new ArrayList<>();

            for (Customer c : allCustomers) {
                boolean match = true;
                // Search
                if (search != null && !search.trim().isEmpty()) {
                    String keyword = search.toLowerCase();

                    if (!((c.getFullName() != null && c.getFullName().toLowerCase().contains(keyword))
                            || (c.getEmail() != null && c.getEmail().toLowerCase().contains(keyword))
                            || (c.getPhone() != null && c.getPhone().contains(keyword))
                            || (c.getAddress() != null && c.getAddress().toLowerCase().contains(keyword)))) {
                        match = false;
                    }
                }
                // Rank Filter
                if (ranks != null && ranks.length > 0) {
                    boolean rankMatch = false;

                    for (String r : ranks) {
                        if (c.getTierName().equalsIgnoreCase(r)) {
                            rankMatch = true;
                            break;
                        }
                    }
                    if (!rankMatch) {
                        match = false;
                    }
                }
                // Status Filter
                if (statuses != null && statuses.length > 0) {

                    boolean statusMatch = false;

                    for (String s : statuses) {
                        if (c.getStatus().equalsIgnoreCase(s)) {
                            statusMatch = true;
                            break;
                        }
                    }
                    if (!statusMatch) {
                        match = false;
                    }
                }
                if (match) {
                    filtered.add(c);
                }
            }
//---

            /* ===== Pagination ===== */
            int totalCustomers = filtered.size();

            int totalPages = (int) Math.ceil((double) totalCustomers / PAGE_SIZE);

            // Fix page overflow
            if (currentPage < 1) {
                currentPage = 1;
            }
            if (currentPage > totalPages && totalPages > 0) {
                currentPage = totalPages;
            }

            int start = (currentPage - 1) * PAGE_SIZE;
            int end = Math.min(start + PAGE_SIZE, totalCustomers);

            List<Customer> pageList = new ArrayList<>();

            if (start >= 0 && start < end && end <= totalCustomers) {
                pageList = filtered.subList(start, end);
            }

            /* ===== Set Attribute ===== */
            request.setAttribute("customers", pageList);
            request.setAttribute("totalCustomers", totalAll);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("currentPage", currentPage);

            request.setAttribute("activeCount", activeCount);
            request.setAttribute("inactiveCount", inactiveCount);

            request.setAttribute("canCreate", true);
            request.setAttribute("canEdit", true);
            request.setAttribute("canDelete", true);

            /* ===== Forward ===== */
            request.getRequestDispatcher("/admin/customers.jsp")
                    .forward(request, response);

        } catch (Exception e) {

            // In lỗi ra console
            e.printStackTrace();

            // In lỗi ra browser để debug
            response.setContentType("text/plain");
            response.getWriter().print("ERROR: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        CustomerDAO dao = new CustomerDAO();

        String action = request.getParameter("action");
        String idParam = request.getParameter("customerId");

        HttpSession session = request.getSession();
        if (action != null) {
            if ("ban".equals(action) || "unban".equals(action)) {

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
            } else if ("edit".equals(action)) {
                try {
                    String cusId = request.getParameter("id");
                    boolean result = false;
                    int id = Integer.parseInt(cusId);

                    String fullName = request.getParameter("fullName");
                    String email = request.getParameter("email");
                    String phone = request.getParameter("phone");
                    String password = request.getParameter("password");
                    String address = request.getParameter("address");
                    int ownerId = Integer.parseInt(request.getParameter("ownerId"));

                    // Checkbox
                    String isActiveRaw = request.getParameter("isActive");
                    String status = (isActiveRaw != null) ? "Active" : "Inactive";

                    Customer c = new Customer();

                    c.setId(id);
                    c.setFullName(fullName);
                    c.setEmail(email);
                    c.setPhone(phone);
                    c.setAddress(address);
                    c.setOwnerId(ownerId);
                    c.setStatus(status);

                    // Nếu có nhập password → update
                    if (password != null && !password.isEmpty()) {
                        c.setPassword(password);
                        result = dao.updateWithPassword(c);
                    } else {
                        result = dao.updateWithoutPassword(c);
                    }

                    if (result) {
                        session.setAttribute("successMessage", "Cập nhật thành công!");
                    } else {
                        session.setAttribute("errorMessage", "Cập nhật thất bại!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    session.setAttribute("errorMessage", "Dữ liệu không hợp lệ!");
                }
            } else if ("add".equals(action)) {
                try {
                    boolean result = false;

                    String fullName = request.getParameter("fullName");
                    String email = request.getParameter("email");
                    String phone = request.getParameter("phone");
                    String password = request.getParameter("password");
                    String address = request.getParameter("address");
                    int ownerId = Integer.parseInt(request.getParameter("ownerId"));

                    String status = "Active";

                    Customer c = new Customer();

                    c.setFullName(fullName);
                    c.setEmail(email);
                    c.setPhone(phone);
                    c.setAddress(address);
                    c.setOwnerId(ownerId);
                    c.setStatus(status);
                    c.setPassword(password);

                    result = dao.insert(c);

                    if (result) {
                        session.setAttribute("successMessage", "Thêm khách hàng thành công!");
                    } else {
                        session.setAttribute("errorMessage", "Thêm khách hàng thất bại!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    session.setAttribute("errorMessage", "Dữ liệu không hợp lệ!");
                }
            }
        }
        // Quay lại trang list
        response.sendRedirect(request.getContextPath() + "/managecustomer");
    }
}
