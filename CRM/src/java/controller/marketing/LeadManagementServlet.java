package controller.marketing;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dal.LeadDAO;
import model.Lead;

import java.io.IOException;
import java.util.List;

@WebServlet("/marketing/leadmanagement")
public class LeadManagementServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private LeadDAO leadDAO;

    @Override
    public void init() throws ServletException {

        leadDAO = new LeadDAO();

    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        response.setCharacterEncoding("UTF-8");


        String search = request.getParameter("search");

        String statusFilter = request.getParameter("statusFilter");


        try {

            List<Lead> leadList =
                    leadDAO.filterLeads(search, statusFilter);


            request.setAttribute("leadList", leadList);


            request.getRequestDispatcher(
                    "/marketingg/leadmanagement.jsp")
                    .forward(request, response);


        } catch (Exception e) {

            e.printStackTrace();

            request.setAttribute("errorMessage",
                    "Lỗi khi tải danh sách Lead!");

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
