package controller.activity;

import dal.ActivityDAO;
import model.activity.Activity;
import model.activity.ActivityAttachment;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ActivityDetailController", urlPatterns = {"/activities/detail"})
public class ActivityDetailController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String idRaw = request.getParameter("id");
            if (idRaw == null || idRaw.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/sale/dashboard");
                return;
            }
            int activityId = Integer.parseInt(idRaw);

            ActivityDAO dao = new ActivityDAO();
            
            // 1. Lấy thông tin chính
            Activity activity = dao.getActivityById(activityId);
            if (activity == null) {
                response.sendRedirect(request.getContextPath() + "/sale/dashboard?error=notfound");
                return;
            }

            // 2. Lấy danh sách người tham gia
            List<String> participants = dao.getParticipantsFullInfo(activityId);
            
            // 3. Lấy danh sách file đính kèm
            List<ActivityAttachment> attachments = dao.getAttachmentsByActivityId(activityId);

            // Gắn vào request
            request.setAttribute("activity", activity);
            request.setAttribute("participants", participants);
            request.setAttribute("attachments", attachments);
            
            request.getRequestDispatcher("/activities/activity-detail.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/sale/dashboard");
        }
    }
}