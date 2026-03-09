package controller.marketing;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import model.UserSession;

import java.io.IOException;

@WebServlet("/marketing/profile")
public class ProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // chưa login
        if (session == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login");

            return;
        }

        UserSession userSession =
                (UserSession) session.getAttribute("userSession");

        // session hết hạn
        if (userSession == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login");

            return;
        }

        // gửi sang JSP
        request.setAttribute("user", userSession);

        request.getRequestDispatcher(
                "/marketingg/profile.jsp")
                .forward(request, response);

    }

}