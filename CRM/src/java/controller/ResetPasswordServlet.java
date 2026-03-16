package controller;

import dal.CustomerDAO;
import dal.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/reset-password")
public class ResetPasswordServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("forgotPassword.jsp")
               .forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");

        if (!newPassword.equals(confirmPassword)) {

            request.setAttribute("error", "Mật khẩu xác nhận không khớp");
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
            return;
        }

        try {

            CustomerDAO dao = new CustomerDAO();

            dao.updatePasswordByEmail(email, newPassword);

            // xoá session
            session.removeAttribute("otp");
            session.removeAttribute("otpTime");
            session.removeAttribute("email");

            response.sendRedirect("login.jsp");

        } catch (Exception e) {

            request.setAttribute("error", "Có lỗi xảy ra");
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
        }
    }
}