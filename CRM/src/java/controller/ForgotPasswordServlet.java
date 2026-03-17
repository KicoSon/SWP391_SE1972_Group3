package controller;

import dal.CustomerDAO;
import dal.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {

    // mở trang forgot password
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
    }

    // xử lý gửi OTP
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String userType = request.getParameter("userType");

        boolean emailExists = false;

        try {

            if ("customer".equals(userType)) {

                CustomerDAO cdao = new CustomerDAO();
                emailExists = cdao.checkEmailExists(email);

            } else if ("staff".equals(userType)) {

                UserDAO udao = new UserDAO();
                emailExists = udao.checkEmailExists(email);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // nếu email không tồn tại
        if (!emailExists) {

            request.setAttribute("error", "Email không tồn tại trong hệ thống");
            request.setAttribute("email", email);
            request.setAttribute("userType", userType);

            request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
            return;
        }

        // tạo OTP
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);

        // lưu session
        HttpSession session = request.getSession();

        session.setAttribute("otp", otp);
        session.setAttribute("email", email);
        session.setAttribute("userType", userType);
        if (session.getAttribute("otpTime") == null) {
            session.setAttribute("otpTime", System.currentTimeMillis());
        }

        // gửi email
        util.SendMail.sendOTP(email, otp);

        response.sendRedirect("verify-otp");
    }
}
