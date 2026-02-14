package controller.marketing;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dal.CampaignDAO;
import model.Campaign;

import java.io.IOException;
import java.util.List;

@WebServlet("/marketing/campaignmanagement")
public class CampaignManagementServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private CampaignDAO campaignDAO;

    @Override
    public void init() throws ServletException {

        campaignDAO = new CampaignDAO();

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        response.setCharacterEncoding("UTF-8");

        String search = request.getParameter("search");

        String statusFilter = request.getParameter("statusFilter");

        try {

            List<Campaign> campaignList =
                    campaignDAO.filterCampaigns(search, statusFilter);

            request.setAttribute("campaignList", campaignList);

            request.getRequestDispatcher("/marketingg/campaignmanagement.jsp")
                    .forward(request, response);

        } catch (Exception e) {

            e.printStackTrace();

            request.setAttribute("errorMessage",
                    "Lỗi khi tải danh sách Campaign!");

            request.getRequestDispatcher("/error.jsp")
                    .forward(request, response);

        }

    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        doGet(request, response);

    }

}
