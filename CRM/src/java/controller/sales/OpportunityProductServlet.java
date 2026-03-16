package controller.sales;

import dal.OpportunityDAO;
import model.sales.OpportunityProduct;
import model.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/sales/opportunity-product-add")
public class OpportunityProductServlet extends HttpServlet {
    private OpportunityDAO opportunityDAO;

    @Override
    public void init() throws ServletException {
        opportunityDAO = new OpportunityDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
        if (userSession == null || (!userSession.isSaleStaff() && !userSession.isAdmin())) {
            response.sendError(403);
            return;
        }

        try {
            int oppId = Integer.parseInt(request.getParameter("opportunityId"));
            OpportunityProduct op = new OpportunityProduct();
            op.setOpportunityId(oppId);
            op.setProductId(Integer.parseInt(request.getParameter("productId")));
            op.setQuantity(Integer.parseInt(request.getParameter("quantity")));
            
            BigDecimal price = new BigDecimal(request.getParameter("unitPrice"));
            BigDecimal discount = new BigDecimal(request.getParameter("discount") != null && !request.getParameter("discount").isEmpty() ? request.getParameter("discount") : "0");
            
            op.setUnitPrice(price);
            op.setDiscount(discount);
            
            // Calc total: (price - discount) * qty
            BigDecimal total = price.subtract(discount).multiply(new BigDecimal(op.getQuantity()));
            op.setTotalPrice(total);
            
            op.setNotes(request.getParameter("notes"));

            opportunityDAO.addOpportunityProduct(op);
            response.sendRedirect(request.getContextPath() + "/sales/opportunity-detail?id=" + oppId);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Error adding product");
        }
    }
}
