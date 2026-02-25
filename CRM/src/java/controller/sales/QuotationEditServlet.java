package controller.sales;

import dal.QuotationDAO;
import dal.ProductDAO;
import model.sales.Quotation;
import model.sales.QuotationItem;
import model.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@WebServlet("/sales/quotation-edit")
public class QuotationEditServlet extends HttpServlet {

    private QuotationDAO quotationDAO;
    private ProductDAO   productDAO;

    @Override
    public void init() throws ServletException {
        quotationDAO = new QuotationDAO();
        productDAO   = new ProductDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
        if (userSession == null || !userSession.isStaff()) {
            response.sendRedirect(request.getContextPath() + "/login"); return;
        }

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Quotation q = quotationDAO.getById(id);
            if (q == null) { response.sendError(404); return; }
            if (!"Draft".equals(q.getStatus())) {
                response.sendRedirect(request.getContextPath() + "/sales/quotation-detail?id=" + id + "&error=notDraft");
                return;
            }
            request.setAttribute("quotation", q);
            request.setAttribute("items", quotationDAO.getItemsByQuotationId(id));
            request.setAttribute("products", productDAO.getAll());
            request.setAttribute("mode", "edit");
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
        if (userSession == null || !userSession.isStaff()) {
            response.sendRedirect(request.getContextPath() + "/login"); return;
        }

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Quotation q = quotationDAO.getById(id);
            if (q == null) { response.sendError(404); return; }

            String validUntil = request.getParameter("validUntil");
            if (validUntil != null && !validUntil.isEmpty()) {
                q.setValidUntil(new SimpleDateFormat("yyyy-MM-dd").parse(validUntil));
            }
            q.setNotes(request.getParameter("notes"));

            List<QuotationItem> items = new QuotationCreateServlet().parseItems(request);
            BigDecimal total = items.stream()
                .map(QuotationItem::getLineTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            q.setTotalAmount(total);

            quotationDAO.update(q, items);
            response.sendRedirect(request.getContextPath() + "/sales/quotation-detail?id=" + id);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
    }
}
