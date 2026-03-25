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

    // Quyền xem detail: Admin, Creator hoặc người có trong participants.
    private boolean canAccessActivity(UserSession userSession, Activity activity, ActivityDAO dao) {
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

        try {
            String idRaw = request.getParameter("id");
            if (idRaw == null || idRaw.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/sale/dashboard");
                return;
            }
            int activityId = Integer.parseInt(idRaw);

            ActivityDAO dao = new ActivityDAO();

            Activity activity = dao.getActivityById(activityId);
            if (activity == null) {
                response.sendRedirect(request.getContextPath() + "/sale/dashboard?error=notfound");
                return;
            }

            HttpSession session = request.getSession();
            UserSession userSession = (UserSession) session.getAttribute("userSession");

            if (userSession == null || userSession.getStaff() == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            if (!canAccessActivity(userSession, activity, dao)) {
                response.sendRedirect(request.getContextPath() + "/sale/dashboard?error=nopermission");
                return;
            }

            // Quyền edit trên màn detail chỉ dành cho Admin/Creator.
            String canEdit = "NONE";

            if (userSession != null && userSession.getStaff() != null) {
                int currentUserId = userSession.getStaff().getId();

                if (userSession.isAdmin() || activity.getCreatedBy() == currentUserId) {
                    canEdit = "FULL";
                }
                else if (dao.isUserInvolvedInActivity(activityId, currentUserId)) {
                    canEdit = "NONE";
                }
            }

            request.setAttribute("canEdit", canEdit);

            List<String> participants = dao.getParticipantsFullInfo(activityId);
            List<ActivityAttachment> attachments = dao.getAttachmentsByActivityId(activityId);

            List<ActivityComment> comments = dao.getCommentsByActivityId(activityId);

            request.setAttribute("activity", activity);
            request.setAttribute("participants", participants);
            request.setAttribute("attachments", attachments);
            request.setAttribute("commentList", comments);

            request.getRequestDispatcher("/activities/activity-detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/sale/dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        try {
            int activityId = Integer.parseInt(request.getParameter("activityId"));
            String content = request.getParameter("content");

            HttpSession session = request.getSession();
            UserSession userSession = (UserSession) session.getAttribute("userSession");

            if (userSession != null && userSession.getStaff() != null) {
                int userId = userSession.getStaff().getId();

                ActivityDAO dao = new ActivityDAO();
                Activity activity = dao.getActivityById(activityId);
                if (activity == null || !canAccessActivity(userSession, activity, dao)) {
                    response.sendRedirect(request.getContextPath() + "/sale/dashboard?error=nopermission");
                    return;
                }

                // Nhánh submit form comment kiểu truyền thống (không phải AJAX API).
                dao.insertComment(activityId, userId, content);
            }

            response.sendRedirect(request.getContextPath() + "/activities/detail?id=" + activityId);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/sale/dashboard");
        }
    }
}