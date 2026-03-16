package controller.customerservice;

import dal.TicketFeedbackDAO;
import model.TicketFeedback;
import model.UserSession;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Xuất Excel từ bảng ticket_feedback — 2 sheets.
 * URL: /customerservice/exportfeedback
 */
@WebServlet("/customerservice/exportfeedback")
public class ExportFeedbackServlet extends HttpServlet {

    private TicketFeedbackDAO feedbackDAO;

    @Override
    public void init() {
        feedbackDAO = new TicketFeedbackDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userSession") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        UserSession user = (UserSession) session.getAttribute("userSession");
        if (!user.isStaff()) {
            resp.sendRedirect(req.getContextPath() + "/customerservice/ticketlist");
            return;
        }

        int ratingFilter = 0;
        String ratingParam = req.getParameter("rating");
        if (ratingParam != null && !ratingParam.isEmpty()) {
            try {
                ratingFilter = Integer.parseInt(ratingParam);
                if (ratingFilter < 1 || ratingFilter > 5) ratingFilter = 0;
            } catch (NumberFormatException e) { ratingFilter = 0; }
        }

        List<TicketFeedback> list  = feedbackDAO.getFeedbacksByRating(ratingFilter);
        Map<String, Object>  stats = feedbackDAO.getFeedbackStats();

        String timestamp   = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String ratingLabel = ratingFilter > 0 ? "_" + ratingFilter + "sao" : "";
        String fileName    = "BaoCaoFeedback" + ratingLabel + "_" + timestamp + ".xlsx";

        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");

        try (XSSFWorkbook wb = new XSSFWorkbook();
             OutputStream out = resp.getOutputStream()) {
            buildSheet1(wb, list, ratingFilter);
            buildSheet2(wb, stats);
            wb.write(out);
        }
    }

    // ── Sheet 1: Danh sách feedback (có cột Ticket) ───────────
    private void buildSheet1(Workbook wb, List<TicketFeedback> list, int ratingFilter) {
        Sheet sheet = wb.createSheet("Danh sách phản hồi");

        CellStyle title   = createTitleStyle(wb);
        CellStyle info    = createInfoStyle(wb);
        CellStyle header  = createHeaderStyle(wb);
        CellStyle data    = createDataStyle(wb);
        CellStyle star    = createStarStyle(wb);
        CellStyle center  = createCenterDataStyle(wb);

        // Tiêu đề
        Row r0 = sheet.createRow(0); r0.setHeightInPoints(28);
        Cell tc = r0.createCell(0);
        tc.setCellValue("BÁO CÁO PHẢN HỒI KHÁCH HÀNG THEO TICKET");
        tc.setCellStyle(title);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));

        // Info
        Row r1 = sheet.createRow(1);
        Cell ic = r1.createCell(0);
        String filterNote = ratingFilter > 0 ? "Lọc: " + ratingFilter + " sao  |  " : "Tất cả  |  ";
        ic.setCellValue(filterNote + "Ngày xuất: " +
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        ic.setCellStyle(info);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 8));

        // Header
        String[] headers = {"STT", "Ticket ID", "Tiêu đề Ticket", "Ưu tiên", "Khách hàng",
                            "Rating", "Sao", "Mức độ", "Nhận xét", "Ngày đánh giá"};
        Row hr = sheet.createRow(3); hr.setHeightInPoints(20);
        for (int i = 0; i < headers.length; i++) {
            Cell c = hr.createCell(i);
            c.setCellValue(headers[i]);
            c.setCellStyle(header);
        }

        // Column widths
        sheet.setColumnWidth(0, 8  * 256); // STT
        sheet.setColumnWidth(1, 12 * 256); // Ticket ID
        sheet.setColumnWidth(2, 32 * 256); // Tiêu đề
        sheet.setColumnWidth(3, 12 * 256); // Ưu tiên
        sheet.setColumnWidth(4, 26 * 256); // Khách hàng
        sheet.setColumnWidth(5, 10 * 256); // Rating
        sheet.setColumnWidth(6, 14 * 256); // Sao
        sheet.setColumnWidth(7, 20 * 256); // Mức độ
        sheet.setColumnWidth(8, 45 * 256); // Nhận xét
        sheet.setColumnWidth(9, 22 * 256); // Ngày

        // Data rows
        int rowNum = 4;
        for (int i = 0; i < list.size(); i++) {
            TicketFeedback fb = list.get(i);
            Row row = sheet.createRow(rowNum++); row.setHeightInPoints(22);
            createCell(row, 0, String.valueOf(i + 1),         center);
            createCell(row, 1, "#" + fb.getTicketId(),        center);
            createCell(row, 2, nvl(fb.getTicketTitle()),      data);
            createCell(row, 3, nvl(fb.getTicketPriority()),   center);
            createCell(row, 4, nvl(fb.getCustomerName()),     data);
            createCell(row, 5, fb.getRating() + " / 5",       center);
            createCell(row, 6, fb.getStarDisplay(),           star);
            createCell(row, 7, fb.getRatingLabel(),           data);
            createCell(row, 8, nvl(fb.getComments()),         data);
            createCell(row, 9, nvl(fb.getCreatedAt()),        data);
        }

        // Tổng
        Row total = sheet.createRow(rowNum + 1);
        Cell lbl = total.createCell(0); lbl.setCellValue("Tổng số:"); lbl.setCellStyle(header);
        Cell val = total.createCell(1); val.setCellValue(list.size()); val.setCellStyle(data);
    }

    // ── Sheet 2: Thống kê ─────────────────────────────────────
    private void buildSheet2(Workbook wb, Map<String, Object> stats) {
        Sheet sheet = wb.createSheet("Thống kê");

        CellStyle title  = createTitleStyle(wb);
        CellStyle header = createHeaderStyle(wb);
        CellStyle data   = createDataStyle(wb);
        CellStyle hi     = createHighlightStyle(wb);
        CellStyle center = createCenterDataStyle(wb);

        Row r0 = sheet.createRow(0); r0.setHeightInPoints(28);
        Cell tc = r0.createCell(0);
        tc.setCellValue("THỐNG KÊ PHẢN HỒI THEO TICKET");
        tc.setCellStyle(title);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        int rn = 2;
        Row r1 = sheet.createRow(rn++);
        createCell(r1, 0, "Tổng số phản hồi", header);
        createCell(r1, 1, String.valueOf(stats.getOrDefault("total", 0)), hi);

        Row r2 = sheet.createRow(rn++);
        createCell(r2, 0, "Rating trung bình", header);
        createCell(r2, 1, stats.getOrDefault("avgRating", 0.0) + " / 5.0", hi);

        rn++;
        Row dh = sheet.createRow(rn++);
        Cell dhc = dh.createCell(0);
        dhc.setCellValue("PHÂN BỔ THEO MỨC SAO");
        dhc.setCellStyle(header);
        sheet.addMergedRegion(new CellRangeAddress(rn - 1, rn - 1, 0, 3));

        Row dhr = sheet.createRow(rn++);
        createCell(dhr, 0, "Mức đánh giá", header);
        createCell(dhr, 1, "Số lượng",     header);
        createCell(dhr, 2, "Tỷ lệ (%)",    header);
        createCell(dhr, 3, "Biểu đồ",      header);

        String[] labels = {
            "★★★★★  5 sao — Rất hài lòng",
            "★★★★☆  4 sao — Hài lòng",
            "★★★☆☆  3 sao — Bình thường",
            "★★☆☆☆  2 sao — Chưa hài lòng",
            "★☆☆☆☆  1 sao — Rất không hài lòng"
        };
        int[] keys = {5, 4, 3, 2, 1};

        sheet.setColumnWidth(0, 40 * 256);
        sheet.setColumnWidth(1, 14 * 256);
        sheet.setColumnWidth(2, 14 * 256);
        sheet.setColumnWidth(3, 44 * 256);

        for (int i = 0; i < 5; i++) {
            int    key = keys[i];
            int    cnt = (Integer) stats.getOrDefault("count" + key, 0);
            double pct = (Double)  stats.getOrDefault("pct"   + key, 0.0);
            Row row = sheet.createRow(rn++);
            createCell(row, 0, labels[i],           data);
            createCell(row, 1, String.valueOf(cnt),  center);
            createCell(row, 2, pct + "%",            center);
            createCell(row, 3, buildBar(pct),        data);
        }
    }

    // ── Helpers ───────────────────────────────────────────────
    private String buildBar(double pct) {
        int f = (int) Math.round(pct / 5);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 20; i++) sb.append(i < f ? "█" : "░");
        return sb + "  " + pct + "%";
    }

    private String nvl(String s) { return s != null ? s : ""; }

    private void createCell(Row row, int col, String val, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(val);
        if (style != null) c.setCellStyle(style);
    }

    // ── Styles ────────────────────────────────────────────────
    private CellStyle createTitleStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle(); Font f = wb.createFont();
        f.setBold(true); f.setFontHeightInPoints((short) 14);
        f.setColor(IndexedColors.WHITE.getIndex()); s.setFont(f);
        s.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        return s;
    }
    private CellStyle createInfoStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle(); Font f = wb.createFont();
        f.setItalic(true); f.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        s.setFont(f); return s;
    }
    private CellStyle createHeaderStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle(); Font f = wb.createFont();
        f.setBold(true); f.setColor(IndexedColors.WHITE.getIndex()); s.setFont(f);
        s.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorder(s); return s;
    }
    private CellStyle createDataStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        s.setWrapText(true); s.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorder(s); return s;
    }
    private CellStyle createCenterDataStyle(Workbook wb) {
        CellStyle s = createDataStyle(wb); s.setAlignment(HorizontalAlignment.CENTER); return s;
    }
    private CellStyle createStarStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle(); Font f = wb.createFont();
        f.setColor(IndexedColors.ORANGE.getIndex()); f.setBold(true); s.setFont(f);
        s.setAlignment(HorizontalAlignment.CENTER); setBorder(s); return s;
    }
    private CellStyle createHighlightStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle(); Font f = wb.createFont();
        f.setBold(true); f.setFontHeightInPoints((short) 13);
        f.setColor(IndexedColors.DARK_BLUE.getIndex()); s.setFont(f);
        s.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER); setBorder(s); return s;
    }
    private void setBorder(CellStyle s) {
        s.setBorderTop(BorderStyle.THIN); s.setBorderBottom(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN); s.setBorderRight(BorderStyle.THIN);
    }
}