package controller.activity;

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

        ActivityDAO dao = new ActivityDAO();
        List<Activity> activityList = dao.getActivitiesForDashboard(filterUserId);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"activity_list.xlsx\"");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Activities");

            String[] headers = {"ID", "Title", "Type", "Status", "Priority", "Due Date", "Customer/Lead Name", "Creator", "Assignee"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (Activity act : activityList) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(act.getId());
                row.createCell(1).setCellValue(act.getTitle() != null ? act.getTitle() : "");
                row.createCell(2).setCellValue(act.getType() != null ? act.getType() : "");
                row.createCell(3).setCellValue(act.getStatus() != null ? act.getStatus() : "");
                row.createCell(4).setCellValue(act.getPriority() != null ? act.getPriority() : "");
                
                String dueDateStr = (act.getDueDate() != null) ? act.getDueDate().toString() : "";
                row.createCell(5).setCellValue(dueDateStr);

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

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());

        } catch (Exception e) {
            e.printStackTrace();
            response.reset();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi khi xuất file Excel");
        }
    }
}