package controller;

import dal.ActivityDAO;
import model.activity.Activity;
import model.UserSession; // Giả sử bạn có class này lưu session

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

// Annotation này bắt buộc để nhận file upload
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // 2MB
    maxFileSize = 1024 * 1024 * 10,      // 10MB
    maxRequestSize = 1024 * 1024 * 50    // 50MB
)
@WebServlet(name = "ActivityCreateController", urlPatterns = {"/create-activity"})
public class ActivityCreateController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Chuyển hướng đến trang JSP để hiển thị form
        request.getRequestDispatcher("activityCreate.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        try {
            // 1. Kiểm tra Session (Người đang đăng nhập)
            HttpSession session = request.getSession();
            UserSession currentUser = (UserSession) session.getAttribute("user");
            
            if (currentUser == null) {
                response.sendRedirect("login"); // Nếu chưa login thì đá về login
                return;
            }

            // 2. Khởi tạo đối tượng Activity
            Activity act = new Activity();
            act.setTitle(request.getParameter("title"));
            act.setType(request.getParameter("type"));
            act.setDescription(request.getParameter("description"));
            act.setPriority(request.getParameter("priority"));
            act.setStatus(request.getParameter("status"));
            act.setCreatedBy(currentUser.getId()); // Người tạo là người đang login

            // 3. Xử lý Ngày + Giờ (Date + Time) -> Timestamp
            String dateStr = request.getParameter("date"); // yyyy-MM-dd
            String timeStr = request.getParameter("time"); // HH:mm
            if (dateStr != null && timeStr != null) {
                String dateTimeStr = dateStr + " " + timeStr + ":00"; // yyyy-MM-dd HH:mm:00
                Timestamp dueDate = Timestamp.valueOf(dateTimeStr);
                act.setDueDate(dueDate);
            }

            // 4. Xử lý logic "Related To" (Cơ hội / Lead)
            // Value từ form có dạng: "opp-1" hoặc "lead-5"
            String relatedTo = request.getParameter("related_to");
            if (relatedTo != null && !relatedTo.isEmpty()) {
                String[] parts = relatedTo.split("-");
                if (parts.length == 2) {
                    int relatedId = Integer.parseInt(parts[1]);
                    if (parts[0].equals("opp")) {
                        act.setOpportunityId(relatedId);
                    } else if (parts[0].equals("lead")) {
                        act.setLeadId((long) relatedId);
                    }
                }
            }
            
            // 5. Xử lý Customer (Assigned To)
            // Giả sử value từ form là ID int (ví dụ: "10"), nếu là string "customer-a" thì phải parse
            String customerVal = request.getParameter("customer");
            if (customerVal != null && customerVal.matches("\\d+")) { // Kiểm tra nếu là số
                act.setCustomerId(Integer.parseInt(customerVal));
            }

            // 6. Xử lý Người tham gia (Participants)
            List<Integer> participantIds = new ArrayList<>();
            
            // a. Thêm Owner (Người chủ trì được chọn trong dropdown)
            String ownerIdRaw = request.getParameter("owner");
            if (ownerIdRaw != null && !ownerIdRaw.isEmpty()) {
                // Giả sử Owner luôn đứng đầu list để DAO set role='Owner'
                try {
                    // Nếu value là username "viehai", bạn cần hàm đổi username -> id
                    // Ở đây tôi giả định form gửi về ID (int) cho đơn giản
                    participantIds.add(Integer.parseInt(ownerIdRaw)); 
                } catch (NumberFormatException e) {
                    System.out.println("Lỗi parse owner ID: " + ownerIdRaw);
                }
            }

            // b. Thêm các Participants khác (Từ thẻ input hidden mà JS đã xử lý)
            String otherParticipants = request.getParameter("participantIds"); // Chuỗi dạng "1,5,9"
            if (otherParticipants != null && !otherParticipants.isEmpty()) {
                String[] pIds = otherParticipants.split(",");
                for (String pId : pIds) {
                    try {
                        int id = Integer.parseInt(pId.trim());
                        // Tránh duplicate Owner
                        if (!participantIds.contains(id)) {
                            participantIds.add(id);
                        }
                    } catch (NumberFormatException e) {
                        continue;
                    }
                }
            }

            // 7. Xử lý Reminder (Nếu có)
            String enableReminder = request.getParameter("reminder_type"); // Lấy logic reminder
            // Logic xử lý reminder có thể phức tạp, tạm thời bỏ qua hoặc set null nếu chưa cần gấp

            // 8. Gọi DAO để lưu (Sử dụng hàm Transaction đã viết trước đó)
            ActivityDAO dao = new ActivityDAO();
            boolean isSuccess = dao.insertActivity(act, participantIds);
            
            // 9. Xử lý File đính kèm (Nếu insert Activity thành công)
            if (isSuccess) {
                // Đoạn này lấy ID của activity vừa tạo (bạn cần sửa hàm insertActivity trả về ID thay vì boolean nếu muốn lưu file ngay)
                // Hoặc lưu file vào folder tạm.
                
                // Collection<Part> parts = request.getParts();
                // Logic upload file ở đây...
                
                // Redirect về trang danh sách
                response.sendRedirect("activity-list?msg=success");
            } else {
                request.setAttribute("error", "Create failed!");
                request.getRequestDispatcher("activityCreate.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "System error: " + e.getMessage());
            request.getRequestDispatcher("activityCreate.jsp").forward(request, response);
        }
    }
}