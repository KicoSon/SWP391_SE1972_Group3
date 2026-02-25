//package controller;
//
//import dal.ActivityDAO;
//import model.activity.Activity;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.util.List;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//@WebServlet(name = "ActivityExportController", urlPatterns = {"/activities/export"})
//public class ActivityExportController extends HttpServlet {
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        
//        // 1. Lấy dữ liệu từ Database (Ví dụ lấy tất cả công việc)
//        ActivityDAO dao = new ActivityDAO();
//        // Giả sử bạn có hàm getAllActivities(), hoặc lấy theo User đang đăng nhập
////        List<Activity> list = dao.getAllActivities(); 
//
//        // 2. Tạo một file Excel mới (Chuẩn .xlsx)
//        try (Workbook workbook = new XSSFWorkbook()) {
//            Sheet sheet = workbook.createSheet("Danh_Sach_Cong_Viec");
//
//            // --- TẠO DÒNG TIÊU ĐỀ (HEADER) ---
//            Row headerRow = sheet.createRow(0);
//            String[] columns = {"ID", "Tiêu đề", "Loại", "Mức độ ưu tiên", "Trạng thái", "Ngày tạo"};
//            
//            // Trang trí Header cho đẹp (In đậm)
//            CellStyle headerStyle = workbook.createCellStyle();
//            Font headerFont = workbook.createFont();
//            headerFont.setBold(true);
//            headerStyle.setFont(headerFont);
//
//            for (int i = 0; i < columns.length; i++) {
//                Cell cell = headerRow.createCell(i);
//                cell.setCellValue(columns[i]);
//                cell.setCellStyle(headerStyle);
//            }
//
//            // --- TẠO CÁC DÒNG DỮ LIỆU ---
//            int rowNum = 1;
//            for (Activity act : list) {
//                Row row = sheet.createRow(rowNum++);
//                row.createCell(0).setCellValue(act.getId());
//                row.createCell(1).setCellValue(act.getTitle() != null ? act.getTitle() : "");
//                row.createCell(2).setCellValue(act.getType() != null ? act.getType() : "");
//                row.createCell(3).setCellValue(act.getPriority() != null ? act.getPriority() : "");
//                row.createCell(4).setCellValue(act.getStatus() != null ? act.getStatus() : "");
//                row.createCell(5).setCellValue(act.getCreatedAt() != null ? act.getCreatedAt().toString() : "");
//            }
//
//            // Căn chỉnh độ rộng cột tự động
//            for (int i = 0; i < columns.length; i++) {
//                sheet.autoSizeColumn(i);
//            }
//
//            // 3. CẤU HÌNH TRÌNH DUYỆT ĐỂ TẢI FILE VỀ
//            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//            // Đặt tên file khi tải về
//            response.setHeader("Content-Disposition", "attachment; filename=DanhSachCongViec.xlsx");
//
//            // 4. Bơm dữ liệu file Excel ra output stream
//            OutputStream outStream = response.getOutputStream();
//            workbook.write(outStream);
//            outStream.flush();
//        } catch (Exception e) {
//            e.printStackTrace();
//            response.getWriter().print("Lỗi khi xuất file Excel!");
//        }
//    }
//}