package controller.marketing;

import dal.LeadDAO;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/marketing/updateLeadStatus")
public class UpdateLeadStatusServlet extends HttpServlet {

    LeadDAO leadDAO = new LeadDAO();

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        try {

            long leadId =
                    Long.parseLong(
                        request.getParameter("leadId"));

            String status =
                    request.getParameter("status");

            leadDAO.updateStatus(leadId, status);

            response.sendRedirect(
                request.getContextPath()
                + "/marketing/leadmanagement");

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

}