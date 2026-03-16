package controller.sales;

import dal.QuotationDAO;
import dal.OpportunityDAO;
import dal.SalesOrderDAO;
import model.sales.Quotation;
import model.sales.SalesOrder;
import model.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.text.SimpleDateFormat;

@WebServlet("/sales/convert-to-order")
public class ConvertQuotationToOrderServlet extends HttpServlet {

    private QuotationDAO    quotationDAO;
    private SalesOrderDAO   salesOrderDAO;
    private OpportunityDAO  opportunityDAO;

    @Override
    public void init() throws ServletException {
        quotationDAO   = new QuotationDAO();
        salesOrderDAO  = new SalesOrderDAO();
        opportunityDAO = new OpportunityDAO();
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
            order.setOpportunityId(q.getOpportunityId());
            order.setOrderCode(salesOrderDAO.generateOrderCode());
            order.setStatus("Confirmed");
            order.setTotalAmount(q.getTotalAmount());
            order.setShippingAddress(request.getParameter("shippingAddress"));
            order.setPaymentMethod(request.getParameter("paymentMethod"));
            order.setPaymentStatus("Pending");
            order.setCreatedBy(userSession.getStaff().getId());
            String deliveryStr = request.getParameter("deliveryDate");
            if (deliveryStr != null && !deliveryStr.isEmpty()) {
                order.setDeliveryDate(new SimpleDateFormat("yyyy-MM-dd").parse(deliveryStr));
            }

            boolean inserted = salesOrderDAO.insert(order);
            if (inserted) {
                // Close opportunity as Won
                opportunityDAO.closeOpportunity(q.getOpportunityId(), "Won", null);
                // Update quotation status to Sent if not already
                quotationDAO.updateStatus(quotationId, "Sent", null);

                SalesOrder saved = salesOrderDAO.getByQuotationId(quotationId);
                if (saved != null) {
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
