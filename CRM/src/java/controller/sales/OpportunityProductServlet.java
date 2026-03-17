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
            int oppId = SalesInputValidator.parsePositiveInt("Opportunity", request.getParameter("opportunityId"));
            OpportunityProduct op = new OpportunityProduct();
            op.setOpportunityId(oppId);
            op.setProductId(SalesInputValidator.parsePositiveInt("Sản phẩm", request.getParameter("productId")));
            op.setQuantity(SalesInputValidator.parsePositiveInt("Số lượng", request.getParameter("quantity")));
            
            BigDecimal price = SalesInputValidator.parseNonNegativeDecimal("Đơn giá", request.getParameter("unitPrice"), BigDecimal.ZERO);
            BigDecimal discount = SalesInputValidator.parseNonNegativeDecimal("Chiết khấu", request.getParameter("discount"), BigDecimal.ZERO);
            
            op.setUnitPrice(price);
            op.setDiscount(discount);
            
            // Calc total: (price - discount) * qty
            if (discount.compareTo(price) > 0) {
                throw new IllegalArgumentException("Chiết khấu không được lớn hơn đơn giá");
            }
            BigDecimal total = price.subtract(discount).multiply(new BigDecimal(op.getQuantity()));
            op.setTotalPrice(total);
            
            op.setNotes(SalesInputValidator.optionalText(request.getParameter("notes"), 1000));

            opportunityDAO.addOpportunityProduct(op);
            response.sendRedirect(request.getContextPath() + "/sales/opportunity-detail?id=" + oppId);
        } catch (IllegalArgumentException e) {
            response.sendError(400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Error adding product");
        }
    }
}
