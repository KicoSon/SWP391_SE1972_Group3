package controller.activity;

import dal.ActivityDAO;
import model.activity.Activity;
import model.activity.ActivityParticipant;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "ActivityDetailApiController", urlPatterns = {"/activities/api/detail"})
public class ActivityDetailApiController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        
        try {
            String idParam = request.getParameter("id");
            
            if (idParam == null || idParam.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                PrintWriter out = response.getWriter();
                out.write("{\"error\": \"ID không được để trống\"}");
                return;
            }
            
            int activityId = Integer.parseInt(idParam);
            
            ActivityDAO dao = new ActivityDAO();
            Activity activity = dao.getActivityById(activityId);
            
            if (activity == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                PrintWriter out = response.getWriter();
                out.write("{\"error\": \"Hoạt động không tồn tại\"}");
                return;
            }
            
            // Convert Activity to JSON (Manual)
            String json = buildActivityJson(activity, dao);
            
            PrintWriter out = response.getWriter();
            out.write(json);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = response.getWriter();
            out.write("{\"error\": \"ID không hợp lệ\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = response.getWriter();
            out.write("{\"error\": \"Lỗi server: " + e.getMessage() + "\"}");
        }
    }

    // Method để build JSON string từ Activity object
    private String buildActivityJson(Activity activity, ActivityDAO dao) {
        StringBuilder json = new StringBuilder();
        java.util.List<ActivityParticipant> participantRows = dao.getParticipantsByActivityId(activity.getId());
        int ownerId = 0;

        for (ActivityParticipant ap : participantRows) {
            if ("Owner".equals(ap.getRole())) {
                ownerId = ap.getUserId();
                break;
            }
        }

        json.append("{");
        json.append("\"id\": ").append(activity.getId()).append(",");
        json.append("\"title\": \"").append(escapeJson(activity.getTitle())).append("\",");
        json.append("\"type\": \"").append(activity.getType()).append("\",");
        json.append("\"description\": \"").append(escapeJson(activity.getDescription())).append("\",");
        json.append("\"status\": \"").append(activity.getStatus()).append("\",");
        json.append("\"priority\": \"").append(activity.getPriority()).append("\",");
        
        // Handle null values for optional fields
        if (activity.getCustomerId() != null) {
            json.append("\"customerId\": ").append(activity.getCustomerId()).append(",");
        }
        if (activity.getLeadId() != null) {
            json.append("\"leadId\": ").append(activity.getLeadId()).append(",");
        }
        if (activity.getOpportunityId() != null) {
            json.append("\"opportunityId\": ").append(activity.getOpportunityId()).append(",");
        }
        
        if (activity.getDueDate() != null) {
            json.append("\"dueDate\": ").append(activity.getDueDate().getTime()).append(",");
        }
        
        json.append("\"createdBy\": ").append(activity.getCreatedBy()).append(",");
        json.append("\"ownerId\": ").append(ownerId).append(",");

        json.append("\"participantIds\": [");
        boolean firstParticipant = true;
        for (ActivityParticipant ap : participantRows) {
            if (!"Owner".equals(ap.getRole())) {
                if (!firstParticipant) {
                    json.append(",");
                }
                json.append(ap.getUserId());
                firstParticipant = false;
            }
        }
        json.append("],");
        
        // Thêm danh sách participants
        java.util.List<String> participants = dao.getParticipantsFullInfo(activity.getId());
        json.append("\"participants\": [");
        for (int i = 0; i < participants.size(); i++) {
            String participant = participants.get(i);
            // Nếu JSON không hợp lệ, chỉ lấy tên
            String[] parts = participant.split(" - ");
            json.append("\"").append(escapeJson(parts[0])).append("\"");
            if (i < participants.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        
        json.append("}");
        
        return json.toString();
    }

    // Helper method để escape JSON string (xử lý quote, backslash, newline, etc.)
    private String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
