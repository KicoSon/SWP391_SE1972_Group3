package controller.activity;

import dal.ActivityDAO;
import dal.CustomerDAO;
import dal.OpportunityDAO;
import dal.StaffDAO;
import dal.LeadDAO;
import model.activity.Activity;
import model.Customer;
import model.UserSession;
import model.Lead;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet(name = "ActivityCreateController", urlPatterns = {"/activities/create"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB đệm
        maxFileSize = 1024 * 1024 * 10,       // Tối đa 10MB / 1 file
        maxRequestSize = 1024 * 1024 * 50     // Tối đa 50MB toàn form
)
public class ActivityCreateController extends HttpServlet {

    // =========================================================================
    // HELPER: Tính quyền canEdit cho một activity cụ thể
    // =========================================================================
    private String resolveCanEdit(Activity activity, UserSession userSession) {
        if (activity == null || userSession == null || userSession.getStaff() == null) {
            return "NONE";
        }
        int currentUserId = userSession.getStaff().getId();

        // Ưu tiên 1: Manager / Admin → Toàn quyền
        if (userSession.isAdmin()) {
            return "FULL";
        }
        // Ưu tiên 2: Người tạo (Creator) → Toàn quyền
        if (activity.getCreatedBy() == currentUserId) {
            return "FULL";
        }
        // Ưu tiên 3: PIC hoặc Participant → Chỉ sửa Status
        ActivityDAO dao = new ActivityDAO();
        if (dao.isUserInvolvedInActivity(activity.getId(), currentUserId)) {
            return "LIMITED";
        }
        // Ưu tiên 4: Còn lại → Chỉ xem
        return "NONE";
    }

    // =========================================================================
    // DO_GET: Chuẩn bị dữ liệu để hiển thị Form
    // =========================================================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        UserSession userSession = (UserSession) session.getAttribute("userSession");

        if (userSession == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Marketing không được vào trang Create
        if (userSession.isMarketingStaff()) {
            response.sendRedirect(request.getContextPath() + "/sale/dashboard?error=nopermission");
            return;
        }

        // --- Load Customer list theo vai trò ---
        CustomerDAO customerDAO = new CustomerDAO();
        List<Customer> customerList;
        if (userSession.isSaleStaff() && !userSession.isAdmin()) {
            // Sale: Chỉ hiện khách của mình (lọc theo owner_id)
            if (userSession.getStaff() != null) {
                customerList = customerDAO.getCustomersByOwnerId(userSession.getStaff().getId());
            } else {
                customerList = customerDAO.getAllActiveCustomers();
            }
        } else {
            // Manager, Support: Hiện TẤT CẢ
            customerList = customerDAO.getAllActiveCustomers();
        }
        request.setAttribute("customerList", customerList);

        // --- Load Lead list theo vai trò ---
        LeadDAO leadDAO = new LeadDAO();
        List<Lead> leadList;
        if (userSession.isSaleStaff() && !userSession.isAdmin()) {
            long currentStaffId = (long) userSession.getStaff().getId();
            leadList = leadDAO.getLeadsBySaleId(currentStaffId);
        } else {
            leadList = leadDAO.getLeadsBySaleId(null); // Tất cả
        }
        request.setAttribute("leads", leadList);

        // --- Load Opportunity list theo vai trò ---
        OpportunityDAO oppDAO = new OpportunityDAO();
        List<model.sales.Opportunity> oppList;
        if (userSession.isSaleStaff() && !userSession.isAdmin() && userSession.getStaff() != null) {
            int currentStaffId = userSession.getStaff().getId();
            oppList = oppDAO.filterOpportunities(null, null, null, currentStaffId);
        } else {
            oppList = oppDAO.filterOpportunities(null, null, null, null);
        }
        request.setAttribute("oppList", oppList);

        // --- Load Staff list ---
        StaffDAO staffDAO = new StaffDAO();
        request.setAttribute("staffList", staffDAO.getAllActiveStaff());

        // --- Nếu là Edit mode, load activity và tính canEdit ---
        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                int id = Integer.parseInt(idParam);
                ActivityDAO dao = new ActivityDAO();
                Activity existingActivity = dao.getActivityById(id);

                if (existingActivity != null) {
                    String canEdit = resolveCanEdit(existingActivity, userSession);

                    // Nếu không có quyền gì → redirect về dashboard
                    if ("NONE".equals(canEdit)) {
                        response.sendRedirect(request.getContextPath() + "/sale/dashboard?error=nopermission");
                        return;
                    }

                    request.setAttribute("activity", existingActivity);
                    request.setAttribute("canEdit", canEdit);
                }
            } catch (NumberFormatException e) {
                System.out.println("ID không hợp lệ: " + e.getMessage());
            }
        } else {
            // Create mode: Người tạo sẽ có FULL quyền với activity mới của mình
            request.setAttribute("canEdit", "FULL");
        }

        request.getRequestDispatcher("/activities/activity-create.jsp").forward(request, response);
    }

    // =========================================================================
    // DO_POST: Xử lý lưu dữ liệu từ Form
    // =========================================================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        try {
            HttpSession session = request.getSession();
            UserSession userSession = (UserSession) session.getAttribute("userSession");

            if (userSession == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            if (userSession.getStaff() == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            // --- Kiểm tra Edit mode hay Create mode ---
            String idParam = request.getParameter("id");
            int activityId = -1;
            boolean isEditMode = false;

            if (idParam != null && !idParam.isEmpty()) {
                try {
                    activityId = Integer.parseInt(idParam);
                    isEditMode = true;
                } catch (NumberFormatException e) {
                    // ID không hợp lệ → coi là Create mới
                }
            }

            ActivityDAO dao = new ActivityDAO();

            // =====================================================================
            // EDIT MODE: Kiểm tra quyền server-side trước khi làm bất cứ điều gì
            // =====================================================================
            if (isEditMode) {
                Activity existingActivity = dao.getActivityById(activityId);
                if (existingActivity == null) {
                    response.sendRedirect(request.getContextPath() + "/sale/dashboard?error=notfound");
                    return;
                }

                String canEdit = resolveCanEdit(existingActivity, userSession);

                if ("NONE".equals(canEdit)) {
                    // Không có quyền → từ chối
                    response.sendRedirect(request.getContextPath() + "/sale/dashboard?error=nopermission");
                    return;
                }

                if ("LIMITED".equals(canEdit)) {
                    // Quyền hạn chế: CHỈ cập nhật Status
                    String newStatus = request.getParameter("status");
                    if (newStatus != null && !newStatus.isEmpty()) {
                        dao.updateActivityStatusOnly(activityId, newStatus);
                    }
                    response.sendRedirect(request.getContextPath() + "/sale/dashboard?msg=updated");
                    return;
                }

                // canEdit == "FULL" → Tiếp tục update đầy đủ bên dưới
                Activity act = new Activity();
                act.setId(activityId);
                act.setTitle(request.getParameter("title"));
                act.setDescription(request.getParameter("description"));
                act.setType(request.getParameter("type"));
                act.setStatus(request.getParameter("status"));
                String priority = request.getParameter("priority");
                act.setPriority((priority != null && !priority.isEmpty()) ? priority : "Medium");
                act.setCreatedBy(userSession.getStaff().getId());

                // Thời gian
                String dateStr = request.getParameter("date");
                String timeStr = request.getParameter("time");
                if (dateStr != null && !dateStr.isEmpty() && timeStr != null && !timeStr.isEmpty()) {
                    act.setDueDate(Timestamp.valueOf(dateStr + " " + timeStr + ":00"));
                } else {
                    act.setDueDate(null);
                }
                act.setReminderAt(null);

                // Related To
                String relatedTo = request.getParameter("related_to");
                if (relatedTo != null && !relatedTo.isEmpty()) {
                    String[] parts = relatedTo.split("-");
                    if (parts.length == 2) {
                        try {
                            if ("opp".equals(parts[0])) act.setOpportunityId(Integer.parseInt(parts[1]));
                            else if ("lead".equals(parts[0])) act.setLeadId(Long.parseLong(parts[1]));
                        } catch (NumberFormatException ex) { /* bỏ qua */ }
                    }
                }

                // Customer
                String customerVal = request.getParameter("customer");
                if (customerVal != null && !customerVal.isEmpty()) {
                    try { act.setCustomerId(Integer.parseInt(customerVal)); }
                    catch (NumberFormatException ex) { /* bỏ qua */ }
                }

                boolean updateSuccess = dao.updateActivity(act);
                if (updateSuccess) {
                    // Xử lý upload file mới nếu có
                    handleAttachmentUpload(request, activityId, dao);
                    response.sendRedirect(request.getContextPath() + "/sale/dashboard?msg=updated");
                } else {
                    request.setAttribute("error", "Lỗi: Không thể cập nhật hoạt động. Vui lòng thử lại.");
                    doGet(request, response);
                }
                return;
            }

            // =====================================================================
            // CREATE MODE: Tạo mới activity
            // =====================================================================
            Activity act = new Activity();
            act.setTitle(request.getParameter("title"));
            act.setDescription(request.getParameter("description"));
            act.setType(request.getParameter("type"));
            act.setStatus(request.getParameter("status"));
            String priority = request.getParameter("priority");
            act.setPriority((priority != null && !priority.isEmpty()) ? priority : "Medium");
            act.setCreatedBy(userSession.getStaff().getId());

            // Thời gian
            String dateStr = request.getParameter("date");
            String timeStr = request.getParameter("time");
            if (dateStr != null && !dateStr.isEmpty() && timeStr != null && !timeStr.isEmpty()) {
                act.setDueDate(Timestamp.valueOf(dateStr + " " + timeStr + ":00"));
            } else {
                act.setDueDate(null);
            }
            act.setReminderAt(null);

            // Related To
            String relatedTo = request.getParameter("related_to");
            if (relatedTo != null && !relatedTo.isEmpty()) {
                String[] parts = relatedTo.split("-");
                if (parts.length == 2) {
                    try {
                        if ("opp".equals(parts[0])) act.setOpportunityId(Integer.parseInt(parts[1]));
                        else if ("lead".equals(parts[0])) act.setLeadId(Long.parseLong(parts[1]));
                    } catch (NumberFormatException ex) { /* bỏ qua */ }
                }
            }

            // Customer
            String customerVal = request.getParameter("customer");
            if (customerVal != null && !customerVal.isEmpty()) {
                try { act.setCustomerId(Integer.parseInt(customerVal)); }
                catch (NumberFormatException ex) { /* bỏ qua */ }
            }

            // Participants
            List<Integer> participantIds = new ArrayList<>();
            String ownerIdRaw = request.getParameter("owner");
            if (ownerIdRaw != null && !ownerIdRaw.isEmpty()) {
                try { participantIds.add(Integer.parseInt(ownerIdRaw)); }
                catch (NumberFormatException ex) { /* bỏ qua */ }
            }
            String otherParticipants = request.getParameter("participantIds");
            if (otherParticipants != null && !otherParticipants.isEmpty()) {
                for (String pId : otherParticipants.split(",")) {
                    try {
                        int id = Integer.parseInt(pId.trim());
                        if (!participantIds.contains(id)) participantIds.add(id);
                    } catch (NumberFormatException ex) { /* bỏ qua */ }
                }
            }

            int newActivityId = dao.insertActivity(act, participantIds);
            if (newActivityId > 0) {
                handleAttachmentUpload(request, newActivityId, dao);
                response.sendRedirect(request.getContextPath() + "/sale/dashboard?msg=success");
            } else {
                request.setAttribute("error", "Lỗi: Không thể lưu vào Database. Vui lòng thử lại.");
                doGet(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            doGet(request, response);
        }
    }

    // =========================================================================
    // HELPER: Xử lý upload file đính kèm
    // =========================================================================
    private void handleAttachmentUpload(HttpServletRequest request, int activityId, ActivityDAO attachmentDao)
            throws Exception {
        String uploadPath = getServletContext().getInitParameter("uploadDirectory");
        if (uploadPath == null || uploadPath.isEmpty()) return;

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        for (Part part : request.getParts()) {
            if ("attachments".equals(part.getName()) && part.getSize() > 0) {
                String fileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
                String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
                part.write(uploadPath + File.separator + uniqueFileName);
                attachmentDao.insertAttachment(activityId, fileName, "uploads/" + uniqueFileName);
            }
        }
    }
}
