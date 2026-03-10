package controller.activity;

import dal.ActivityDAO;
import model.activity.Activity;
import model.activity.ActivityAttachment;
import model.activity.ActivityComment;
import model.UserSession;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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

            // 2. Tính toán quyền canEdit theo 4 cấp ưu tiên
            HttpSession session = request.getSession();
            UserSession userSession = (UserSession) session.getAttribute("userSession");

            String canEdit = "NONE"; // Mặc định: Chỉ xem + Bình luận

            if (userSession != null && userSession.getStaff() != null) {
                int currentUserId = userSession.getStaff().getId();

                // Ưu tiên 1: Manager / Admin → Toàn quyền
                if (userSession.isAdmin()) {
                    canEdit = "FULL";
                }
                // Ưu tiên 2: Người tạo (Creator) → Toàn quyền
                else if (activity.getCreatedBy() == currentUserId) {
                    canEdit = "FULL";
                }
                // Ưu tiên 3: PIC hoặc Participant → Quyền hạn chế (chỉ sửa Status)
                else if (dao.isUserInvolvedInActivity(activityId, currentUserId)) {
                    canEdit = "LIMITED";
                }
                // Ưu tiên 4: Còn lại → Chỉ xem + Bình luận (canEdit = "NONE")
            }

            request.setAttribute("canEdit", canEdit);

            // 3. Lấy danh sách người tham gia & File đính kèm
            List<String> participants = dao.getParticipantsFullInfo(activityId);
            List<ActivityAttachment> attachments = dao.getAttachmentsByActivityId(activityId);

            // 4. Lấy danh sách COMMENT
            List<ActivityComment> comments = dao.getCommentsByActivityId(activityId);

            // Gắn vào request
            request.setAttribute("activity", activity);
            request.setAttribute("participants", participants);
            request.setAttribute("attachments", attachments);
            request.setAttribute("commentList", comments);

            request.getRequestDispatcher("/activities/activity-detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/sale/dashboard");
        }
    }

    // Xử lý khi bấm nút "Gửi bình luận" — Ai thấy được activity đều được comment
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        try {
            // Lấy dữ liệu từ Form
            int activityId = Integer.parseInt(request.getParameter("activityId"));
            String content = request.getParameter("content");

            // Lấy ID người đang đăng nhập
            HttpSession session = request.getSession();
            UserSession userSession = (UserSession) session.getAttribute("userSession");

            if (userSession != null && userSession.getStaff() != null) {
                int userId = userSession.getStaff().getId();

                // Gọi DAO lưu vào DB
                ActivityDAO dao = new ActivityDAO();
                dao.insertComment(activityId, userId, content);
            }

            // Load lại trang chi tiết để thấy comment mới
            response.sendRedirect(request.getContextPath() + "/activities/detail?id=" + activityId);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/sale/dashboard");
        }
    }
}