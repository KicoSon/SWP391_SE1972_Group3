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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@WebServlet("/admin/customer-import-preview")
@MultipartConfig
public class ImportCustomerPreviewController extends HttpServlet {

    Pattern phonePattern = Pattern.compile("^[0-9]{10,11}$");
    Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Part filePart = request.getPart("excelFile");
        AdminDAO adDao = new AdminDAO();

        List<Customer> previewList = new ArrayList<>();

        Map<Integer, List<String>> errorMap = new HashMap<>();

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
            Set<String> emailSet = new HashSet<>();
            Set<String> phoneSet = new HashSet<>();

            tierMap.put(1, "Bronze");
            tierMap.put(2, "Silver");
            tierMap.put(3, "Gold");
            tierMap.put(4, "Platinum");

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // bỏ header
                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }
                Customer c = new Customer();

                List<String> errors = new ArrayList<>();

                //NAME----------------------------------------------------------
                String name = formatter.formatCellValue(row.getCell(1)).trim();
                if (name.isEmpty()) {
                    errors.add("Full name is required");
                }

                if (name.length() > 150) {
                    errors.add("Full name max 150 characters");
                }
                c.setFullName(name);

                //EMAIL---------------------------------------------------------
                String email = formatter.formatCellValue(row.getCell(2)).trim();

                if (email.isEmpty()) {
                    errors.add("Email cannot be empty");
                }

                if (email.length() > 150) {
                    errors.add("Email max 150 characters");
                }

                if (!emailPattern.matcher(email).matches()) {
                    errors.add("Invalid email format");
                }

                if (emailSet.contains(email)) {
                    errors.add("Duplicate email in file");
                }

                if (adDao.isEmailExist(email)) {
                    errors.add("Email already exists in DB");
                }
                emailSet.add(email);
                c.setEmail(email);

                //PHONE---------------------------------------------------------
                String phone = formatter.formatCellValue(row.getCell(3)).trim();

                if (phone.isEmpty()) {
                    errors.add("Phone cannot be empty");
                }

                if (!phonePattern.matcher(phone).matches()) {
                    errors.add("Phone must be 10-11 digits");
                }

                if (phone.length() > 20) {
                    errors.add("Phone max 20 characters");
                }

                if (phoneSet.contains(phone)) {
                    errors.add("Duplicate phone in file");
                }

                if (adDao.isPhoneExist(phone)) {
                    errors.add("Phone already exists in DB");
                }
                phoneSet.add(phone);
                c.setPhone(phone);

                //PASSWORD------------------------------------------------------
                String password = formatter.formatCellValue(row.getCell(4)).trim();

                if (password.isEmpty()) {
                    errors.add("Password required");
                }

                if (password.length() < 6) {
                    errors.add("Password must be at least 6 characters");
                }

                if (password.length() > 25) {
                    errors.add("Password max 25 characters");
                }

                if (password.contains(" ")) {
                    errors.add("Password cannot contain spaces");
                }

                c.setPassword(password);

                //ADDRESS-------------------------------------------------------
                String address = formatter.formatCellValue(row.getCell(5)).trim();

                if (address.isEmpty()) {
                    errors.add("Address cannot be empty");
                }

                c.setAddress(address);

                //TIER----------------------------------------------------------
                String tierStr = formatter.formatCellValue(row.getCell(6)).trim();

                try {

                    int tierId = Integer.parseInt(tierStr);

                    if (!adDao.isTierExist(tierId)) {
                        errors.add("Tier ID not found");
                    }

                    c.setTierId(tierId);
                    String tierName = tierMap.getOrDefault(tierId, "Default");

                    c.setTierName(tierName);

                } catch (Exception e) {

                    errors.add("Tier must be a number");

                }

                //STATUS--------------------------------------------------------
                String status = formatter.formatCellValue(row.getCell(7)).trim();

                if (status.isEmpty()) {
                    errors.add("Status cannot be empty");
                }

                if (status.length() > 10) {
                    errors.add("Status max 10 characters");
                }

                if (!status.isEmpty() && !("active".equals(status.toLowerCase())
                        || "inactive".equals(status.toLowerCase()))) {
                    errors.add("Invalid status value");
                }

                c.setStatus(status);

                //OWNER---------------------------------------------------------
                String ownerStr = formatter.formatCellValue(row.getCell(8)).trim();

                try {

                    int ownerId = Integer.parseInt(ownerStr);

                    if (!adDao.isOwnerExist(ownerId)) {
                        errors.add("Owner ID not found");
                    }

                    c.setOwnerId(ownerId);

                } catch (Exception e) {

                    errors.add("Owner must be a number");

                }

                //ADD ERROR TO MAP ---------------------------------------------
                if (!errors.isEmpty()) {
                    errorMap.put(i, errors);
                }

                //ADD-----------------------------------------------------------
                previewList.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Preview size: " + previewList.size());

        request.setAttribute("previewCustomers", previewList);
        request.setAttribute("errorMap", errorMap);

        request.getRequestDispatcher("/admin/customer-import-preview.jsp")
                .forward(request, response);
    }
}
