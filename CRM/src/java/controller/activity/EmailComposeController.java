package controller.activity;

import dal.ActivityDAO;
import dal.CustomerDAO;
import dal.LeadDAO;
import dal.EmailDAO;
import model.Customer;
import model.Lead;
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
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 50
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

        if (userSession.isMarketingStaff()) {
            response.sendRedirect(request.getContextPath() + "/sale/dashboard?error=nopermission");
            return;
        }

        // Nếu mở từ activity, sẽ khóa người nhận theo customer/lead của activity đó.
        String activityIdParam = request.getParameter("activityId");

        if (activityIdParam != null && !activityIdParam.trim().isEmpty()) {
            int activityId = Integer.parseInt(activityIdParam);
            ActivityDAO actDAO = new ActivityDAO();
            model.activity.Activity act = actDAO.getActivityById(activityId);

            if (act != null) {
                if (act.getCustomerId() != null && act.getCustomerId() > 0) {
                    CustomerDAO custDAO = new CustomerDAO();
                    model.Customer fixedCustomer = custDAO.getCustomerById(act.getCustomerId());
                    request.setAttribute("fixedCustomer", fixedCustomer);
                    request.setAttribute("sourceActivityId", activityId);
                } else if (act.getLeadId() != null && act.getLeadId() > 0) {
                    LeadDAO lDAO = new LeadDAO();
                    model.Lead fixedLead = lDAO.getLeadById(act.getLeadId());
                    request.setAttribute("fixedLead", fixedLead);
                    request.setAttribute("sourceActivityId", activityId);
                }
            }
        }

        CustomerDAO customerDAO = new CustomerDAO();
        List<Customer> customers;
        LeadDAO leadDAO = new LeadDAO();
        List<Lead> leads;

        if (userSession.isSaleStaff() && !userSession.isAdmin()) {
            int currentStaffId = userSession.getStaff().getId();
            customers = customerDAO.getCustomersByOwnerId(currentStaffId);
            leads = leadDAO.getLeadsBySaleId((long) currentStaffId);
        } else {
            customers = customerDAO.getAllActiveCustomers();
            leads = leadDAO.getLeadsBySaleId(null);
        }

        request.setAttribute("customers", customers);
        request.setAttribute("leads", leads);

        request.getRequestDispatcher("/emails/email-compose.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        try {
            // recipientId có format customer_<id> hoặc lead_<id>.
            String recipientRaw = request.getParameter("recipientId");
            int customerId = 0;
            Long leadId = null;
            String receiverEmail = "";
            
            if (recipientRaw != null && !recipientRaw.isEmpty()) {
                String[] parts = recipientRaw.split("_");
                if (parts.length == 2) {
                    if ("customer".equals(parts[0])) {
                        customerId = Integer.parseInt(parts[1]);
                        CustomerDAO customerDAO = new CustomerDAO();
                        Customer receiver = customerDAO.getCustomerById(customerId);
                        if (receiver != null) receiverEmail = receiver.getEmail();
                    } else if ("lead".equals(parts[0])) {
                        leadId = Long.parseLong(parts[1]);
                        LeadDAO leadDAO = new LeadDAO();
                        Lead receiver = leadDAO.getLeadById(leadId);
                        if (receiver != null) receiverEmail = receiver.getEmail();
                        customerId = 0;
                    }
                }
            }

            // Có activityId => gửi mail để hoàn tất một activity có sẵn.
            String activityIdRaw = request.getParameter("activityId");
            String subject = request.getParameter("subject");
            String content = request.getParameter("content");

            // Thu thập file đính kèm để stream qua EmailService.
            List<Part> fileParts = new ArrayList<>();
            if (request.getParts() != null) {
                for (Part part : request.getParts()) {
                    if ("attachments".equals(part.getName()) && part.getSize() > 0 && part.getSubmittedFileName() != null) {
                        fileParts.add(part);
                    }
                }
            }

            HttpSession session = request.getSession();
            UserSession userSession = (UserSession) session.getAttribute("userSession");
            int fromUserId = userSession.getStaff().getId();

            // Gọi SMTP thật qua Jakarta Mail.
            boolean sendSuccess = EmailService.sendEmail(receiverEmail, subject, content, fileParts);

            EmailDAO emailDAO = new EmailDAO();
            // Dù thành công hay thất bại đều log vào bảng emails.
            emailDAO.insertEmailLog(fromUserId, customerId, receiverEmail, subject, content, sendSuccess ? "Sent" : "Failed");

            if (sendSuccess) {
                ActivityDAO activityDAO = new ActivityDAO();

                if (activityIdRaw != null && !activityIdRaw.trim().isEmpty()) {
                    // Nhánh 1: cập nhật activity hiện hữu thành Completed.
                    int actId = Integer.parseInt(activityIdRaw);
                    activityDAO.updateActivityStatus(actId, "Completed", "Đã gửi email: " + subject);
                } else {
                    // Nhánh 2: compose tự do thì tạo activity Email mới để lưu vết hành vi gửi.
                    model.activity.Activity newAct = new model.activity.Activity();
                    newAct.setTitle("Gửi Email: " + subject);
                    newAct.setType("Email");
                    newAct.setDescription("Nội dung: " + content);
                    if (customerId > 0) newAct.setCustomerId(customerId);
                    if (leadId != null) newAct.setLeadId(leadId);
                    newAct.setCreatedBy(fromUserId);
                    newAct.setStatus("Completed");
                    newAct.setPriority("Medium");

                    java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
                    newAct.setDueDate(now);
                    newAct.setReminderAt(now);

                    List<Integer> participants = new ArrayList<>();
                    participants.add(fromUserId);

                    activityDAO.insertActivity(newAct, participants);
                }

                response.sendRedirect(request.getContextPath() + "/sale/dashboard?msg=emailsent");
            } else {
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
