package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/verify-otp")
public class VerifyOTPServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("email") == null) {

            response.sendRedirect("forgot-password");
            return;
        }

        request.setAttribute("email", session.getAttribute("email"));

        request.getRequestDispatcher("verify-otp.jsp")
               .forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        String action = request.getParameter("action");

        String sessionOTP = (String) session.getAttribute("otp");
        String email = (String) session.getAttribute("email");
        Long otpTime = (Long) session.getAttribute("otpTime");

        request.setAttribute("email", email);

        // resend OTP
        if ("resend".equals(action)) {

            String newOTP = String.valueOf((int)(Math.random()*900000)+100000);

            session.setAttribute("otp", newOTP);
            session.setAttribute("otpTime", System.currentTimeMillis());

            util.SendMail.sendOTP(email, newOTP);

            request.setAttribute("success", "OTP mới đã được gửi");
            request.getRequestDispatcher("verify-otp.jsp").forward(request, response);
            return;
        }

        String userOTP = request.getParameter("otp");

        // check hết hạn 5 phút
        if (otpTime == null || System.currentTimeMillis() - otpTime > 5*60*1000) {

            request.setAttribute("error", "OTP đã hết hạn");
            request.getRequestDispatcher("verify-otp.jsp").forward(request, response);
            return;
        }

        if (userOTP != null && userOTP.equals(sessionOTP)) {

            response.sendRedirect("reset-password.jsp");

        } else {

            request.setAttribute("error", "OTP không đúng");
            request.getRequestDispatcher("verify-otp.jsp").forward(request, response);
        }
    }
}