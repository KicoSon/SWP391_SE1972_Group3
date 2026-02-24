package controller;

import java.io.IOException;
import jakarta.servlet.ServletException; // Hoặc javax.servlet tùy phiên bản Tomcat
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.UserSession;

// Map đường dẫn này bắt đầu bằng /sale/ để tận dụng AuthorizationFilter có sẵn
@WebServlet(name = "ActivityDashboardController", urlPatterns = {"/sale/dashboard"})
public class ActivityDashboardController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Code này forward request vào bên trong thư mục bảo mật WEB-INF
        request.getRequestDispatcher("/activities/activity-dashboard.jsp")
               .forward(request, response);
    }
}