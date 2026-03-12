package controller.customer;

import dal.AdminDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import model.Customer;

import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/customer-import-preview")
@MultipartConfig
public class ImportCustomerPreviewController extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Part filePart = request.getPart("excelFile");
        AdminDAO adDao = new AdminDAO();

        List<Customer> previewList = new ArrayList<>();

        if (filePart == null || filePart.getSize() == 0) {
            request.setAttribute("previewCustomers", previewList);
            request.getRequestDispatcher("/admin/customer-import-preview.jsp")
                    .forward(request, response);
            return;
        }

        try (InputStream is = filePart.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            DataFormatter formatter = new DataFormatter();

            Map<Integer, String> tierMap = new HashMap<>();

            tierMap.put(1, "Bronze");
            tierMap.put(2, "Silver");
            tierMap.put(3, "Gold");
            tierMap.put(4, "Platinum");

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // bỏ header
                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                String name = formatter.formatCellValue(row.getCell(1));

                if (name == null || name.trim().isEmpty()) {
                    continue;
                }

                Customer c = new Customer();

                String idStr = formatter.formatCellValue(row.getCell(0));
                if (!idStr.isEmpty()) {
                    c.setId(Integer.parseInt(idStr));
                }

                c.setFullName(name);
                c.setEmail(formatter.formatCellValue(row.getCell(2)));
                c.setPhone(formatter.formatCellValue(row.getCell(3)));
                c.setPassword(formatter.formatCellValue(row.getCell(4)));
                c.setAddress(formatter.formatCellValue(row.getCell(5)));

                String tierStr = formatter.formatCellValue(row.getCell(6));

                if (!tierStr.isEmpty()) {

                    int tierId = Integer.parseInt(tierStr);

                    c.setTierId(tierId);

                    String tierName = tierMap.getOrDefault(tierId, "Default");

                    c.setTierName(tierName);
                }

                c.setStatus(formatter.formatCellValue(row.getCell(7)));

                String ownerStr = formatter.formatCellValue(row.getCell(8));

                if (!ownerStr.equals("")) {
                    try {
                        int ownerId = Integer.parseInt(ownerStr);
                        c.setOwnerId(ownerId);
                        String ownerName = adDao.getStaffNameById(ownerId);
                        c.setOwnerName(ownerName);
                    } catch (Exception e) {
                        c.setOwnerId(-1);
                    }
                }

                previewList.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Preview size: " + previewList.size());

        request.setAttribute("previewCustomers", previewList);

        request.getRequestDispatcher("/admin/customer-import-preview.jsp")
                .forward(request, response);
    }
}
