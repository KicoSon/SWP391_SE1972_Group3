package controller.customerservice;

import dal.CustomerFeedbackDAO;
import dal.TicketFeedbackDAO;
import model.CustomerFeedback;
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
 * Xuất Excel báo cáo feedback — 3 sheets: Sheet 1 "Customer Feedback" — danh
 * sách từ bảng customer_feedback Sheet 2 "Ticket Feedback" — danh sách từ bảng
 * ticket_feedback (kèm cột Ticket) Sheet 3 "Thống kê tổng hợp" — gộp cả 2
 * nguồn: tổng số, avg rating, phân bổ
 *
 * URL: /customerservice/exportfeedback Không có filter — luôn xuất toàn bộ data
 */
@WebServlet("/customerservice/exportfeedback")
public class ExportFeedbackServlet extends HttpServlet {

    private CustomerFeedbackDAO customerFeedbackDAO;
    private TicketFeedbackDAO ticketFeedbackDAO;

    @Override
    public void init() {
        customerFeedbackDAO = new CustomerFeedbackDAO();
        ticketFeedbackDAO = new TicketFeedbackDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // ── Auth ──────────────────────────────────────────────
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

        // ── Lấy dữ liệu từ cả 2 bảng ─────────────────────────
        List<CustomerFeedback> customerList = customerFeedbackDAO.getAllFeedbacks();
        List<TicketFeedback> ticketList = ticketFeedbackDAO.getFeedbacksByRating(0);

        // Stats gộp từ DashboardDAO (reuse logic UNION ALL)
        // Dùng trực tiếp từ 2 DAO riêng để đơn giản
        Map<String, Object> customerStats = customerFeedbackDAO.getFeedbackStats();
        Map<String, Object> ticketStats = ticketFeedbackDAO.getFeedbackStats();

        // ── Tên file kèm timestamp ────────────────────────────
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "BaoCaoFeedback_" + timestamp + ".xlsx";

        // ── HTTP headers ──────────────────────────────────────
        resp.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.setHeader("Content-Disposition",
                "attachment; filename=\"" + fileName + "\"");
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");

        // ── Build workbook ────────────────────────────────────
        try (XSSFWorkbook wb = new XSSFWorkbook(); OutputStream out = resp.getOutputStream()) {

            buildSheet1_CustomerFeedback(wb, customerList);
            buildSheet2_TicketFeedback(wb, ticketList);
            buildSheet3_Statistics(wb, customerStats, ticketStats,
                    customerList.size(), ticketList.size());

            wb.write(out);
        }
    }

    // =========================================================
    // Sheet 1: Customer Feedback
    // Cột: STT | Khách hàng | Rating | Sao | Mức độ | Nhận xét | Ngày đánh giá
    // =========================================================
    private void buildSheet1_CustomerFeedback(Workbook wb,
            List<CustomerFeedback> list) {
        Sheet sheet = wb.createSheet("Customer Feedback");

        CellStyle title = createTitleStyle(wb, IndexedColors.DARK_BLUE);
        CellStyle info = createInfoStyle(wb);
        CellStyle header = createHeaderStyle(wb, IndexedColors.CORNFLOWER_BLUE);
        CellStyle data = createDataStyle(wb);
        CellStyle star = createStarStyle(wb);
        CellStyle center = createCenterStyle(wb);

        // Tiêu đề
        Row r0 = sheet.createRow(0);
        r0.setHeightInPoints(28);
        mkCell(r0, 0, "DANH SÁCH FEEDBACK KHÁCH HÀNG", title);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

        // Info
        Row r1 = sheet.createRow(1);
        mkCell(r1, 0, "Tổng số: " + list.size() + "  |  Ngày xuất: "
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                info);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));

        // Header
        String[] headers = {"STT", "Khách hàng", "Rating", "Sao",
            "Mức độ hài lòng", "Nhận xét", "Ngày đánh giá"};
        Row hr = sheet.createRow(3);
        hr.setHeightInPoints(20);
        for (int i = 0; i < headers.length; i++) {
            mkCell(hr, i, headers[i], header);
        }

        // Column widths
        sheet.setColumnWidth(0, 8 * 256);
        sheet.setColumnWidth(1, 28 * 256);
        sheet.setColumnWidth(2, 10 * 256);
        sheet.setColumnWidth(3, 14 * 256);
        sheet.setColumnWidth(4, 22 * 256);
        sheet.setColumnWidth(5, 48 * 256);
        sheet.setColumnWidth(6, 22 * 256);

        // Data
        int rn = 4;
        for (int i = 0; i < list.size(); i++) {
            CustomerFeedback fb = list.get(i);
            Row row = sheet.createRow(rn++);
            row.setHeightInPoints(20);
            mkCell(row, 0, String.valueOf(i + 1), center);
            mkCell(row, 1, nvl(fb.getCustomerName()), data);
            mkCell(row, 2, fb.getRating() + " / 5", center);
            mkCell(row, 3, fb.getStarDisplay(), star);
            mkCell(row, 4, fb.getRatingLabel(), data);
            mkCell(row, 5, nvl(fb.getComments()), data);
            mkCell(row, 6, nvl(fb.getCreatedAt()), data);
        }

        // Tổng
        addTotalRow(wb, sheet, rn + 1, list.size(), header, data);
    }

    // =========================================================
    // Sheet 2: Ticket Feedback
    // Cột: STT | Ticket ID | Tiêu đề | Ưu tiên | Khách hàng |
    //          Rating | Sao | Mức độ | Nhận xét | Ngày đánh giá
    // =========================================================
    private void buildSheet2_TicketFeedback(Workbook wb,
            List<TicketFeedback> list) {
        Sheet sheet = wb.createSheet("Ticket Feedback");

        CellStyle title = createTitleStyle(wb, IndexedColors.DARK_TEAL);
        CellStyle info = createInfoStyle(wb);
        CellStyle header = createHeaderStyle(wb, IndexedColors.TEAL);
        CellStyle data = createDataStyle(wb);
        CellStyle star = createStarStyle(wb);
        CellStyle center = createCenterStyle(wb);

        // Tiêu đề
        Row r0 = sheet.createRow(0);
        r0.setHeightInPoints(28);
        mkCell(r0, 0, "DANH SÁCH FEEDBACK THEO TICKET", title);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));

        // Info
        Row r1 = sheet.createRow(1);
        mkCell(r1, 0, "Tổng số: " + list.size() + "  |  Ngày xuất: "
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                info);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 9));

        // Header
        String[] headers = {"STT", "Ticket ID", "Tiêu đề Ticket", "Ưu tiên",
            "Khách hàng", "Rating", "Sao",
            "Mức độ hài lòng", "Nhận xét", "Ngày đánh giá"};
        Row hr = sheet.createRow(3);
        hr.setHeightInPoints(20);
        for (int i = 0; i < headers.length; i++) {
            mkCell(hr, i, headers[i], header);
        }

        // Column widths
        sheet.setColumnWidth(0, 8 * 256);
        sheet.setColumnWidth(1, 12 * 256);
        sheet.setColumnWidth(2, 30 * 256);
        sheet.setColumnWidth(3, 12 * 256);
        sheet.setColumnWidth(4, 26 * 256);
        sheet.setColumnWidth(5, 10 * 256);
        sheet.setColumnWidth(6, 14 * 256);
        sheet.setColumnWidth(7, 20 * 256);
        sheet.setColumnWidth(8, 44 * 256);
        sheet.setColumnWidth(9, 22 * 256);

        // Data
        int rn = 4;
        for (int i = 0; i < list.size(); i++) {
            TicketFeedback fb = list.get(i);
            Row row = sheet.createRow(rn++);
            row.setHeightInPoints(20);
            mkCell(row, 0, String.valueOf(i + 1), center);
            mkCell(row, 1, "#" + fb.getTicketId(), center);
            mkCell(row, 2, nvl(fb.getTicketTitle()), data);
            mkCell(row, 3, nvl(fb.getTicketPriority()), center);
            mkCell(row, 4, nvl(fb.getCustomerName()), data);
            mkCell(row, 5, fb.getRating() + " / 5", center);
            mkCell(row, 6, fb.getStarDisplay(), star);
            mkCell(row, 7, fb.getRatingLabel(), data);
            mkCell(row, 8, nvl(fb.getComments()), data);
            mkCell(row, 9, nvl(fb.getCreatedAt()), data);
        }

        addTotalRow(wb, sheet, rn + 1, list.size(), header, data);
    }

    // =========================================================
    // Sheet 3: Thống kê tổng hợp
    // Gồm: tổng số từng nguồn, avg rating từng nguồn,
    //      phân bổ rating cạnh nhau (Customer | Ticket)
    // =========================================================
    private void buildSheet3_Statistics(Workbook wb,
            Map<String, Object> cfStats,
            Map<String, Object> tfStats,
            int cfTotal, int tfTotal) {
        Sheet sheet = wb.createSheet("Thống kê tổng hợp");

        CellStyle title = createTitleStyle(wb, IndexedColors.DARK_BLUE);
        CellStyle secHeader = createHeaderStyle(wb, IndexedColors.GREY_50_PERCENT);
        CellStyle cfHeader = createHeaderStyle(wb, IndexedColors.CORNFLOWER_BLUE);
        CellStyle tfHeader = createHeaderStyle(wb, IndexedColors.TEAL);
        CellStyle data = createDataStyle(wb);
        CellStyle hi = createHighlightStyle(wb);
        CellStyle center = createCenterStyle(wb);

        sheet.setColumnWidth(0, 36 * 256);
        sheet.setColumnWidth(1, 22 * 256);
        sheet.setColumnWidth(2, 22 * 256);
        sheet.setColumnWidth(3, 38 * 256);
        sheet.setColumnWidth(4, 38 * 256);

        // ── Tiêu đề chính ──────────────────────────────────
        Row r0 = sheet.createRow(0);
        r0.setHeightInPoints(28);
        mkCell(r0, 0, "THỐNG KÊ TỔNG HỢP PHẢN HỒI KHÁCH HÀNG", title);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

        Row r1 = sheet.createRow(1);
        mkCell(r1, 0, "Ngày xuất: "
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                createInfoStyle(wb));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));

        // ── Section A: Tổng quan ───────────────────────────
        int rn = 3;
        Row secA = sheet.createRow(rn++);
        mkCell(secA, 0, "A. TỔNG QUAN", secHeader);
        sheet.addMergedRegion(new CellRangeAddress(rn - 1, rn - 1, 0, 4));

        // Header dòng nguồn
        Row colHeader = sheet.createRow(rn++);
        mkCell(colHeader, 0, "", data);
        mkCell(colHeader, 1, "Customer Feedback", cfHeader);
        mkCell(colHeader, 2, "Ticket Feedback", tfHeader);
        mkCell(colHeader, 3, "Tổng cộng", secHeader);

        // Tổng số
        Row rowTotal = sheet.createRow(rn++);
        mkCell(rowTotal, 0, "Tổng số phản hồi", data);
        mkCell(rowTotal, 1, String.valueOf(cfTotal), hi);
        mkCell(rowTotal, 2, String.valueOf(tfTotal), hi);
        mkCell(rowTotal, 3, String.valueOf(cfTotal + tfTotal), hi);

        // Rating trung bình
        Row rowAvg = sheet.createRow(rn++);
        mkCell(rowAvg, 0, "Rating trung bình", data);
        mkCell(rowAvg, 1, cfStats.getOrDefault("avgRating", 0.0) + " / 5.0", hi);
        mkCell(rowAvg, 2, tfStats.getOrDefault("avgRating", 0.0) + " / 5.0", hi);

        // Hài lòng (4-5 sao)
        Row rowSat = sheet.createRow(rn++);
        int cfSat = (Integer) cfStats.getOrDefault("count4", 0)
                + (Integer) cfStats.getOrDefault("count5", 0);
        int tfSat = (Integer) tfStats.getOrDefault("count4", 0)
                + (Integer) tfStats.getOrDefault("count5", 0);
        mkCell(rowSat, 0, "Hài lòng (4–5 sao)", data);
        mkCell(rowSat, 1, cfSat + " (" + pct(cfSat, cfTotal) + "%)", center);
        mkCell(rowSat, 2, tfSat + " (" + pct(tfSat, tfTotal) + "%)", center);
        mkCell(rowSat, 3, String.valueOf(cfSat + tfSat), center);

        // Chưa hài lòng (1-2 sao)
        Row rowDis = sheet.createRow(rn++);
        int cfDis = (Integer) cfStats.getOrDefault("count1", 0)
                + (Integer) cfStats.getOrDefault("count2", 0);
        int tfDis = (Integer) tfStats.getOrDefault("count1", 0)
                + (Integer) tfStats.getOrDefault("count2", 0);
        mkCell(rowDis, 0, "Chưa hài lòng (1–2 sao)", data);
        mkCell(rowDis, 1, cfDis + " (" + pct(cfDis, cfTotal) + "%)", center);
        mkCell(rowDis, 2, tfDis + " (" + pct(tfDis, tfTotal) + "%)", center);
        mkCell(rowDis, 3, String.valueOf(cfDis + tfDis), center);

        rn++; // khoảng trống

        // ── Section B: Phân bổ từng mức rating ────────────
        Row secB = sheet.createRow(rn++);
        mkCell(secB, 0, "B. PHÂN BỔ THEO MỨC SAO", secHeader);
        sheet.addMergedRegion(new CellRangeAddress(rn - 1, rn - 1, 0, 4));

        // Header
        Row distH = sheet.createRow(rn++);
        mkCell(distH, 0, "Mức đánh giá", secHeader);
        mkCell(distH, 1, "Customer — Số lượng (%)", cfHeader);
        mkCell(distH, 2, "Ticket — Số lượng (%)", tfHeader);
        mkCell(distH, 3, "Customer — Biểu đồ", cfHeader);
        mkCell(distH, 4, "Ticket — Biểu đồ", tfHeader);

        String[] starLabels = {
            "★★★★★  5 sao — Rất hài lòng",
            "★★★★☆  4 sao — Hài lòng",
            "★★★☆☆  3 sao — Bình thường",
            "★★☆☆☆  2 sao — Chưa hài lòng",
            "★☆☆☆☆  1 sao — Rất không hài lòng"
        };
        int[] keys = {5, 4, 3, 2, 1};

        for (int i = 0; i < 5; i++) {
            int key = keys[i];
            int cfCnt = (Integer) cfStats.getOrDefault("count" + key, 0);
            int tfCnt = (Integer) tfStats.getOrDefault("count" + key, 0);
            double cfPct = (Double) cfStats.getOrDefault("pct" + key, 0.0);
            double tfPct = (Double) tfStats.getOrDefault("pct" + key, 0.0);

            Row row = sheet.createRow(rn++);
            mkCell(row, 0, starLabels[i], data);
            mkCell(row, 1, cfCnt + "  (" + cfPct + "%)", center);
            mkCell(row, 2, tfCnt + "  (" + tfPct + "%)", center);
            mkCell(row, 3, buildBar(cfPct), data);
            mkCell(row, 4, buildBar(tfPct), data);
        }
    }

    // =========================================================
    // Helpers
    // =========================================================
    private void addTotalRow(Workbook wb, Sheet sheet, int rowNum,
            int total, CellStyle labelStyle, CellStyle valStyle) {
        Row row = sheet.createRow(rowNum);
        mkCell(row, 0, "Tổng số:", labelStyle);
        mkCell(row, 1, String.valueOf(total), valStyle);
    }

    private String buildBar(double pct) {
        int filled = (int) Math.round(pct / 5); // 0..20 ô
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            sb.append(i < filled ? "█" : "░");
        }
        return sb + "  " + pct + "%";
    }

    private double pct(int part, int total) {
        if (total == 0) {
            return 0.0;
        }
        return Math.round(part * 1000.0 / total) / 10.0;
    }

    private String nvl(String s) {
        return s != null ? s : "";
    }

    private void mkCell(Row row, int col, String val, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(val);
        if (style != null) {
            c.setCellStyle(style);
        }
    }

    // =========================================================
    // Cell Styles
    // =========================================================
    private CellStyle createTitleStyle(Workbook wb, IndexedColors bgColor) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        f.setFontHeightInPoints((short) 14);
        f.setColor(IndexedColors.WHITE.getIndex());
        s.setFont(f);
        s.setFillForegroundColor(bgColor.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        return s;
    }

    private CellStyle createInfoStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setItalic(true);
        f.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        s.setFont(f);
        return s;
    }

    private CellStyle createHeaderStyle(Workbook wb, IndexedColors bgColor) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        f.setColor(IndexedColors.WHITE.getIndex());
        s.setFont(f);
        s.setFillForegroundColor(bgColor.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorder(s);
        return s;
    }

    private CellStyle createDataStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        s.setWrapText(true);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorder(s);
        return s;
    }

    private CellStyle createCenterStyle(Workbook wb) {
        CellStyle s = createDataStyle(wb);
        s.setAlignment(HorizontalAlignment.CENTER);
        return s;
    }

    private CellStyle createStarStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setColor(IndexedColors.ORANGE.getIndex());
        f.setBold(true);
        s.setFont(f);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorder(s);
        return s;
    }

    private CellStyle createHighlightStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        f.setFontHeightInPoints((short) 12);
        f.setColor(IndexedColors.DARK_BLUE.getIndex());
        s.setFont(f);
        s.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        setBorder(s);
        return s;
    }

    private void setBorder(CellStyle s) {
        s.setBorderTop(BorderStyle.THIN);
        s.setBorderBottom(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN);
        s.setBorderRight(BorderStyle.THIN);
    }
}
