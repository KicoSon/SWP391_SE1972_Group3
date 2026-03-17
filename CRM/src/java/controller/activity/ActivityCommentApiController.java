package controller.activity;

import dal.ActivityDAO;
import model.UserSession;
import model.activity.ActivityComment;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet(name = "ActivityCommentApiController", urlPatterns = {"/api/comments"})
public class ActivityCommentApiController extends HttpServlet {

    private boolean canAccessActivity(UserSession userSession, model.activity.Activity activity, ActivityDAO dao) {
        if (userSession == null || userSession.getStaff() == null || activity == null) {
            return false;
        }

        int currentUserId = userSession.getStaff().getId();
        return userSession.isAdmin()
                || activity.getCreatedBy() == currentUserId
                || dao.isUserInvolvedInActivity(activity.getId(), currentUserId);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        
        try {
            int activityId = Integer.parseInt(request.getParameter("activityId"));

            HttpSession session = request.getSession(false);
            UserSession userSession = session != null
                    ? (UserSession) session.getAttribute("userSession")
                    : null;

            ActivityDAO dao = new ActivityDAO();
            model.activity.Activity activity = dao.getActivityById(activityId);

            if (!canAccessActivity(userSession, activity, dao)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("{\"error\": \"Forbidden\"}");
                return;
            }

            List<ActivityComment> comments = dao.getCommentsByActivityId(activityId);
            
            StringBuilder json = new StringBuilder();
            json.append("[");
            for (int i = 0; i < comments.size(); i++) {
                ActivityComment c = comments.get(i);
                json.append("{");
                json.append("\"commenterName\": \"").append(escapeJson(c.getCommenterName())).append("\",");
                json.append("\"content\": \"").append(escapeJson(c.getContent())).append("\",");
                String dateStr = new SimpleDateFormat("dd/MM/yyyy 'lúc' HH:mm").format(c.getCreatedAt());
                json.append("\"createdAt\": \"").append(dateStr).append("\"");
                json.append("}");
                
                if (i < comments.size() - 1) json.append(",");
            }
            json.append("]");
            
            PrintWriter out = response.getWriter();
            out.print(json.toString());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        
        try {
            int activityId = Integer.parseInt(request.getParameter("activityId"));
            String content = request.getParameter("content");
            
            HttpSession session = request.getSession();
            UserSession userSession = (UserSession) session.getAttribute("userSession");
            
            if (userSession != null && userSession.getStaff() != null) {
                int userId = userSession.getStaff().getId();
                
                ActivityDAO dao = new ActivityDAO();
                model.activity.Activity activity = dao.getActivityById(activityId);

                if (!canAccessActivity(userSession, activity, dao)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN); //khong co quyen them comment
                    response.getWriter().write("{\"error\": \"Forbidden\"}");
                    return;
                }

                dao.insertComment(activityId, userId, content);
                
                response.getWriter().write("{\"status\": \"success\"}");
            } else {
                response.setStatus(401); //chua dang nhap
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500); //loi server
        }
    }
    
    private String escapeJson(String text) {
        if (text == null) return "";
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
}