package controller.sales;

import dal.QuotationDAO;
import model.sales.Quotation;
import model.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/sales/quotation-version")
public class QuotationVersionServlet extends HttpServlet {

    private QuotationDAO quotationDAO;

    @Override
    public void init() throws ServletException {
        quotationDAO = new QuotationDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
        if (userSession == null || !userSession.isStaff()) {
            response.sendRedirect(request.getContextPath() + "/login"); return;
        }

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Quotation newQ = quotationDAO.createNewVersion(id);
            if (newQ != null) {
                response.sendRedirect(request.getContextPath() + "/sales/quotation-detail?id=" + newQ.getId());
            } else {
                response.sendError(500, "Failed to create new version");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
    }
}
