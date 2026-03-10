package controller.activity;

import dal.ActivityDAO; // Import thêm DAO để xử lý trạng thái Activity
import dal.CustomerDAO;
import dal.EmailDAO;
import model.Customer;
import model.UserSession;
import util.EmailService;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Part;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

@WebServlet(name = "EmailComposeController", urlPatterns = {"/emails/compose"})
@MultipartConfig( // BẮT BUỘC PHẢI CÓ
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50 // 50MB
)
public class EmailComposeController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserSession userSession = (UserSession) session.getAttribute("userSession");

        if (userSession == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Marketing bị chặn hoàn toàn - không được vào trang soạn mail
        if (userSession.isMarketingStaff()) {
            response.sendRedirect(request.getContextPath() + "/sale/dashboard?error=nopermission");
            return;
        }

        String activityIdParam = request.getParameter("activityId");

        // Nếu đi từ Activity (có ID hoạt động)
        if (activityIdParam != null && !activityIdParam.trim().isEmpty()) {
            int activityId = Integer.parseInt(activityIdParam);
            ActivityDAO actDAO = new ActivityDAO();
            model.activity.Activity act = actDAO.getActivityById(activityId);

            if (act != null && act.getCustomerId() != null) {
                CustomerDAO custDAO = new CustomerDAO();
                model.Customer fixedCustomer = custDAO.getCustomerById(act.getCustomerId());
                // Gửi đối tượng khách hàng cố định này sang JSP
                request.setAttribute("fixedCustomer", fixedCustomer);
                request.setAttribute("sourceActivityId", activityId);
            }
        }

        CustomerDAO customerDAO = new CustomerDAO();
        List<Customer> customers;

        // Kiểm tra quyền: Nếu là Sale và KHÔNG PHẢI Admin -> Chỉ lấy khách của mình
        if (userSession.isSaleStaff() && !userSession.isAdmin()) {
            // Lấy ID của nhân viên đang đăng nhập
            int currentStaffId = userSession.getStaff().getId();

            // Gọi hàm lấy khách theo Owner ID (Hàm này bạn đã có trong CustomerDAO)
            customers = customerDAO.getCustomersByOwnerId(currentStaffId);
        } else {
            // Nếu là Admin hoặc Manager -> Lấy tất cả để họ hỗ trợ bất kỳ ai
            customers = customerDAO.getAllActiveCustomers();
        }

        request.setAttribute("customers", customers);

        request.getRequestDispatcher("/emails/email-compose.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        try {
            // 1. Lấy thông tin cơ bản từ Form
            String customerIdRaw = request.getParameter("customerId");
            int customerId = (customerIdRaw != null && !customerIdRaw.isEmpty()) ? Integer.parseInt(customerIdRaw) : 0;

            String activityIdRaw = request.getParameter("activityId");
            String subject = request.getParameter("subject");
            String content = request.getParameter("content");

            // 2. LẤY FILE TỪ FORM (Chỉ để gửi mail, không lưu)
            List<Part> fileParts = new ArrayList<>();
            if (request.getParts() != null) {
                for (Part part : request.getParts()) {
                    // Lọc lấy các file đính kèm có dung lượng > 0
                    if ("attachments".equals(part.getName()) && part.getSize() > 0 && part.getSubmittedFileName() != null) {
                        fileParts.add(part);
                    }
                }
            }

            // Lấy thông tin người nhận & người gửi
            CustomerDAO customerDAO = new CustomerDAO();
            Customer receiver = customerDAO.getCustomerById(customerId);

            HttpSession session = request.getSession();
            UserSession userSession = (UserSession) session.getAttribute("userSession");
            int fromUserId = userSession.getStaff().getId();

            // 3. GỬI MAIL (File sẽ được stream trực tiếp lên Gmail server)
            boolean sendSuccess = EmailService.sendEmail(receiver.getEmail(), subject, content, fileParts);

            // 4. LƯU LOG VÀO DATABASE
            // a. Lưu vào bảng lịch sử Email (emails table)
            EmailDAO emailDAO = new EmailDAO();
            emailDAO.insertEmailLog(fromUserId, customerId, receiver.getEmail(), subject, content, sendSuccess ? "Sent" : "Failed");

            if (sendSuccess) {
                ActivityDAO activityDAO = new ActivityDAO();

                // b. Cập nhật hoặc Tạo mới Activity (Để hiện lên Dashboard)
                if (activityIdRaw != null && !activityIdRaw.trim().isEmpty()) {
                    // TRƯỜNG HỢP 1: Gửi từ Activity có sẵn -> Update trạng thái thành Completed
                    int actId = Integer.parseInt(activityIdRaw);
                    activityDAO.updateActivityStatus(actId, "Completed", "Đã gửi email: " + subject);
                } else {
                    // TRƯỜNG HỢP 2: Soạn mail mới -> Tự tạo Activity mới ghi nhận việc này
                    model.activity.Activity newAct = new model.activity.Activity();
                    newAct.setTitle("Gửi Email: " + subject);
                    newAct.setType("Email");
                    newAct.setDescription("Nội dung: " + content);
                    newAct.setCustomerId(customerId);
                    newAct.setCreatedBy(fromUserId);
                    newAct.setStatus("Completed"); // Xong luôn
                    newAct.setPriority("Medium");

                    java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
                    newAct.setDueDate(now);
                    newAct.setReminderAt(now);

                    List<Integer> participants = new ArrayList<>();
                    participants.add(fromUserId);

                    activityDAO.insertActivity(newAct, participants);
                }

                // Xong việc -> Quay về Dashboard
                response.sendRedirect(request.getContextPath() + "/sale/dashboard?msg=emailsent");
            } else {
                // Gửi thất bại
                request.setAttribute("error", "Gửi mail thất bại. Vui lòng kiểm tra lại đường truyền.");
                doGet(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            doGet(request, response);
        }
    }
}
