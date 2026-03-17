package controller.sales;

import dal.QuotationDAO;
import dal.ProductDAO;
import dal.OpportunityDAO;
import model.sales.Quotation;
import model.sales.QuotationItem;
import model.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@WebServlet("/sales/quotation-create")
public class QuotationCreateServlet extends HttpServlet {

    private QuotationDAO quotationDAO;
    private ProductDAO productDAO;
    private OpportunityDAO opportunityDAO;

    @Override
    public void init() throws ServletException {
        quotationDAO   = new QuotationDAO();
        productDAO     = new ProductDAO();
        opportunityDAO = new OpportunityDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
        if (userSession == null || (!userSession.isSaleStaff() && !userSession.isAdmin())) {
            response.sendRedirect(request.getContextPath() + "/login"); return;
        }

        try {
            int oppId = Integer.parseInt(request.getParameter("opportunityId"));
            request.setAttribute("opportunityId", oppId);
            request.setAttribute("opportunity", opportunityDAO.getById(oppId));
            request.setAttribute("products", productDAO.getAll());
            request.setAttribute("mode", "create");
            request.getRequestDispatcher("/sales/quotation-form.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
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
            String oppIdStr = request.getParameter("opportunityId");
            if (oppIdStr == null || oppIdStr.trim().isEmpty()) {
                response.sendError(400, "Missing opportunityId parameter"); return;
            }
            int oppId = SalesInputValidator.parsePositiveInt("Opportunity", oppIdStr);
            if (userSession.getStaff() == null) {
                response.sendError(403, "Không tìm thấy thông tin nhân viên");
                return;
            }

            Quotation q = new Quotation();
            q.setOpportunityId(oppId);
            q.setVersion(1);
            q.setStatus("Draft");
            q.setCreatedBy(userSession.getStaff().getId());

            List<QuotationItem> items = parseItems(request);
            if (items.isEmpty()) {
                throw new IllegalArgumentException("Báo giá phải có ít nhất 1 sản phẩm hợp lệ");
            }
            BigDecimal total = items.stream()
                .map(QuotationItem::getLineTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            q.setTotalAmount(total);

            quotationDAO.insert(q, items);
            response.sendRedirect(request.getContextPath() + "/sales/opportunity-detail?id=" + oppId);
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            try {
                int oppId = SalesInputValidator.parsePositiveInt("Opportunity", request.getParameter("opportunityId"));
                request.setAttribute("opportunityId", oppId);
                request.setAttribute("opportunity", opportunityDAO.getById(oppId));
                request.setAttribute("products", productDAO.getAll());
                request.setAttribute("mode", "create");
                request.getRequestDispatcher("/sales/quotation-form.jsp").forward(request, response);
            } catch (Exception ex) {
                response.sendError(400, e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
    }

    protected List<QuotationItem> parseItems(HttpServletRequest request) {
        List<QuotationItem> items = new ArrayList<>();
        String[] productIds = request.getParameterValues("productId");
        if (productIds == null) return items;

        String[] productNames = request.getParameterValues("productName");
        String[] quantities   = request.getParameterValues("quantity");
        String[] unitPrices   = request.getParameterValues("unitPrice");
        String[] discounts    = request.getParameterValues("discount");
        String[] taxRates     = request.getParameterValues("taxRate");

        for (int i = 0; i < productIds.length; i++) {
            if (productIds[i] == null || productIds[i].isEmpty()) continue;
            try {
                QuotationItem item = new QuotationItem();
                item.setProductId(SalesInputValidator.parsePositiveInt("Sản phẩm", productIds[i]));
                item.setProductName(productNames != null && i < productNames.length ? productNames[i] : "");
                int qty = quantities != null && i < quantities.length && !quantities[i].isEmpty()
                        ? SalesInputValidator.parsePositiveInt("Số lượng", quantities[i]) : 1;
                item.setQuantity(qty);
                BigDecimal price = unitPrices != null && i < unitPrices.length && !unitPrices[i].isEmpty() ?
                    SalesInputValidator.parseNonNegativeDecimal("Đơn giá", unitPrices[i], BigDecimal.ZERO) : BigDecimal.ZERO;
                item.setUnitPrice(price);
                BigDecimal disc = discounts != null && i < discounts.length && !discounts[i].isEmpty() ?
                    SalesInputValidator.parseNonNegativeDecimal("Giảm giá", discounts[i], BigDecimal.ZERO) : BigDecimal.ZERO;
                if (disc.compareTo(new BigDecimal("100")) > 0) {
                    throw new IllegalArgumentException("Giảm giá không được vượt quá 100%");
                }
                item.setDiscount(disc);
                BigDecimal tax = taxRates != null && i < taxRates.length && !taxRates[i].isEmpty() ?
                    SalesInputValidator.parseNonNegativeDecimal("Thuế", taxRates[i], BigDecimal.ZERO) : BigDecimal.ZERO;
                if (tax.compareTo(new BigDecimal("100")) > 0) {
                    throw new IllegalArgumentException("Thuế không được vượt quá 100%");
                }
                item.setTaxRate(tax);
                // lineTotal = qty * price * (1 - disc/100) * (1 + tax/100)
                BigDecimal lineTotal = price.multiply(new BigDecimal(qty))
                    .multiply(BigDecimal.ONE.subtract(disc.divide(new BigDecimal(100))))
                    .multiply(BigDecimal.ONE.add(tax.divide(new BigDecimal(100))));
                item.setLineTotal(lineTotal.setScale(2, BigDecimal.ROUND_HALF_UP));
                items.add(item);
            } catch (IllegalArgumentException e) {
                throw e;
            } catch (Exception e) {
                throw new IllegalArgumentException("Dòng sản phẩm không hợp lệ");
            }
        }
        return items;
    }
}
