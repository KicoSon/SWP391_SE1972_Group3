package controller;

import dal.ActivityDAO;
import model.activity.Activity;
import model.UserSession;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@WebServlet(name = "ExportActivityExcelController", urlPatterns = {"/sale/export-activities"})
public class ExportActivityExcelController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Kiểm tra đăng nhập (Tái sử dụng logic của Dashboard)
        HttpSession session = request.getSession();
        UserSession userSession = (UserSession) session.getAttribute("userSession");

        if (userSession == null || userSession.getStaff() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }


        Integer filterUserId = null;

        if (userSession.isSaleStaff() && !userSession.isAdmin()) {
            filterUserId = userSession.getStaff().getId();
        }

        // 3. Lấy dữ liệu từ DB
        ActivityDAO dao = new ActivityDAO();
        List<Activity> activityList = dao.getActivitiesForDashboard(filterUserId);

        // 4. Cấu hình HTTP Response để trình duyệt tải file Excel về
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"activity_list.xlsx\"");

        // 5. Tạo file Excel bằng Apache POI
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Activities");

            // Tạo Header Row
            String[] headers = {"ID", "Title", "Type", "Status", "Priority", "Due Date", "Customer/Lead Name", "Creator", "Assignee"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Đổ dữ liệu vào các Row tiếp theo
            int rowNum = 1;
            for (Activity act : activityList) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(act.getId());
                row.createCell(1).setCellValue(act.getTitle() != null ? act.getTitle() : "");
                row.createCell(2).setCellValue(act.getType() != null ? act.getType() : "");
                row.createCell(3).setCellValue(act.getStatus() != null ? act.getStatus() : "");
                row.createCell(4).setCellValue(act.getPriority() != null ? act.getPriority() : "");
                
                // Xử lý Ngày tháng (DueDate có thể null)
                String dueDateStr = (act.getDueDate() != null) ? act.getDueDate().toString() : "";
                row.createCell(5).setCellValue(dueDateStr);

                // Gộp hiển thị Customer hoặc Lead (dựa vào code DAO của bạn)
                String contactName = "";
                if (act.getCustomerName() != null) {
                    contactName = act.getCustomerName() + " (Customer)";
                } else if (act.getLeadName() != null) {
                    contactName = act.getLeadName() + " (Lead)";
                }
                row.createCell(6).setCellValue(contactName);

                row.createCell(7).setCellValue(act.getCreatorName() != null ? act.getCreatorName() : "");
                row.createCell(8).setCellValue(act.getAssigneeName() != null ? act.getAssigneeName() : "");
            }

            // Auto-size các cột cho đẹp
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 6. Ghi Workbook vào Response Output Stream
            workbook.write(response.getOutputStream());

        } catch (Exception e) {
            e.printStackTrace();
            response.reset(); // Reset response nếu có lỗi
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi khi xuất file Excel");
        }
    }
}