package controller;

import dal.AdminDAO;
import dal.CustomerDAO;
import dal.StaffDAO;
import dal.UserDAO;
import model.Customer;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import model.Staff;

@WebServlet(name = "ManageCustomer", urlPatterns = {"/managecustomer"})
public class ManageCustomer extends HttpServlet {

    Pattern phonePattern = Pattern.compile("^[0-9]{10,11}$");
    Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
//            int PAGE_SIZE = Integer.parseInt(request.getParameter("pageSize"));
            int PAGE_SIZE = 8; // default

            String pageSizeParam = request.getParameter("pageSize");

            if (pageSizeParam != null && !pageSizeParam.isEmpty()) {
                PAGE_SIZE = Integer.parseInt(pageSizeParam);
            }

            request.setAttribute("pageSize", PAGE_SIZE);
            // Xu li View + EDIT
            CustomerDAO dao = new CustomerDAO();
            StaffDAO staffDAO = new StaffDAO();
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
                Customer customer = dao.getCustomerByID(customerID);
                List<Staff> owners = staffDAO.getAllSales();

                request.setAttribute("customer", customer);
                request.setAttribute("owners", owners);

                request.getRequestDispatcher("/admin/customer-form.jsp")
                        .forward(request, response);
                return;
            } else if ("add".equals(action)) {
                List<Staff> owners = staffDAO.getAllSales();

                request.removeAttribute("customer");
                request.setAttribute("owners", owners);

                request.getRequestDispatcher("/admin/customer-form.jsp")
                        .forward(request, response);
                return;
            }

            List<Customer> allCustomers = dao.getAllCustomers();

            // get all cus num
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
            // ---

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
        AdminDAO adminDAO = new AdminDAO();

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
                    // String cusId = request.getParameter("id");
                    boolean result = false;
                    // int id = Integer.parseInt(cusId);
                    int id = Integer.parseInt(idParam);

                    String fullName = request.getParameter("fullName");
                    String email = request.getParameter("email");
                    String phone = request.getParameter("phone");
                    String password = request.getParameter("password");
                    String address = request.getParameter("address");
                    int ownerId = Integer.parseInt(request.getParameter("ownerId"));

                    // Checkbox
                    String isActiveRaw = request.getParameter("isActive");
                    String status = (isActiveRaw != null) ? "Active" : "Inactive";

                    List<String> errors = new ArrayList<>();

                    // FULL NAME
                    if (fullName == null || fullName.trim().isEmpty()) {
                        errors.add("Full name is required");
                    } else if (fullName.length() > 150) {
                        errors.add("Full name max 150 characters");
                    }

                    // EMAIL
                    if (email == null || email.trim().isEmpty()) {
                        errors.add("Email cannot be empty");
                    } else if (!emailPattern.matcher(email).matches()) {
                        errors.add("Invalid email format");
                    } else if (adminDAO.isEmailExistExceptId(email, id)) {
                        errors.add("Email already exists");
                    }

                    // PHONE
                    if (phone == null || phone.trim().isEmpty()) {
                        errors.add("Phone cannot be empty");
                    } else if (!phonePattern.matcher(phone).matches()) {
                        errors.add("Phone must be 10-11 digits");
                    } else if (adminDAO.isPhoneExistExceptId(phone, id)) {
                        errors.add("Phone already exists");
                    }

                    // PASSWORD (optional khi edit)
                    if (password != null && !password.isEmpty()) {
                        if (password.length() < 6) {
                            errors.add("Password must be at least 6 characters");
                        }
                        if (password.contains(" ")) {
                            errors.add("Password cannot contain spaces");
                        }
                    }

                    // ADDRESS
                    if (address == null || address.trim().isEmpty()) {
                        errors.add("Address cannot be empty");
                    }

                    if (!errors.isEmpty()) {
                        StaffDAO staffDAO = new StaffDAO();

                        request.setAttribute("errorMessage", String.join(", ", errors));

                        Customer c = new Customer();
                        c.setId(id);
                        c.setFullName(fullName);
                        c.setEmail(email);
                        c.setPhone(phone);
                        c.setAddress(address);
                        c.setOwnerId(ownerId);
                        c.setStatus(status);
                        List<Staff> owners = staffDAO.getAllSales();

                        request.setAttribute("customer", c);    
                        request.setAttribute("owners", owners);

                        request.getRequestDispatcher("/admin/customer-form.jsp")
                                .forward(request, response);

                        return;
                    }

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
            }
        }
        if ("add".equals(action) && idParam == null) {
            try {
                boolean result = false;

                String fullName = request.getParameter("fullName");
                String email = request.getParameter("email");
                String phone = request.getParameter("phone");
                String password = request.getParameter("password");
                String address = request.getParameter("address");
                int ownerId = Integer.parseInt(request.getParameter("ownerId"));

                String status = "Active";

                List<String> errors = new ArrayList<>();

                // FULL NAME
                if (fullName == null || fullName.trim().isEmpty()) {
                    errors.add("Full name is required");
                } else if (fullName.length() > 150) {
                    errors.add("Full name max 150 characters");
                }

                // EMAIL
                if (email == null || email.trim().isEmpty()) {
                    errors.add("Email cannot be empty");
                } else if (!emailPattern.matcher(email).matches()) {
                    errors.add("Invalid email format");
                } else if (adminDAO.isEmailExist(phone)) {
                    errors.add("Email already exists");
                }

                // PHONE
                if (phone == null || phone.trim().isEmpty()) {
                    errors.add("Phone cannot be empty");
                } else if (!phonePattern.matcher(phone).matches()) {
                    errors.add("Phone must be 10-11 digits");
                } else if (adminDAO.isPhoneExist(phone)) {
                    errors.add("Phone already exists");
                }

                // PASSWORD (optional khi edit)
                if (password != null && !password.isEmpty()) {
                    if (password.length() < 6) {
                        errors.add("Password must be at least 6 characters");
                    }
                    if (password.contains(" ")) {
                        errors.add("Password cannot contain spaces");
                    }
                }

                // ADDRESS
                if (address == null || address.trim().isEmpty()) {
                    errors.add("Address cannot be empty");
                }

                // Nếu có lỗi
                if (!errors.isEmpty()) {
                    session.setAttribute("errorMessage", String.join(", ", errors));
                    response.sendRedirect(request.getContextPath() + "/managecustomer");
                    return;
                }

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
            response.sendRedirect(request.getContextPath() + "/managecustomer?success=add");
            return;
        }
        // Quay lại trang list
        response.sendRedirect(request.getContextPath() + "/managecustomer");
    }
}
