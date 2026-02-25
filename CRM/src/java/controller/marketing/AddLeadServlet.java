package controller.marketing;

import dal.CampaignDAO;
import dal.LeadDAO;
import model.Lead;
import model.UserSession;
import model.Campaign;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/marketingg/addLead")
public class AddLeadServlet extends HttpServlet {

    private LeadDAO leadDAO;
    private CampaignDAO campaignDAO;

    @Override
    public void init() {

        leadDAO = new LeadDAO();
        campaignDAO = new CampaignDAO();

    }

    // =========================
    // LOAD FORM
    // =========================
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        loadCampaignList(request);

        request.getRequestDispatcher(
                "/marketingg/addLead.jsp")
                .forward(request, response);

    }

    // =========================
    // INSERT LEAD
    // =========================
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            request.setCharacterEncoding("UTF-8");

            // GET DATA
            String fullName = request.getParameter("fullName");
            String phone = request.getParameter("phone");
            String email = request.getParameter("email");
            String address = request.getParameter("address");
            String productInterest = request.getParameter("productInterest");
            String source = request.getParameter("source");
            String campaignIdRaw = request.getParameter("campaignId");


            // =========================
            // GIỮ LẠI DATA CŨ
            // =========================

            request.setAttribute("fullName", fullName);
            request.setAttribute("phone", phone);
            request.setAttribute("email", email);
            request.setAttribute("address", address);
            request.setAttribute("productInterest", productInterest);
            request.setAttribute("source", source);
            request.setAttribute("campaignId", campaignIdRaw);


            // =========================
            // VALIDATE NAME
            // =========================

            if (fullName == null || fullName.trim().isEmpty()) {

                request.setAttribute("error",
                        "Tên Lead không được để trống");

                loadCampaignList(request);

                request.getRequestDispatcher(
                        "/marketingg/addLead.jsp")
                        .forward(request, response);

                return;
            }


            // =========================
            // VALIDATE EMAIL EXIST
            // =========================

            if (email != null
                    && !email.trim().isEmpty()
                    && leadDAO.isEmailExist(email)) {

                request.setAttribute("error",
                        "Email đã tồn tại");

                loadCampaignList(request);

                request.getRequestDispatcher(
                        "/marketingg/addLead.jsp")
                        .forward(request, response);

                return;
            }


            // =========================
            // GET STAFF SESSION
            // =========================

            HttpSession session = request.getSession(false);

            UserSession userSession =
                    (UserSession) session.getAttribute("userSession");

            long staffId = userSession.getUserId();


            // =========================
            // PARSE CAMPAIGN ID
            // =========================

            Long campaignId = null;

            if (campaignIdRaw != null && !campaignIdRaw.isEmpty()) {

                campaignId = Long.parseLong(campaignIdRaw);

            }


            // =========================
            // SET MODEL
            // =========================

            Lead lead = new Lead();

            lead.setFullName(fullName);
            lead.setPhone(phone);
            lead.setEmail(email);
            lead.setAddress(address);
            lead.setProductInterest(productInterest);
            lead.setSource(source);
            lead.setStatus("new");
            lead.setCampaignId(campaignId);
            lead.setCreatedBy(staffId);


            // =========================
            // INSERT DB
            // =========================

            leadDAO.insertLead(lead);


            // SUCCESS MESSAGE
            session.setAttribute("success",
                    "Thêm Lead thành công");


            response.sendRedirect(
                    request.getContextPath()
                            + "/marketing/leadmanagement");

        } catch (Exception e) {

            e.printStackTrace();

            request.setAttribute("error",
                    "Lỗi hệ thống");

            loadCampaignList(request);

            request.getRequestDispatcher(
                    "/marketingg/addLead.jsp")
                    .forward(request, response);

        }

    }


    // =========================
    // LOAD CAMPAIGN LIST FUNCTION
    // =========================

    private void loadCampaignList(HttpServletRequest request) {

        List<Campaign> campaignList =
                campaignDAO.filterCampaigns(null, "ACTIVE");

        request.setAttribute("campaignList", campaignList);

    }

}
