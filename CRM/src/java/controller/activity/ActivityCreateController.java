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

// 1. THÊM MULTIPART ĐỂ NHẬN FILE TỪ FORM
@WebServlet(name = "ActivityCreateController", urlPatterns = {"/activities/create"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB đệm
        maxFileSize = 1024 * 1024 * 10, // Tối đa 10MB / 1 file
        maxRequestSize = 1024 * 1024 * 50 // Tối đa 50MB toàn form
)
public class ActivityCreateController extends HttpServlet {

    // --- PHẦN 1: DO_GET (Chuẩn bị dữ liệu để hiển thị Form) ---
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Kiểm tra đăng nhập
        HttpSession session = request.getSession();
        UserSession userSession = (UserSession) session.getAttribute("userSession");

        if (userSession == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 2. Lấy danh sách CUSTOMER (Logic phân quyền)
        CustomerDAO customerDAO = new CustomerDAO();
        List<Customer> customerList;

        if (userSession.isSaleStaff() && !userSession.isAdmin()) {
            if (userSession.getStaff() != null) {
                int currentStaffId = userSession.getStaff().getId();
                customerList = customerDAO.getCustomersByOwnerId(currentStaffId);
            } else {
                customerList = customerDAO.getAllActiveCustomers();
            }
        } else {
            customerList = customerDAO.getAllActiveCustomers();
        }
        request.setAttribute("customerList", customerList);

        // 3. Lấy danh sách Related To (LEAD & OPPORTUNITY)
        LeadDAO leadDAO = new LeadDAO();
        List<Lead> leadList;

        if (userSession.isSaleStaff() && !userSession.isAdmin()) {
            long currentStaffId = (long) userSession.getStaffInfo().getId();
            leadList = leadDAO.getLeadsBySaleId(currentStaffId);
        } else {
            leadList = leadDAO.getLeadsBySaleId(null);
        }
        request.setAttribute("leads", leadList);

        OpportunityDAO oppDAO = new OpportunityDAO();
//        request.setAttribute("oppList", oppDAO.getAllOpportunities());

        StaffDAO staffDAO = new StaffDAO();
        request.setAttribute("staffList", staffDAO.getAllActiveStaff());

        // 4. Forward sang JSP
        request.getRequestDispatcher("/activities/activity-create.jsp").forward(request, response);
    }

    // --- PHẦN 2: DO_POST (Xử lý lưu dữ liệu từ Form) ---
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

            // 0. Kiểm tra xem có ID (edit mode) không
            String idParam = request.getParameter("id");
            int activityId = -1;
            boolean isEditMode = false;
            
            if (idParam != null && !idParam.isEmpty()) {
                try {
                    activityId = Integer.parseInt(idParam);
                    isEditMode = true;
                } catch (NumberFormatException e) {
                    // Nếu ID không hợp lệ, coi là tạo mới
                }
            }

            // 1. Map dữ liệu Text cơ bản
            Activity act = new Activity();
            
            if (isEditMode) {
                act.setId(activityId);
            }
            
            act.setTitle(request.getParameter("title"));
            act.setDescription(request.getParameter("description"));
            act.setType(request.getParameter("type"));
            act.setStatus(request.getParameter("status"));

            String priority = request.getParameter("priority");
            act.setPriority((priority != null && !priority.isEmpty()) ? priority : "Medium");

            if (userSession.getStaff() == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            act.setCreatedBy(userSession.getStaff().getId());

            // 2. Xử lý Thời gian
            String dateStr = request.getParameter("date");
            String timeStr = request.getParameter("time");

            if (dateStr != null && !dateStr.isEmpty() && timeStr != null && !timeStr.isEmpty()) {
                String dateTimeStr = dateStr + " " + timeStr + ":00";
                act.setDueDate(Timestamp.valueOf(dateTimeStr));
            } else {
                act.setDueDate(null);
            }
            act.setReminderAt(null);

            // 3. Xử lý Related To
            String relatedTo = request.getParameter("related_to");
            if (relatedTo != null && !relatedTo.isEmpty()) {
                String[] parts = relatedTo.split("-");
                if (parts.length == 2) {
                    try {
                        if ("opp".equals(parts[0])) {
                            act.setOpportunityId(Integer.parseInt(parts[1]));
                        } else if ("lead".equals(parts[0])) {
                            act.setLeadId(Long.parseLong(parts[1]));
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error parsing Related ID: " + relatedTo);
                    }
                }
            }

            // 4. Xử lý Customer
            String customerVal = request.getParameter("customer");
            if (customerVal != null && !customerVal.isEmpty()) {
                try {
                    act.setCustomerId(Integer.parseInt(customerVal));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Customer ID: " + customerVal);
                }
            }

            // 5. Xử lý Participants
            List<Integer> participantIds = new ArrayList<>();
            String ownerIdRaw = request.getParameter("owner");
            if (ownerIdRaw != null && !ownerIdRaw.isEmpty()) {
                try {
                    participantIds.add(Integer.parseInt(ownerIdRaw));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Owner ID");
                }
            }

            String otherParticipants = request.getParameter("participantIds");
            if (otherParticipants != null && !otherParticipants.isEmpty()) {
                String[] pIds = otherParticipants.split(",");
                for (String pId : pIds) {
                    try {
                        int id = Integer.parseInt(pId.trim());
                        if (!participantIds.contains(id)) {
                            participantIds.add(id);
                        }
                    } catch (NumberFormatException e) {
                        continue;
                    }
                }
            }

            ActivityDAO dao = new ActivityDAO();
            
            // === EDIT MODE: CẬP NHẬT ===
            if (isEditMode) {
                boolean updateSuccess = dao.updateActivity(act);
                
                if (updateSuccess) {
                    // Xử lý upload file mới nếu có
                    String uploadPath = getServletContext().getInitParameter("uploadDirectory");
                    if (uploadPath != null && !uploadPath.isEmpty()) {
                        File uploadDir = new File(uploadPath);
                        if (!uploadDir.exists()) {
                            uploadDir.mkdirs();
                        }

                        ActivityDAO attachmentDao = new ActivityDAO();

                        for (Part part : request.getParts()) {
                            if ("attachments".equals(part.getName()) && part.getSize() > 0) {
                                String fileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
                                String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
                                String savePath = uploadPath + File.separator + uniqueFileName;

                                part.write(savePath);

                                String dbPath = "uploads/" + uniqueFileName;
                                attachmentDao.insertAttachment(activityId, fileName, dbPath);
                            }
                        }
                    }
                    
                    // Redirect về detail page
                    response.sendRedirect(request.getContextPath() + "/activities/detail?id=" + activityId + "&msg=updated");
                } else {
                    request.setAttribute("error", "Lỗi: Không thể cập nhật hoạt động. Vui lòng thử lại.");
                    doGet(request, response);
                }
            }
            // === CREATE MODE: TẠO MỚI ===
            else {
                int newActivityId = dao.insertActivity(act, participantIds);

                if (newActivityId > 0) {
                    // Lưu file vào D:/uploads thay vì build folder
                    String uploadPath = getServletContext().getInitParameter("uploadDirectory");
                    if (uploadPath != null && !uploadPath.isEmpty()) {
                        File uploadDir = new File(uploadPath);
                        if (!uploadDir.exists()) {
                            uploadDir.mkdirs();
                        }

                        ActivityDAO attachmentDao = new ActivityDAO();

                        for (Part part : request.getParts()) {
                            if ("attachments".equals(part.getName()) && part.getSize() > 0) {
                                String fileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
                                String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
                                String savePath = uploadPath + File.separator + uniqueFileName;

                                part.write(savePath);

                                String dbPath = "uploads/" + uniqueFileName;
                                attachmentDao.insertAttachment(newActivityId, fileName, dbPath);
                            }
                        }
                    }

                    response.sendRedirect(request.getContextPath() + "/sale/dashboard?msg=success");
                } else {
                    request.setAttribute("error", "Lỗi: Không thể lưu vào Database. Vui lòng thử lại.");
                    doGet(request, response);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            doGet(request, response);
        }
    }
}
