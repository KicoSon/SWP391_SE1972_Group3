<<<<<<< HEAD
//package controller;
//
//import dal.ActivityDAO;
//import model.activity.Activity;
//import model.UserSession; // Giả sử bạn có class này lưu session
//
//import java.io.IOException;
//import java.sql.Timestamp;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.List;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.MultipartConfig;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import jakarta.servlet.http.Part;
//
//// Annotation này bắt buộc để nhận file upload
//@MultipartConfig(
//    fileSizeThreshold = 1024 * 1024 * 2, // 2MB
//    maxFileSize = 1024 * 1024 * 10,      // 10MB
//    maxRequestSize = 1024 * 1024 * 50    // 50MB
//)
//@WebServlet(name = "ActivityCreateController", urlPatterns = {"/create-activity"})
//public class ActivityCreateController extends HttpServlet {
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        // Chuyển hướng đến trang JSP để hiển thị form
//        request.getRequestDispatcher("activityCreate.jsp").forward(request, response);
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        request.setCharacterEncoding("UTF-8");
//        
//        try {
//            // 1. Kiểm tra Session (Người đang đăng nhập)
//            HttpSession session = request.getSession();
//            UserSession currentUser = (UserSession) session.getAttribute("user");
//            
//            if (currentUser == null) {
//                response.sendRedirect("login"); // Nếu chưa login thì đá về login
//                return;
//            }
//
//            // 2. Khởi tạo đối tượng Activity
//            Activity act = new Activity();
//            act.setTitle(request.getParameter("title"));
//            act.setType(request.getParameter("type"));
//            act.setDescription(request.getParameter("description"));
//            act.setPriority(request.getParameter("priority"));
//            act.setStatus(request.getParameter("status"));
//            act.setCreatedBy(currentUser.getId()); // Người tạo là người đang login
//
//            // 3. Xử lý Ngày + Giờ (Date + Time) -> Timestamp
//            String dateStr = request.getParameter("date"); // yyyy-MM-dd
//            String timeStr = request.getParameter("time"); // HH:mm
//            if (dateStr != null && timeStr != null) {
//                String dateTimeStr = dateStr + " " + timeStr + ":00"; // yyyy-MM-dd HH:mm:00
//                Timestamp dueDate = Timestamp.valueOf(dateTimeStr);
//                act.setDueDate(dueDate);
//            }
//
//            // 4. Xử lý logic "Related To" (Cơ hội / Lead)
//            // Value từ form có dạng: "opp-1" hoặc "lead-5"
//            String relatedTo = request.getParameter("related_to");
//            if (relatedTo != null && !relatedTo.isEmpty()) {
//                String[] parts = relatedTo.split("-");
//                if (parts.length == 2) {
//                    int relatedId = Integer.parseInt(parts[1]);
//                    if (parts[0].equals("opp")) {
//                        act.setOpportunityId(relatedId);
//                    } else if (parts[0].equals("lead")) {
//                        act.setLeadId((long) relatedId);
//                    }
//                }
//            }
//            
//            // 5. Xử lý Customer (Assigned To)
//            // Giả sử value từ form là ID int (ví dụ: "10"), nếu là string "customer-a" thì phải parse
//            String customerVal = request.getParameter("customer");
//            if (customerVal != null && customerVal.matches("\\d+")) { // Kiểm tra nếu là số
//                act.setCustomerId(Integer.parseInt(customerVal));
//            }
//
//            // 6. Xử lý Người tham gia (Participants)
//            List<Integer> participantIds = new ArrayList<>();
//            
//            // a. Thêm Owner (Người chủ trì được chọn trong dropdown)
//            String ownerIdRaw = request.getParameter("owner");
//            if (ownerIdRaw != null && !ownerIdRaw.isEmpty()) {
//                // Giả sử Owner luôn đứng đầu list để DAO set role='Owner'
//                try {
//                    // Nếu value là username "viehai", bạn cần hàm đổi username -> id
//                    // Ở đây tôi giả định form gửi về ID (int) cho đơn giản
//                    participantIds.add(Integer.parseInt(ownerIdRaw)); 
//                } catch (NumberFormatException e) {
//                    System.out.println("Lỗi parse owner ID: " + ownerIdRaw);
//                }
//            }
//
//            // b. Thêm các Participants khác (Từ thẻ input hidden mà JS đã xử lý)
//            String otherParticipants = request.getParameter("participantIds"); // Chuỗi dạng "1,5,9"
//            if (otherParticipants != null && !otherParticipants.isEmpty()) {
//                String[] pIds = otherParticipants.split(",");
//                for (String pId : pIds) {
//                    try {
//                        int id = Integer.parseInt(pId.trim());
//                        // Tránh duplicate Owner
//                        if (!participantIds.contains(id)) {
//                            participantIds.add(id);
//                        }
//                    } catch (NumberFormatException e) {
//                        continue;
//                    }
//                }
//            }
//
//            // 7. Xử lý Reminder (Nếu có)
//            String enableReminder = request.getParameter("reminder_type"); // Lấy logic reminder
//            // Logic xử lý reminder có thể phức tạp, tạm thời bỏ qua hoặc set null nếu chưa cần gấp
//
//            // 8. Gọi DAO để lưu (Sử dụng hàm Transaction đã viết trước đó)
//            ActivityDAO dao = new ActivityDAO();
//            boolean isSuccess = dao.insertActivity(act, participantIds);
//            
//            // 9. Xử lý File đính kèm (Nếu insert Activity thành công)
//            if (isSuccess) {
//                // Đoạn này lấy ID của activity vừa tạo (bạn cần sửa hàm insertActivity trả về ID thay vì boolean nếu muốn lưu file ngay)
//                // Hoặc lưu file vào folder tạm.
//                
//                // Collection<Part> parts = request.getParts();
//                // Logic upload file ở đây...
//                
//                // Redirect về trang danh sách
//                response.sendRedirect("activity-list?msg=success");
//            } else {
//                request.setAttribute("error", "Create failed!");
//                request.getRequestDispatcher("activityCreate.jsp").forward(request, response);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            request.setAttribute("error", "System error: " + e.getMessage());
//            request.getRequestDispatcher("activityCreate.jsp").forward(request, response);
//        }
//    }
//}
=======
package controller;

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
        request.setAttribute("oppList", oppDAO.getAllOpportunities());

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
>>>>>>> develop
