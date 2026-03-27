package controller.activity;

import dal.ActivityDAO;
import dal.CustomerDAO;
import dal.OpportunityDAO;
import dal.StaffDAO;
import dal.LeadDAO;
import model.activity.Activity;
import model.activity.ActivityParticipant;
import model.Customer;
import model.UserSession;
import model.Lead;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
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
    fileSizeThreshold = 1024 * 1024 * 2,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 50
)
public class ActivityCreateController extends HttpServlet {

    private String resolveCanEdit(Activity activity, UserSession userSession) {
        if (activity == null || userSession == null || userSession.getStaff() == null) {
            return "NONE";
        }

        int currentUserId = userSession.getStaff().getId();
        ActivityDAO dao = new ActivityDAO();

        if (userSession.isAdmin() || activity.getCreatedBy() == currentUserId) {
            return "FULL";
        }

        if (dao.isUserInvolvedInActivity(activity.getId(), currentUserId)) {
            return "NONE";
        }

        return "NONE";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        UserSession userSession = (UserSession) session.getAttribute("userSession");

        if (userSession == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (userSession.isMarketingStaff()) {
            response.sendRedirect(request.getContextPath() + "/sale/dashboard?error=nopermission");
            return;
        }

        CustomerDAO customerDAO = new CustomerDAO();
        List<Customer> customerList;
        if (userSession.isSaleStaff() && !userSession.isAdmin()) {
            if (userSession.getStaff() != null) {
                customerList = customerDAO.getCustomersByOwnerId(userSession.getStaff().getId());
            } else {
                customerList = customerDAO.getAllActiveCustomers();
            }
        } else {
            customerList = customerDAO.getAllActiveCustomers();
        }
        request.setAttribute("customerList", customerList);

        LeadDAO leadDAO = new LeadDAO();
        List<Lead> leadList;
        if (userSession.isSaleStaff() && !userSession.isAdmin()) {
            long currentStaffId = (long) userSession.getStaff().getId();
            leadList = leadDAO.getLeadsBySaleId(currentStaffId);
        } else {
            leadList = leadDAO.getLeadsBySaleId(null);
        }
        request.setAttribute("leads", leadList);

        OpportunityDAO oppDAO = new OpportunityDAO();
        List<model.sales.Opportunity> oppList;
        if (userSession.isSaleStaff() && !userSession.isAdmin() && userSession.getStaff() != null) {
            int currentStaffId = userSession.getStaff().getId();
            oppList = oppDAO.filterOpportunities(null, null, null, currentStaffId);
        } else {
            oppList = oppDAO.filterOpportunities(null, null, null, null);
        }
        request.setAttribute("oppList", oppList);

        StaffDAO staffDAO = new StaffDAO();
        request.setAttribute("staffList", staffDAO.getAllActiveStaff());

        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                int id = Integer.parseInt(idParam);
                ActivityDAO dao = new ActivityDAO();
                Activity existingActivity = dao.getActivityById(id);

                if (existingActivity != null) {
                    String canEdit = resolveCanEdit(existingActivity, userSession);

                    if ("NONE".equals(canEdit)) {
                        response.sendRedirect(request.getContextPath() + "/sale/dashboard?error=nopermission");
                        return;
                    }

                    request.setAttribute("activity", existingActivity);

                    List<ActivityParticipant> existingParticipants = dao.getParticipantsByActivityId(id);
                    Integer ownerId = null;
                    List<Integer> participantIds = new ArrayList<>();
                    for (ActivityParticipant ap : existingParticipants) {
                        if ("Owner".equals(ap.getRole())) {
                            ownerId = ap.getUserId();
                        } else {
                            participantIds.add(ap.getUserId());
                        }
                    }
                    request.setAttribute("activityOwnerId", ownerId);
                    request.setAttribute("activityParticipantIds", participantIds);
                    request.setAttribute("existingAttachments", dao.getAttachmentsByActivityId(id));
                    request.setAttribute("canEdit", canEdit);
                }
            } catch (NumberFormatException e) {
                System.out.println("ID không hợp lệ: " + e.getMessage());
            }
        } else {
            request.setAttribute("canEdit", "FULL");
        }

        request.getRequestDispatcher("/activities/activity-create.jsp").forward(request, response);
    }

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

            String idParam = request.getParameter("id");
            int activityId = -1;
            boolean isEditMode = false;

            if (idParam != null && !idParam.isEmpty()) {
                try {
                    activityId = Integer.parseInt(idParam);
                    isEditMode = true;
                } catch (NumberFormatException e) {
                }
            }

            ActivityDAO dao = new ActivityDAO();

            if (isEditMode) {
                Activity existingActivity = dao.getActivityById(activityId);
                if (existingActivity == null) {
                    response.sendRedirect(request.getContextPath() + "/sale/dashboard?error=notfound");
                    return;
                }

                String canEdit = resolveCanEdit(existingActivity, userSession);

                if ("NONE".equals(canEdit)) {
                    response.sendRedirect(request.getContextPath() + "/sale/dashboard?error=nopermission");
                    return;
                }

                Activity act = new Activity();
                act.setId(activityId);
                act.setTitle(request.getParameter("title"));
                act.setDescription(request.getParameter("description"));
                act.setType(request.getParameter("type"));
                act.setStatus(request.getParameter("status"));
                String priority = request.getParameter("priority");
                act.setPriority((priority != null && !priority.isEmpty()) ? priority : "Medium");
                act.setCreatedBy(userSession.getStaff().getId());

                String dateStr = request.getParameter("date");
                String timeStr = request.getParameter("time");
                if (dateStr != null && !dateStr.isEmpty() && timeStr != null && !timeStr.isEmpty()) {
                    act.setDueDate(Timestamp.valueOf(dateStr + " " + timeStr + ":00"));
                } else {
                    act.setDueDate(null);
                }
                act.setReminderAt(null);

                String relatedTo = request.getParameter("related_to");
                if (relatedTo != null && !relatedTo.isEmpty()) {
                    String[] parts = relatedTo.split("-");
                    if (parts.length == 2) {
                        try {
                            if ("opp".equals(parts[0])) act.setOpportunityId(Integer.parseInt(parts[1]));
                            else if ("lead".equals(parts[0])) act.setLeadId(Long.parseLong(parts[1]));
                        } catch (NumberFormatException ex) { }
                    }
                }

                String customerVal = request.getParameter("customer");
                if (customerVal != null && !customerVal.isEmpty()) {
                    try { act.setCustomerId(Integer.parseInt(customerVal)); }
                    catch (NumberFormatException ex) { }
                }

                boolean updateSuccess = dao.updateActivity(act);
                if (updateSuccess) {
                    List<Integer> participantIds = new ArrayList<>();
                    String ownerIdRaw = request.getParameter("owner");
                    if (ownerIdRaw != null && !ownerIdRaw.isEmpty()) {
                        try { participantIds.add(Integer.parseInt(ownerIdRaw)); }
                        catch (NumberFormatException ex) { }
                    }
                    String otherParticipants = request.getParameter("participantIds");
                    if (otherParticipants != null && !otherParticipants.isEmpty()) {
                        for (String pId : otherParticipants.split(",")) {
                            try {
                                int id = Integer.parseInt(pId.trim());
                                if (!participantIds.contains(id)) participantIds.add(id);
                            } catch (NumberFormatException ex) { }
                        }
                    }
                    if (!participantIds.isEmpty()) {
                        dao.updateActivityParticipants(activityId, participantIds);
                    }

                    ActivityDAO attachmentDao = new ActivityDAO();
                    deleteRemovedAttachments(request, activityId, attachmentDao);

                    handleAttachmentUpload(request, activityId, attachmentDao);
                    response.sendRedirect(request.getContextPath() + "/sale/dashboard?msg=updated");
                } else {
                    request.setAttribute("error", "Lỗi: Không thể cập nhật hoạt động. Vui lòng thử lại.");
                    doGet(request, response);
                }
                return;
            }

            Activity act = new Activity();
            act.setTitle(request.getParameter("title"));
            act.setDescription(request.getParameter("description"));
            act.setType(request.getParameter("type"));
            act.setStatus(request.getParameter("status"));
            String priority = request.getParameter("priority");
            act.setPriority((priority != null && !priority.isEmpty()) ? priority : "Medium");
            act.setCreatedBy(userSession.getStaff().getId());

            String dateStr = request.getParameter("date");
            String timeStr = request.getParameter("time");
            if (dateStr != null && !dateStr.isEmpty() && timeStr != null && !timeStr.isEmpty()) {
                act.setDueDate(Timestamp.valueOf(dateStr + " " + timeStr + ":00"));
            } else {
                act.setDueDate(null);
            }
            act.setReminderAt(null);

            String relatedTo = request.getParameter("related_to");
            if (relatedTo != null && !relatedTo.isEmpty()) {
                String[] parts = relatedTo.split("-");
                if (parts.length == 2) {
                    try {
                        if ("opp".equals(parts[0])) act.setOpportunityId(Integer.parseInt(parts[1]));
                        else if ("lead".equals(parts[0])) act.setLeadId(Long.parseLong(parts[1]));
                    } catch (NumberFormatException ex) { }
                }
            }

            String customerVal = request.getParameter("customer");
            if (customerVal != null && !customerVal.isEmpty()) {
                try { act.setCustomerId(Integer.parseInt(customerVal)); }
                catch (NumberFormatException ex) { }
            }

            List<Integer> participantIds = new ArrayList<>();
            String ownerIdRaw = request.getParameter("owner");
            if (ownerIdRaw != null && !ownerIdRaw.isEmpty()) {
                try { participantIds.add(Integer.parseInt(ownerIdRaw)); }
                catch (NumberFormatException ex) { }
            }
            String otherParticipants = request.getParameter("participantIds");
            if (otherParticipants != null && !otherParticipants.isEmpty()) {
                for (String pId : otherParticipants.split(",")) {
                    try {
                        int id = Integer.parseInt(pId.trim());
                        if (!participantIds.contains(id)) participantIds.add(id);
                    } catch (NumberFormatException ex) { }
                }
            }

            int newActivityId = dao.insertActivity(act, participantIds);
            if (newActivityId > 0) {
                handleAttachmentUpload(request, newActivityId, new ActivityDAO());
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

    private void deleteRemovedAttachments(HttpServletRequest request, int activityId, ActivityDAO attachmentDao)
            throws Exception {
        Set<Integer> keptIds = new HashSet<>();
        List<String> existingAttachmentIds = getMultipartFieldValues(request, "existingAttachmentIds");
        if (!existingAttachmentIds.isEmpty()) {
            for (String rawId : existingAttachmentIds) {
                try {
                    keptIds.add(Integer.parseInt(rawId.trim()));
                } catch (NumberFormatException ex) {
                }
            }
        }

        Set<Integer> removedIds = new HashSet<>();
        String removedIdsRaw = getMultipartFieldValue(request, "removedAttachmentIds");
        if (removedIdsRaw != null && !removedIdsRaw.trim().isEmpty()) {
            for (String rawId : removedIdsRaw.split(",")) {
                try {
                    removedIds.add(Integer.parseInt(rawId.trim()));
                } catch (NumberFormatException ex) {
                }
            }
        }

        List<model.activity.ActivityAttachment> existingAttachments = attachmentDao.getAttachmentsByActivityId(activityId);
        for (model.activity.ActivityAttachment attachment : existingAttachments) {
            if (!keptIds.isEmpty()) {
                if (!keptIds.contains(attachment.getId())) {
                    removedIds.add(attachment.getId());
                }
            } else if (existingAttachmentIds.isEmpty()) {
                removedIds.add(attachment.getId());
            }
        }

        if (removedIds.isEmpty()) {
            return;
        }

        String uploadPath = getServletContext().getInitParameter("uploadDirectory");
        for (model.activity.ActivityAttachment attachment : existingAttachments) {
            if (!removedIds.contains(attachment.getId())) {
                continue;
            }

            try {
                if (uploadPath != null && !uploadPath.isEmpty() && attachment.getFilePath() != null) {
                    String storedFileName = Paths.get(attachment.getFilePath()).getFileName().toString();
                    File physicalFile = new File(uploadPath, storedFileName);
                    if (physicalFile.exists()) {
                        physicalFile.delete();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        attachmentDao.deleteAttachmentsByIds(activityId, new ArrayList<>(removedIds));
    }

    private String getMultipartFieldValue(HttpServletRequest request, String fieldName) throws Exception {
        List<String> values = getMultipartFieldValues(request, fieldName);
        return values.isEmpty() ? null : values.get(0);
    }

    private List<String> getMultipartFieldValues(HttpServletRequest request, String fieldName) throws Exception {
        List<String> values = new ArrayList<>();

        String[] parameterValues = request.getParameterValues(fieldName);
        if (parameterValues != null) {
            for (String value : parameterValues) {
                if (value != null) {
                    values.add(value);
                }
            }
            if (!values.isEmpty()) {
                return values;
            }
        }

        for (Part part : request.getParts()) {
            if (!fieldName.equals(part.getName()) || part.getSubmittedFileName() != null) {
                continue;
            }

            try (InputStream inputStream = part.getInputStream();
                 Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
                scanner.useDelimiter("\\A");
                if (scanner.hasNext()) {
                    values.add(scanner.next());
                }
            }
        }

        return values;
    }
}
