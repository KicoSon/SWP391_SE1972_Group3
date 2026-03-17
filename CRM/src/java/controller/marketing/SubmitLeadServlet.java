package controller.marketing;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import dal.LeadDAO;
import model.Lead;

import java.io.IOException;

@WebServlet("/submitLead")
public class SubmitLeadServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        LeadDAO dao = new LeadDAO();

        // ===== LẤY DATA =====
        String name = request.getParameter("name");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String address = request.getParameter("address");
        String product = request.getParameter("productInterest");
        String campaignRaw = request.getParameter("campaignId");

        Long campaignId = null;

        try {
            if (campaignRaw != null && !campaignRaw.isEmpty()) {
                campaignId = Long.parseLong(campaignRaw);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ===== VALIDATE =====
        if (name == null || name.trim().isEmpty()
                || phone == null || phone.trim().isEmpty()) {

            saveFormToSession(request, name, phone, email, address, product, campaignId);

            request.getSession().setAttribute("error", "❌ Vui lòng nhập đủ thông tin!");
            response.sendRedirect("home");
            return;
        }

        // ===== CHECK EMAIL TRÙNG =====
        if (email != null && !email.trim().isEmpty()) {
            if (dao.isEmailExist(email)) {

                saveFormToSession(request, name, phone, email, address, product, campaignId);

                request.getSession().setAttribute("error", "❌ Email đã tồn tại!");
                response.sendRedirect("home");
                return;
            }
        }

        // ===== TẠO LEAD =====
        Lead l = new Lead();
        l.setFullName(name);
        l.setPhone(phone);
        l.setEmail(email);
        l.setAddress(address);
        l.setProductInterest(product);
        l.setSource("banner");
        l.setStatus("new");
        l.setCampaignId(campaignId);
        l.setCreatedBy(null);

        boolean success = dao.insertLead(l);

        if (success) {
            request.getSession().setAttribute("success", "🎉 Đăng ký thành công!");
        } else {
            saveFormToSession(request, name, phone, email, address, product, campaignId);
            request.getSession().setAttribute("error", "❌ Có lỗi xảy ra!");
        }

        response.sendRedirect("home");
    }

    // ===== SAVE FORM =====
    private void saveFormToSession(HttpServletRequest request,
                                  String name, String phone, String email,
                                  String address, String product, Long campaignId) {

        HttpSession session = request.getSession();

        session.setAttribute("form_name", name);
        session.setAttribute("form_phone", phone);
        session.setAttribute("form_email", email);
        session.setAttribute("form_address", address);
        session.setAttribute("form_product", product);
        session.setAttribute("form_campaignId", campaignId);
    }
}