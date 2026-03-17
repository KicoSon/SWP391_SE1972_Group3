package controller.admin;

import dal.StaffDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Staff;

/**
 *
 * @author PC
 */
@WebServlet(name = "ManageStaffController", urlPatterns = {"/admin/staff"})
public class ManageStaffController extends HttpServlet {

    private static final int PAGE_SIZE = 8;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            StaffDAO dao = new StaffDAO();

            String action = request.getParameter("action");

            // ===== VIEW =====
            if ("view".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Staff staff = dao.getStaffById(id);

                request.setAttribute("staff", staff);
                request.getRequestDispatcher("/admin/staff-details.jsp")
                        .forward(request, response);
                return;
            }

            // ===== EDIT =====
            if ("edit".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Staff staff = dao.getStaffById(id);

                request.setAttribute("staff", staff);
                request.setAttribute("mode", "edit");

                request.getRequestDispatcher("/admin/staff-form.jsp")
                        .forward(request, response);
                return;
            }

            // ===== ADD =====
            if ("add".equals(action)) {
                request.setAttribute("mode", "add");
                request.getRequestDispatcher("/admin/staff-form.jsp")
                        .forward(request, response);
                return;
            }

            // ===== LIST =====
            List<Staff> allStaff = dao.getAllStaff();
            if (allStaff == null) {
                allStaff = new ArrayList<>();
            }

            // ===== STATISTICS =====
            int totalStaff = allStaff.size();
            int activeStaff = 0;

            for (Staff s : allStaff) {
                if (s.isActive()) {
                    activeStaff++;
                }
            }

            int inactiveStaff = totalStaff - activeStaff;

            request.setAttribute("totalStaff", totalStaff);
            request.setAttribute("activeStaff", activeStaff);
            request.setAttribute("inactiveStaff", inactiveStaff);

            // ===== PERMISSION (demo) =====
            request.setAttribute("canCreate", true);
            request.setAttribute("canEdit", true);
            request.setAttribute("canDelete", true);

            // ===== SEARCH =====
            String search = request.getParameter("search");
            List<Staff> filtered = new ArrayList<>();

            for (Staff s : allStaff) {
                boolean match = true;

                if (search != null && !search.trim().isEmpty()) {
                    String key = search.toLowerCase();

                    if (!(s.getFullName().toLowerCase().contains(key)
                            || s.getEmail().toLowerCase().contains(key)
                            || (s.getDepartment() != null
                            && s.getDepartment().toLowerCase().contains(key)))) {
                        match = false;
                    }
                }

                if (match) {
                    filtered.add(s);
                }
            }

            // ===== PAGINATION =====
            int total = filtered.size();
            int totalPages = (int) Math.ceil((double) total / PAGE_SIZE);

            int page = 1;
            try {
                page = Integer.parseInt(request.getParameter("page"));
            } catch (Exception e) {
            }

            if (page < 1) {
                page = 1;
            }
            if (page > totalPages && totalPages > 0) {
                page = totalPages;
            }

            int start = (page - 1) * PAGE_SIZE;
            int end = Math.min(start + PAGE_SIZE, total);

            List<Staff> pageList = new ArrayList<>();
            if (start < end) {
                pageList = filtered.subList(start, end);
            }

            request.setAttribute("staffList", pageList);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);

            request.getRequestDispatcher("/admin/staff.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("ERROR: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        StaffDAO dao = new StaffDAO();
        HttpSession session = request.getSession();

        String action = request.getParameter("action");

        try {

            int id = Integer.parseInt(request.getParameter("id"));

            // ===== DEACTIVATE =====
            if ("deactivate".equals(action)) {
                dao.updateStatus(id, false);
                session.setAttribute("successMessage", "Đã vô hiệu hóa nhân viên");
            } // ===== ACTIVATE =====
            else if ("activate".equals(action)) {
                dao.updateStatus(id, true);
                session.setAttribute("successMessage", "Đã kích hoạt nhân viên");
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Lỗi xử lý!");
        }

        response.sendRedirect(request.getContextPath() + "/admin/staff");
    }
}
