package controller;

import dal.AuthDAO;

import model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
        
        // Check if user already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userSession") != null) {
            UserSession userSession = (UserSession) session.getAttribute("userSession");
            redirectToDashboard(request, userSession, response);
            return;
        }
        
        // Check for Remember Me cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            String rememberedEmail = null;
            String rememberedUserType = null;
            
            for (Cookie cookie : cookies) {
                if ("rememberedEmail".equals(cookie.getName())) {
                    rememberedEmail = cookie.getValue();
                } else if ("rememberedUserType".equals(cookie.getName())) {
                    rememberedUserType = cookie.getValue();
                }
            }
            
            if (rememberedEmail != null && rememberedUserType != null) {
                request.setAttribute("email", rememberedEmail);
                request.setAttribute("userType", rememberedUserType);
                request.setAttribute("rememberMe", true);
            }
        }
        
        // Show login page
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String userType = request.getParameter("userType"); // "customer" or "staff"
        String rememberMe = request.getParameter("rememberMe"); // "true" or null
        
        // Validate input
        if (email == null || password == null || userType == null || 
            email.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng điền đầy đủ thông tin đăng nhập");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }
        
        UserSession userSession = null;
        
        try {
            if ("customer".equals(userType)) {
                // Customer login - check account status first
                Customer customerCheck = authDAO.checkCustomerAccount(email, password);
                if (customerCheck != null) {
                    // Account exists, check if active
                    if (!customerCheck.isActive()) {
                        request.setAttribute("error", "Tài khoản của bạn đã bị khóa bởi quản trị viên. Vui lòng liên hệ để được hỗ trợ.");
                        request.setAttribute("email", email);
                        request.setAttribute("userType", userType);
                        request.getRequestDispatcher("/login.jsp").forward(request, response);
                        return;
                    }
                    // Account is active, proceed with login
                    Customer customer = authDAO.loginCustomer(email, password);
                    if (customer != null) {
                        userSession = new UserSession(customer);
                    }
                } else {
                    // Account not found or wrong password
                    request.setAttribute("error", "Email hoặc mật khẩu không đúng");
                    request.setAttribute("email", email);
                    request.setAttribute("userType", userType);
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                    return;
                }
            } else if ("staff".equals(userType)) {
                // Staff login - check account status first
                Staff staffCheck = authDAO.checkStaffAccount(email, password);
                if (staffCheck != null) {
                    // Account exists, check if active
                    if (!staffCheck.isActive()) {
                        request.setAttribute("error", "Tài khoản của bạn đã bị khóa bởi quản trị viên. Vui lòng liên hệ bộ phận nhân sự để được hỗ trợ.");
                        request.setAttribute("email", email);
                        request.setAttribute("userType", userType);
                        request.getRequestDispatcher("/login.jsp").forward(request, response);
                        return;
                    }
                    // Account is active, proceed with login
                    Staff staff = authDAO.loginStaff(email, password);
                    if (staff != null) {
                        // Get staff roles and permissions
                        List<Role> roles = authDAO.getStaffRoles(staff.getId());
                        List<Permission> permissions = authDAO.getStaffPermissions(staff.getId());
                        
                        userSession = new UserSession(staff, roles, permissions);
                    }
                } else {
                    // Account not found or wrong password
                    request.setAttribute("error", "Email hoặc mật khẩu không đúng");
                    request.setAttribute("email", email);
                    request.setAttribute("userType", userType);
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                    return;
                }
            }
            
            if (userSession != null) {
                // Login successful
                HttpSession session = request.getSession(true);
                session.setAttribute("userSession", userSession);
                session.setMaxInactiveInterval(30 * 60); // 30 minutes
                
                // Handle Remember Me
                if ("true".equals(rememberMe)) {
                    // Create cookies for 7 days
                    Cookie emailCookie = new Cookie("rememberedEmail", email);
                    emailCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
                    emailCookie.setPath("/");
                    emailCookie.setHttpOnly(true);
                    
                    Cookie userTypeCookie = new Cookie("rememberedUserType", userType);
                    userTypeCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
                    userTypeCookie.setPath("/");
                    userTypeCookie.setHttpOnly(true);
                    
                    response.addCookie(emailCookie);
                    response.addCookie(userTypeCookie);
                } else {
                    // Remove remember me cookies if unchecked
                    Cookie emailCookie = new Cookie("rememberedEmail", "");
                    emailCookie.setMaxAge(0);
                    emailCookie.setPath("/");
                    
                    Cookie userTypeCookie = new Cookie("rememberedUserType", "");
                    userTypeCookie.setMaxAge(0);
                    userTypeCookie.setPath("/");
                    
                    response.addCookie(emailCookie);
                    response.addCookie(userTypeCookie);
                }
                
                // Redirect to appropriate dashboard
                redirectToDashboard(request, userSession, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Đã xảy ra lỗi trong quá trình đăng nhập");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
    
    private void redirectToDashboard(HttpServletRequest request, UserSession userSession, HttpServletResponse response) 
            throws IOException {
        
        String contextPath = request.getContextPath();
        
        if (userSession.isCustomer()) {
            response.sendRedirect(contextPath + "/customer/dashboard");
        } else if (userSession.isStaff()) {
            // Redirect based on role
            if (userSession.isAdmin()) {
                response.sendRedirect(contextPath + "/admin/dashboard");
            } else if (userSession.isSupportStaff()) {
                response.sendRedirect(contextPath + "/support/dashboard");
            } else if (userSession.isSaleStaff()) {
                response.sendRedirect(contextPath + "/sale/dashboard");
            } else if (userSession.isMarketingStaff()) {
                response.sendRedirect(contextPath + "/maketing/dashboard");
        } else {
            response.sendRedirect(contextPath + "/login");
        }
        }

    }
}
