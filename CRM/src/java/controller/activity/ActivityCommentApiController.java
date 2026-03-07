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

    // 1. DO_GET: Lấy danh sách comment về (để tự động cập nhật)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        
        try {
            int activityId = Integer.parseInt(request.getParameter("activityId"));
            ActivityDAO dao = new ActivityDAO();
            List<ActivityComment> comments = dao.getCommentsByActivityId(activityId);
            
            // Chuyển List Java thành chuỗi JSON thủ công (Vì chưa dùng thư viện Gson/Jackson)
            StringBuilder json = new StringBuilder();
            json.append("[");
            for (int i = 0; i < comments.size(); i++) {
                ActivityComment c = comments.get(i);
                json.append("{");
                json.append("\"commenterName\": \"").append(escapeJson(c.getCommenterName())).append("\",");
                json.append("\"content\": \"").append(escapeJson(c.getContent())).append("\",");
                // Format ngày tháng đẹp
                String dateStr = new SimpleDateFormat("dd/MM/yyyy 'lúc' HH:mm").format(c.getCreatedAt());
                json.append("\"createdAt\": \"").append(dateStr).append("\"");
                json.append("}");
                
                if (i < comments.size() - 1) json.append(","); // Dấu phẩy giữa các phần tử
            }
            json.append("]");
            
            PrintWriter out = response.getWriter();
            out.print(json.toString());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 2. DO_POST: Nhận comment mới (Gửi ngầm)
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
                dao.insertComment(activityId, userId, content);
                
                // Trả về thành công
                response.getWriter().write("{\"status\": \"success\"}");
            } else {
                response.setStatus(401); // Lỗi chưa đăng nhập
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
        }
    }
    
    // Hàm phụ để xử lý ký tự đặc biệt trong JSON
    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}