package controller.sales;

import dal.QuotationDAO;
import dal.OpportunityDAO;
import dal.SalesOrderDAO;
import dal.SalesOrderItemDAO;
import model.sales.Quotation;
import model.sales.QuotationItem;
import model.sales.SalesOrder;
import model.sales.SalesOrderItem;
import model.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet("/sales/convert-to-order")
public class ConvertQuotationToOrderServlet extends HttpServlet {

    private QuotationDAO    quotationDAO;
    private SalesOrderDAO   salesOrderDAO;
    private OpportunityDAO  opportunityDAO;
    private SalesOrderItemDAO salesOrderItemDAO;

    @Override
    public void init() throws ServletException {
        quotationDAO   = new QuotationDAO();
        salesOrderDAO  = new SalesOrderDAO();
        opportunityDAO = new OpportunityDAO();
        salesOrderItemDAO = new SalesOrderItemDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
        if (userSession == null || (!userSession.isSaleStaff() && !userSession.isAdmin())) {
            response.sendRedirect(request.getContextPath() + "/login"); return;
        }

        try {
            int quotationId = Integer.parseInt(request.getParameter("quotationId"));
            Quotation q = quotationDAO.getById(quotationId);
            if (q == null || !"Approved".equals(q.getStatus())) {
                response.sendError(400, "Quotation must be Approved to convert");
                return;
            }

            SalesOrder order = new SalesOrder();
            order.setQuotationId(quotationId);
            order.setOrderCode(salesOrderDAO.generateOrderCode());
            order.setStatus("Pending");
            order.setTotalAmount(q.getTotalAmount());

            boolean inserted = salesOrderDAO.insert(order);
            if (inserted) {
                // Close opportunity as Won
                opportunityDAO.closeOpportunity(q.getOpportunityId(), "Won", null);
                // Update quotation status to Sent if not already
                quotationDAO.updateStatus(quotationId, "Sent", null);

                SalesOrder saved = salesOrderDAO.getByQuotationId(quotationId);
                if (saved != null) {
                    // Clone Items
                    List<QuotationItem> items = quotationDAO.getItemsByQuotationId(quotationId);
                    for (QuotationItem qi : items) {
                        SalesOrderItem oi = new SalesOrderItem();
                        oi.setOrderId(saved.getId());
                        oi.setProductName(qi.getProductName());
                        oi.setQuantity(qi.getQuantity());
                        oi.setUnitPrice(qi.getUnitPrice());
                        oi.setTotalPrice(qi.getLineTotal());
                        salesOrderItemDAO.insert(oi);
                    }

                    salesOrderDAO.syncToCustomerCore(saved.getId());
                    response.sendRedirect(request.getContextPath() + "/sales/order-detail?id=" + saved.getId());
                    return;
                }
            }
            response.sendRedirect(request.getContextPath() + "/sales/opportunity-detail?id=" + q.getOpportunityId());
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
    }
}
