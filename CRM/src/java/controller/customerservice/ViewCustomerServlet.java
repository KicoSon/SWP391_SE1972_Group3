/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.customerservice;

import dal.CustomerDAO;
import model.Customer;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet("/customerservice/viewCustomer")
public class ViewCustomerServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idRaw = request.getParameter("id");

        if (idRaw == null) {
            response.sendRedirect("customerlist");
            return;
        }

        try {
            int id = Integer.parseInt(idRaw);

            CustomerDAO dao = new CustomerDAO();
            Customer customer = dao.getCustomerById(id);

            if (customer == null) {
                response.sendRedirect("customerlist");
                return;
            }

            request.setAttribute("customer", customer);
            request.getRequestDispatcher("/customerservice/customerdetail.jsp")
                    .forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect("customerlist");
        }
    }
}