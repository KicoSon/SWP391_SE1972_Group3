package controller;

import dal.LeadDAO;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Map;

@WebServlet("/marketingg/dashboard")
public class MarketingDashboardController extends HttpServlet {

    private LeadDAO leadDAO;

    @Override
    public void init() {
        leadDAO = new LeadDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        try {

            // =============================
            // TOTAL LEADS
            // =============================

            int totalLeads = leadDAO.getTotalLeads();


            // =============================
            // STATUS STATS
            // =============================

            Map<String, Integer> statusStats
                    = leadDAO.getLeadStatusStats();


            // =============================
            // SOURCE STATS
            // =============================

            Map<String, Integer> sourceStats
                    = leadDAO.getLeadSourceStats();


            // =============================
            // CAMPAIGN STATS
            // =============================

            Map<String, Integer> campaignStats
                    = leadDAO.getTopCampaignStats();


            // =============================
            // SEND TO JSP
            // =============================

            request.setAttribute("totalLeads", totalLeads);

            request.setAttribute("statusStats", statusStats);

            request.setAttribute("sourceStats", sourceStats);

            request.setAttribute("campaignStats", campaignStats);


            // =============================
            // FORWARD
            // =============================

            request.getRequestDispatcher("/marketingg/dashboard.jsp")
                    .forward(request, response);

        } catch (Exception e) {

            e.printStackTrace();

            response.sendRedirect(
                    request.getContextPath() + "/marketingg/dashboard"
            );
        }
    }
}