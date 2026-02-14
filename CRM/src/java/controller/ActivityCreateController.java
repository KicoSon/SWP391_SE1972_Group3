package controller;

import dal.ActivityDAO;
import dal.CustomerDAO;
import dal.OpportunityDAO;
import dal.StaffDAO;
// import dal.UserDAO;        <-- Bạn cần tạo class này
// import dal.LeadDAO;        <-- Bạn cần tạo class này
// import dal.OpportunityDAO; <-- Bạn cần tạo class này
import model.activity.Activity;
import model.Customer;
import model.UserSession;
// import model.User;         <-- Model User/Staff
// import model.Lead;         <-- Model Lead
// import model.Opportunity;  <-- Model Opportunity

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ActivityCreateController", urlPatterns = {"/activities/create"})
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

        // 3. Lấy danh sách CUSTOMER (Logic phân quyền: Sale thấy khách mình, Sếp thấy hết)
        CustomerDAO customerDAO = new CustomerDAO();
        List<Customer> customerList;

        if (userSession.isSaleStaff() && !userSession.isAdmin()) {
            // Nếu là Sale -> Chỉ lấy khách hàng do mình phụ trách
            // Lưu ý: Đảm bảo class UserSession có hàm getStaff() trả về thông tin nhân viên
            int currentStaffId = userSession.getStaff().getId();
            customerList = customerDAO.getCustomersByOwnerId(currentStaffId);
        } else {
            // Nếu là Admin, Manager, Marketing -> Lấy toàn bộ
            customerList = customerDAO.getAllActiveCustomers();
        }
        request.setAttribute("customerList", customerList);

        // 4. Lấy danh sách Related To (LEAD & OPPORTUNITY)
        // LeadDAO leadDAO = new LeadDAO();
        // request.setAttribute("leadList", leadDAO.getAllLeads());
        OpportunityDAO oppDAO = new OpportunityDAO();
        request.setAttribute("oppList", oppDAO.getAllOpportunities());

        StaffDAO staffDAO = new StaffDAO();
        request.setAttribute("staffList", staffDAO.getAllActiveStaff());
        // 5. Forward sang JSP
        request.getRequestDispatcher("/activities/activity-create.jsp").forward(request, response);
    }

    // --- PHẦN 2: DO_POST (Xử lý lưu dữ liệu từ Form) ---
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        try {
            // 1. Kiểm tra Session
            HttpSession session = request.getSession();
            UserSession userSession = (UserSession) session.getAttribute("userSession");

            if (userSession == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            // 2. Map dữ liệu cơ bản
            Activity act = new Activity();
            act.setTitle(request.getParameter("title"));
            act.setDescription(request.getParameter("description"));
            act.setType(request.getParameter("type"));     // Call, Email, Meeting...
            act.setStatus(request.getParameter("status")); // Planned, In Progress...

            // Priority: Kiểm tra null, mặc định Medium
            String priority = request.getParameter("priority");
            act.setPriority((priority != null && !priority.isEmpty()) ? priority : "Medium");

            // Người tạo (Created_by) lấy từ Session Staff ID
            act.setCreatedBy(userSession.getStaff().getId());

            // 3. Xử lý Thời gian (Date + Time -> Timestamp)
            String dateStr = request.getParameter("date"); // yyyy-MM-dd
            String timeStr = request.getParameter("time"); // HH:mm

            if (dateStr != null && !dateStr.isEmpty() && timeStr != null && !timeStr.isEmpty()) {
                // Thêm giây :00 để đúng format Timestamp
                String dateTimeStr = dateStr + " " + timeStr + ":00";
                act.setDueDate(Timestamp.valueOf(dateTimeStr));
            } else {
                act.setDueDate(null);
            }

            act.setReminderAt(null); // Xử lý Reminder sau nếu cần

            // 4. Xử lý "Related To" (Cơ hội hoặc Lead)
            String relatedTo = request.getParameter("related_to"); // VD: "opp-1" hoặc "lead-5"

            if (relatedTo != null && !relatedTo.isEmpty()) {
                String[] parts = relatedTo.split("-");
                if (parts.length == 2) {
                    try {
                        if ("opp".equals(parts[0])) {
                            act.setOpportunityId(Integer.parseInt(parts[1]));
                        } else if ("lead".equals(parts[0])) {
                            // Lead ID trong DB là BIGINT -> Parse Long
                            act.setLeadId(Long.parseLong(parts[1]));
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error parsing Related ID: " + relatedTo);
                    }
                }
            }

            // 5. Xử lý Customer (Assigned To)
            String customerVal = request.getParameter("customer");
            if (customerVal != null && !customerVal.isEmpty()) {
                try {
                    act.setCustomerId(Integer.parseInt(customerVal));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Customer ID: " + customerVal);
                }
            }

            // 6. Xử lý Participants (Owner + Others)
            List<Integer> participantIds = new ArrayList<>();

            // A. OWNER (Quan trọng: Luôn add vào đầu danh sách)
            String ownerIdRaw = request.getParameter("owner");
            if (ownerIdRaw != null && !ownerIdRaw.isEmpty()) {
                try {
                    participantIds.add(Integer.parseInt(ownerIdRaw));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Owner ID");
                }
            }

            // B. Các Participants khác (Từ input hidden của JS)
            String otherParticipants = request.getParameter("participantIds"); // VD: "2,5,9"
            if (otherParticipants != null && !otherParticipants.isEmpty()) {
                String[] pIds = otherParticipants.split(",");
                for (String pId : pIds) {
                    try {
                        int id = Integer.parseInt(pId.trim());
                        if (!participantIds.contains(id)) { // Tránh add trùng Owner
                            participantIds.add(id);
                        }
                    } catch (NumberFormatException e) {
                        continue;
                    }
                }
            }

            // 7. Gọi DAO thực thi
            ActivityDAO dao = new ActivityDAO();
            boolean isSuccess = dao.insertActivity(act, participantIds);

            // 8. Điều hướng
            if (isSuccess) {
                // Thành công -> Về Dashboard kèm thông báo
                response.sendRedirect(request.getContextPath() + "/sale/dashboard?msg=success");
            } else {
                // Thất bại -> Forward lại trang create kèm thông báo lỗi
                request.setAttribute("error", "Lỗi: Không thể lưu vào Database. Vui lòng thử lại.");
                // Cần load lại các list dữ liệu cho dropdown trước khi forward
                doGet(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            request.getRequestDispatcher("/activities/activity-create.jsp").forward(request, response);
        }
    }
}
