package controller.marketing;

import dal.CampaignDAO;
import dal.LeadDAO;
import model.Lead;
import model.Campaign;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/marketingg/editLead")
public class EditLeadServlet extends HttpServlet {

    private LeadDAO dao;

    @Override
    public void init() {

        dao = new LeadDAO();

    }

    // =========================
    // LOAD DATA CŨ
    // =========================
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        long id =
                Long.parseLong(request.getParameter("id"));

        Lead lead =
                dao.getLeadById(id);

        CampaignDAO campaignDAO =
                new CampaignDAO();

        List<Campaign> campaignList =
                campaignDAO.filterCampaigns(null, "ACTIVE");

        request.setAttribute("lead", lead);

        request.setAttribute("campaignList",
                campaignList);

        request.getRequestDispatcher(
                "/marketingg/editLead.jsp")
                .forward(request, response);

    }


    // =========================
    // UPDATE LEAD
    // =========================
@Override
protected void doPost(HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException {

    try {

        request.setCharacterEncoding("UTF-8");


        long id =
                Long.parseLong(request.getParameter("id"));


        String fullName =
                request.getParameter("fullName");

        String phone =
                request.getParameter("phone");

        String email =
                request.getParameter("email");

        String address =
                request.getParameter("address");

        String productInterest =
                request.getParameter("productInterest");

        String source =
                request.getParameter("source");

        String campaignIdRaw =
                request.getParameter("campaignId");


        Lead old =
                dao.getLeadById(id);


        // VALIDATE NAME
        if (fullName == null || fullName.trim().isEmpty()) {

            request.setAttribute("error",
                    "Tên không được để trống");

            request.setAttribute("lead", old);

            request.getRequestDispatcher(
                    "/marketingg/editLead.jsp")
                    .forward(request, response);

            return;

        }


        // VALIDATE EMAIL
        if (!email.equalsIgnoreCase(old.getEmail())
                && dao.isEmailExist(email)) {

            request.setAttribute("error",
                    "Email đã tồn tại");

            request.setAttribute("lead", old);

            request.getRequestDispatcher(
                    "/marketingg/editLead.jsp")
                    .forward(request, response);

            return;

        }


        // =========================
        // PARSE CAMPAIGN
        // =========================

        Long campaignId = old.getCampaignId();

        // chỉ cho sửa nếu NEW hoặc NURTURING
        if (old.getStatus().equalsIgnoreCase("new")
                || old.getStatus().equalsIgnoreCase("nurturing")) {

            if (campaignIdRaw != null
                    && !campaignIdRaw.isEmpty()) {

                campaignId =
                        Long.parseLong(campaignIdRaw);

            } else {

                campaignId = null;

            }

        }



        // =========================
        // UPDATE MODEL
        // =========================

        Lead lead =
                new Lead();

        lead.setId(id);

        lead.setFullName(fullName);

        lead.setPhone(phone);

        lead.setEmail(email);

        lead.setAddress(address);

        lead.setProductInterest(productInterest);

        lead.setSource(source);

        lead.setCampaignId(campaignId);



        dao.updateLead(lead);



        HttpSession session =
                request.getSession();

        session.setAttribute("success",
                "Cập nhật Lead thành công");


        response.sendRedirect(
                request.getContextPath()
                + "/marketing/leadmanagement");



    } catch (Exception e) {

        e.printStackTrace();

    }

}

}