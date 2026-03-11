package controller.activity;

import dal.ActivityDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.UserSession;

@WebServlet(name = "ActivityDeleteController", urlPatterns = {"/activities/delete"})
public class ActivityDeleteController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        UserSession user = (UserSession) session.getAttribute("userSession");
        
        response.setContentType("application/json;charset=UTF-8");
        
        // Kiểm tra quyền: Chỉ Admin / Manager mới được xóa
        if (user == null || !user.isAdmin()) {
            response.getWriter().write("{\"success\":false,\"message\":\"Bạn không có quyền xóa Activity này.\"}");
            return;
        }

        try {
            int activityId = Integer.parseInt(request.getParameter("id"));
            ActivityDAO activityDAO = new ActivityDAO();
            
            boolean success = activityDAO.deleteActivity(activityId);
            
            if (success) {
                response.getWriter().write("{\"success\":true,\"message\":\"Đã xóa Activity thành công!\"}");
            } else {
                response.getWriter().write("{\"success\":false,\"message\":\"Xóa Activity thất bại hoặc không tồn tại.\"}");
            }
        } catch (NumberFormatException e) {
            response.getWriter().write("{\"success\":false,\"message\":\"ID không hợp lệ.\"}");
        }
    }
}
