package controller.marketing;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import dal.LeadDAO;
import dal.UserDao;
import model.Lead;

import java.io.IOException;
import java.util.List;
import model.Staff;

@WebServlet("/marketing/assignLead")
public class AssignLeadToSalesServlet extends HttpServlet {

    private LeadDAO leadDAO;
    private UserDao UserDao;

    @Override
    public void init() throws ServletException {

        leadDAO = new LeadDAO();

        UserDao = new UserDao();
    }


    // HIỂN THỊ LIST
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        try {

            // list lead qualified
            List<Lead> leadList =
                    leadDAO.getQualifiedLeadsFull();

            // list sales staff
            List<Staff> salesList =
                    UserDao.getSalesList();

            request.setAttribute("leadList", leadList);

            request.setAttribute("salesList", salesList);


            request.getRequestDispatcher(
                    "/marketingg/assignLead.jsp")
                    .forward(request, response);

        } catch (Exception e) {

            e.printStackTrace();

            response.sendRedirect(
                    request.getContextPath() + "/error.jsp");
        }

    }


    // ASSIGN ACTION
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        try {

            Long leadId =
                    Long.parseLong(
                            request.getParameter("leadId"));

            Long saleId =
                    Long.parseLong(
                            request.getParameter("saleId"));


            leadDAO.assignLeadToSale(leadId, saleId);


            response.sendRedirect(
                    request.getContextPath()
                            + "/marketing/assignLead");


        } catch (Exception e) {

            e.printStackTrace();

            response.sendRedirect(
                    request.getContextPath() + "/error.jsp");

        }

    }

}