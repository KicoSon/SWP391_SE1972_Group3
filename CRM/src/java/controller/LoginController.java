package controller;

import dal.AuthDAO;
import model.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "LoginController", urlPatterns = {"/login"})
public class LoginController extends HttpServlet {

    private AuthDAO authDAO;

    @Override
    public void init() throws ServletException {
        authDAO = new AuthDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userSession") != null) {
            redirectToDashboard(request,
                    (UserSession) session.getAttribute("userSession"),
                    response);
            return;
        }

        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String userType = request.getParameter("userType");

        if (email == null || password == null || userType == null
                || email.trim().isEmpty() || password.trim().isEmpty()) {

            request.setAttribute("error", "Vui lòng điền đầy đủ thông tin");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        UserSession userSession = null;

        try {
            // ===== CUSTOMER LOGIN =====
            if ("customer".equals(userType)) {

                Customer customer = authDAO.loginCustomer(email, password);

                if (customer == null) {
                    request.setAttribute("error", "Email hoặc mật khẩu không đúng");
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                    return;
                }

                userSession = new UserSession(customer);
            } // ===== STAFF LOGIN =====
            else if ("staff".equals(userType)) {

                Staff staff = authDAO.loginStaff(email, password);

                if (staff == null) {
                    request.setAttribute("error", "Email hoặc mật khẩu không đúng");
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                    return;
                }

                List<Role> roles = authDAO.getStaffRoles(staff.getId());
                List<Permission> permissions = authDAO.getStaffPermissions(staff.getId());

                userSession = new UserSession(staff, roles, permissions);
            }

            // ===== CREATE SESSION =====
            HttpSession session = request.getSession(true);
            session.setAttribute("userSession", userSession);
            session.setMaxInactiveInterval(30 * 60);
            System.out.println("ROLE: " + userSession.getRoles());
            redirectToDashboard(request, userSession, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi đăng nhập");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    private void redirectToDashboard(HttpServletRequest request,
            UserSession userSession,
            HttpServletResponse response) throws IOException {

        String contextPath = request.getContextPath();

        if (userSession.isCustomer()) {
            response.sendRedirect(contextPath + "/customer/dashboard");

        } else if (userSession.isStaff()) {

            if (userSession.isAdmin()) {
                response.sendRedirect(contextPath + "/admin/dashboard");
            } else if (userSession.isSupportStaff()) {
                response.sendRedirect(contextPath + "/support/dashboard");
            } else if (userSession.isSaleStaff()) {
                response.sendRedirect(contextPath + "/sale/dashboard");
            } else if (userSession.isMarketingStaff()) {
                response.sendRedirect(contextPath + "/marketingg/dashboard");
            }
        }
    }
}
