package controller.marketing;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import dal.CampaignDAO;
import model.Campaign;

import java.io.IOException;
import java.util.List;

@WebServlet("/home")
public class LandingPageServlet extends HttpServlet {

    private CampaignDAO campaignDAO;

    @Override
    public void init() throws ServletException {
        campaignDAO = new CampaignDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Lấy danh sách campaign ACTIVE
            List<Campaign> list = campaignDAO.getActiveCampaigns();

            // Debug (in ra console)
            System.out.println("Campaign size: " + list.size());

            // Gửi sang JSP
            request.setAttribute("campaignList", list);

            // Forward sang landing page
            request.getRequestDispatcher("/marketingg/landing.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Lỗi load landing page!");
        }
    }
}