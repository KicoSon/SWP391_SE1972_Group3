package controller.marketing;

import dal.CampaignDAO;
import dal.LeadDAO;
import model.Campaign;
import model.Lead;
import model.UserSession;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@WebServlet("/marketingg/importLead")
@MultipartConfig
public class ImportLeadServlet extends HttpServlet {

    private LeadDAO leadDAO;
    private CampaignDAO campaignDAO;

    @Override
    public void init() {

        leadDAO = new LeadDAO();
        campaignDAO = new CampaignDAO();

    }

    // =========================
    // LOAD PAGE
    // =========================
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        List<Campaign> campaignList =
                campaignDAO.filterCampaigns(null, "ACTIVE");

        request.setAttribute("campaignList", campaignList);

        request.getRequestDispatcher(
                "/marketingg/importLead.jsp")
                .forward(request, response);

    }

    // =========================
    // IMPORT EXCEL
    // =========================
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            request.setCharacterEncoding("UTF-8");

            String campaignIdRaw = request.getParameter("campaignId");
            Long campaignId = Long.parseLong(campaignIdRaw);

            HttpSession session = request.getSession(false);

            UserSession userSession =
                    (UserSession) session.getAttribute("userSession");

            long staffId = userSession.getUserId();

            Part filePart = request.getPart("excelFile");

            InputStream inputStream = filePart.getInputStream();

            Workbook workbook = new XSSFWorkbook(inputStream);

            Sheet sheet = workbook.getSheetAt(0);

            DataFormatter formatter = new DataFormatter();

            int imported = 0;
            int duplicate = 0;
            int emptyRow = 0;

            // =========================
            // READ EXCEL
            // =========================
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null) {
                    emptyRow++;
                    continue;
                }

                String fullName = formatter.formatCellValue(row.getCell(0)).trim();
                String phone = formatter.formatCellValue(row.getCell(1)).trim();
                String email = formatter.formatCellValue(row.getCell(2)).trim();
                String address = formatter.formatCellValue(row.getCell(3)).trim();
                String productInterest = formatter.formatCellValue(row.getCell(4)).trim();

                // skip empty lead
                if (fullName.isEmpty() && email.isEmpty()) {
                    emptyRow++;
                    continue;
                }

                // check duplicate email
                if (!email.isEmpty() && leadDAO.isEmailExist(email)) {

                    duplicate++;
                    continue;

                }

                Lead lead = new Lead();

                lead.setFullName(fullName);
                lead.setPhone(phone);
                lead.setEmail(email);
                lead.setAddress(address);
                lead.setProductInterest(productInterest);

                // auto field
                lead.setSource("Excel");
                lead.setStatus("new");

                lead.setCampaignId(campaignId);
                lead.setCreatedBy(staffId);

                leadDAO.insertLead(lead);

                imported++;

            }

            workbook.close();

            session.setAttribute("success",
                    "Import hoàn tất | Imported: "
                    + imported
                    + " | Duplicate: "
                    + duplicate
                    + " | Empty Row: "
                    + emptyRow);

            response.sendRedirect(
                    request.getContextPath()
                    + "/marketing/leadmanagement");

        } catch (Exception e) {

            e.printStackTrace();

            request.setAttribute("error",
                    "Import thất bại");

            doGet(request, response);

        }

    }

}