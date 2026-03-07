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

@WebServlet(name = "EmailComposeController", urlPatterns = {"/emails/compose"})
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

        // Luôn load danh sách khách hàng để dự phòng cho trường hợp "Soạn mail mới" trực tiếp
        CustomerDAO customerDAO = new CustomerDAO();
        request.setAttribute("customers", customerDAO.getAllActiveCustomers());

        request.getRequestDispatcher("/emails/email-compose.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        int customerId = Integer.parseInt(request.getParameter("customerId"));
        String activityIdRaw = request.getParameter("activityId"); // Nhận lại ID từ form ẩn
        String subject = request.getParameter("subject");
        String content = request.getParameter("content");

        // 1. Lấy thông tin khách hàng để có Email nhận
        CustomerDAO customerDAO = new CustomerDAO();
        Customer receiver = customerDAO.getCustomerById(customerId);

        // 2. Lấy thông tin người gửi từ Session
        HttpSession session = request.getSession();
        UserSession userSession = (UserSession) session.getAttribute("userSession");
        int fromUserId = userSession.getStaff().getId();

        // 3. Tiến hành gửi mail thật
        boolean sendSuccess = EmailService.sendEmail(receiver.getEmail(), subject, content);

        // 4. Lưu vào lịch sử Email DB
        EmailDAO emailDAO = new EmailDAO();
        emailDAO.insertEmailLog(fromUserId, customerId, receiver.getEmail(), subject, content, sendSuccess ? "Sent" : "Failed");

        // --- PHẦN MỚI: Tự động hoàn thành Activity nếu gửi mail thành công ---
        if (sendSuccess && activityIdRaw != null && !activityIdRaw.isEmpty()) {
            int activityId = Integer.parseInt(activityIdRaw);
            ActivityDAO activityDAO = new ActivityDAO();
            // Cập nhật trạng thái activity từ Planned sang Completed
            activityDAO.updateActivityStatus(activityId, "Completed", "Hệ thống: Đã thực hiện gửi mail vào lúc " + new java.util.Date());
        }

        if (sendSuccess) {
            response.sendRedirect(request.getContextPath() + "/sale/dashboard?msg=emailsent");
        } else {
            request.setAttribute("error", "Gửi mail thất bại. Vui lòng kiểm tra lại cấu hình.");
            doGet(request, response);
        }
    }
}
