package controller.sales;

import dal.ProductDAO;
import model.sales.Product;
import model.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/sales/products")
public class ProductServlet extends HttpServlet {

    private ProductDAO productDAO;

    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
        if (userSession == null || (!userSession.isSaleStaff() && !userSession.isAdmin())) {
            response.sendError(401, "Unauthorized"); return;
        }

        String format = request.getParameter("format");
        String keyword = request.getParameter("q");
        String category = request.getParameter("category");

        try {
            List<Product> products;
            if (keyword != null && !keyword.isEmpty()) {
                products = productDAO.search(keyword);
            } else if (category != null && !category.isEmpty()) {
                products = productDAO.getByCategory(category);
            } else {
                products = productDAO.getAll();
            }

            if ("json".equals(format)) {
                // Return JSON for AJAX calls in quotation form
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.print("[");
                for (int i = 0; i < products.size(); i++) {
                    Product p = products.get(i);
                    if (i > 0) out.print(",");
                    out.printf("{\"id\":%d,\"name\":\"%s\",\"sku\":\"%s\",\"basePrice\":%.2f,\"category\":\"%s\"}",
                        p.getId(),
                        escapeJson(p.getName()),
                        escapeJson(p.getSku()),
                        p.getBasePrice().doubleValue(),
                        escapeJson(p.getCategory())
                    );
                }
                out.print("]");
            } else {
                request.setAttribute("products", products);
                request.getRequestDispatcher("/sales/product-list.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
