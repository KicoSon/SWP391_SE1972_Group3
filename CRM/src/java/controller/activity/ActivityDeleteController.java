package controller.activity;

import dal.ActivityDAO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.UserSession;
import model.activity.ActivityAttachment;

@WebServlet(name = "ActivityDeleteController", urlPatterns = {"/activities/delete"})
public class ActivityDeleteController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        UserSession user = (UserSession) session.getAttribute("userSession");
        
        response.setContentType("application/json;charset=UTF-8");
        
        if (user == null || !user.isAdmin()) {
            response.getWriter().write("{\"success\":false,\"message\":\"Bạn không có quyền xóa Activity này.\"}");
            return;
        }

        try {
            int activityId = Integer.parseInt(request.getParameter("id"));
            ActivityDAO activityDAO = new ActivityDAO();
            List<ActivityAttachment> attachments = activityDAO.getAttachmentsByActivityId(activityId);
            
            boolean success = activityDAO.deleteActivity(activityId);
            
            if (success) {
                deletePhysicalFiles(attachments);
                response.getWriter().write("{\"success\":true,\"message\":\"Đã xóa Activity thành công!\"}");
            } else {
                response.getWriter().write("{\"success\":false,\"message\":\"Xóa Activity thất bại hoặc không tồn tại.\"}");
            }
        } catch (NumberFormatException e) {
            response.getWriter().write("{\"success\":false,\"message\":\"ID không hợp lệ.\"}");
        }
    }

    private void deletePhysicalFiles(List<ActivityAttachment> attachments) {
        if (attachments == null || attachments.isEmpty()) {
            return;
        }

        String uploadPath = getServletContext().getInitParameter("uploadDirectory");
        if (uploadPath == null || uploadPath.isEmpty()) {
            return;
        }

        for (ActivityAttachment attachment : attachments) {
            if (attachment == null || attachment.getFilePath() == null || attachment.getFilePath().isEmpty()) {
                continue;
            }

            try {
                String storedFileName = Paths.get(attachment.getFilePath()).getFileName().toString();
                File physicalFile = new File(uploadPath, storedFileName);
                if (physicalFile.exists() && !physicalFile.delete()) {
                    System.err.println("[ActivityDelete] Không thể xóa file vật lý: " + physicalFile.getAbsolutePath());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
