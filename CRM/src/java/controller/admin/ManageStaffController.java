package controller.admin;

import dal.RoleDAO;
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
import java.util.regex.Pattern;
import model.Staff;

/**
 *
 * @author PC
 */
@WebServlet(name = "ManageStaffController", urlPatterns = {"/admin/staff"})
public class ManageStaffController extends HttpServlet {

    private static final int PAGE_SIZE = 8;
    Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            StaffDAO dao = new StaffDAO();
            RoleDAO roleDao = new RoleDAO();

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
                request.setAttribute("departments", roleDao.getAllDepartments());

                request.getRequestDispatcher("/admin/staff-form.jsp")
                        .forward(request, response);
                return;
            }

            // ===== ADD =====
            if ("add".equals(action)) {
                request.setAttribute("mode", "add");
                request.setAttribute("departments", roleDao.getAllDepartments());

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
        RoleDAO roleDao = new RoleDAO();
        HttpSession session = request.getSession();

        String action = request.getParameter("action");

        String idRaw = request.getParameter("id");
        int id = (idRaw != null && !idRaw.isEmpty()) ? Integer.parseInt(idRaw) : 0;
        try {

            // ===== DEACTIVATE =====
            if ("deactivate".equals(action)) {
                dao.updateStatus(id, false);
                session.setAttribute("successMessage", "Đã vô hiệu hóa nhân viên");
            } // ===== ACTIVATE =====
            else if ("activate".equals(action)) {
                dao.updateStatus(id, true);
                session.setAttribute("successMessage", "Đã kích hoạt nhân viên");
            } else if ("edit".equals(action)) {
                boolean result = false;

                String fullName = request.getParameter("fullName");
                String email = request.getParameter("email");
                String password = request.getParameter("password");
                String departmentRaw = request.getParameter("departmentId");

                String isActiveRaw = request.getParameter("isActive");
                boolean isActive = (isActiveRaw != null);

                List<String> errors = new ArrayList<>();

                // ===== VALIDATE =====
                if (fullName == null || fullName.trim().isEmpty()) {
                    errors.add("Tên không được để trống");
                } else if (fullName.length() > 150) {
                    errors.add("Tên tối đa 150 ký tự");
                }

                if (email == null || email.trim().isEmpty()) {
                    errors.add("Email không được để trống");
                } else if (!emailPattern.matcher(email).matches()) {
                    errors.add("Email không hợp lệ");
                } else if (dao.isEmailExistExceptId(email, id)) {
                    errors.add("Email đã tồn tại");
                }

                if (departmentRaw == null || departmentRaw.isEmpty()) {
                    errors.add("Vui lòng chọn phòng ban");
                }

                if (password != null && !password.isEmpty()) {
                    if (password.length() < 6) {
                        errors.add("Mật khẩu phải >= 6 ký tự");
                    }
                    if (password.contains(" ")) {
                        errors.add("Mật khẩu không được chứa khoảng trắng");
                    }
                }

                // ===== CÓ LỖI =====
                if (!errors.isEmpty()) {
                    request.setAttribute("errorMessage", String.join(", ", errors));

                    Staff s = new Staff();
                    s.setId(id);
                    s.setFullName(fullName);
                    s.setEmail(email);
                    s.setRoleId(Integer.parseInt(departmentRaw));
                    s.setActive(isActive);

                    request.setAttribute("staff", s);
                    request.setAttribute("departments", roleDao.getAllDepartments());
                    request.setAttribute("isEdit", true);
                    request.setAttribute("mode", "edit");

                    request.getRequestDispatcher("/admin/staff-form.jsp")
                            .forward(request, response);
                    return;
                }

                // ===== UPDATE =====
                Staff s = new Staff();
                s.setId(id);
                s.setFullName(fullName);
                s.setEmail(email);
                s.setRoleId(Integer.parseInt(departmentRaw));
                s.setActive(isActive);

                if (password != null && !password.isEmpty()) {
                    s.setPassword(password);
                    result = dao.updateWithPassword(s);
                } else {
                    result = dao.updateWithoutPassword(s);
                }

                if (result) {
                    session.setAttribute("successMessage", "Cập nhật thành công!");
                } else {
                    session.setAttribute("errorMessage", "Cập nhật thất bại!");
                }

                response.sendRedirect(request.getContextPath() + "/admin/staff");
                return;
            } else if ("add".equals(action)) {
                try {
                    boolean result = false;

                    String fullName = request.getParameter("fullName");
                    String email = request.getParameter("email");
                    String password = request.getParameter("password");
                    String confirmPassword = request.getParameter("confirmPassword");
                    String departmentRaw = request.getParameter("departmentId");

                    List<String> errors = new ArrayList<>();

                    // ===== VALIDATE =====
                    if (fullName == null || fullName.trim().isEmpty()) {
                        errors.add("Tên không được để trống");
                    } else if (fullName.length() > 150) {
                        errors.add("Tên tối đa 150 ký tự");
                    }

                    if (email == null || email.trim().isEmpty()) {
                        errors.add("Email không được để trống");
                    } else if (!emailPattern.matcher(email).matches()) {
                        errors.add("Email không hợp lệ");
                    } else if (dao.isEmailExist(email)) {
                        errors.add("Email đã tồn tại");
                    }

                    if (password == null || password.isEmpty()) {
                        errors.add("Mật khẩu không được để trống");
                    } else if (password.length() < 6) {
                        errors.add("Mật khẩu phải >= 6 ký tự");
                    } else if (password.contains(" ")) {
                        errors.add("Mật khẩu không được chứa khoảng trắng");
                    }

                    if (!password.equals(confirmPassword)) {
                        errors.add("Mật khẩu xác nhận không khớp");
                    }

                    if (departmentRaw == null || departmentRaw.isEmpty()) {
                        errors.add("Vui lòng chọn phòng ban");
                    }

                    // ===== CÓ LỖI =====
                    if (!errors.isEmpty()) {
                        request.setAttribute("errorMessage", String.join(", ", errors));

                        Staff s = new Staff();
                        s.setFullName(fullName);
                        s.setEmail(email);
                        s.setRoleId(Integer.parseInt(departmentRaw));

                        request.setAttribute("staff", s);
                        request.setAttribute("departments", roleDao.getAllDepartments());
                        request.setAttribute("isEdit", false);
                        request.setAttribute("mode", "add");

                        request.getRequestDispatcher("/admin/staff-form.jsp")
                                .forward(request, response);
                        return;
                    }

                    // ===== INSERT =====
                    Staff s = new Staff();
                    s.setFullName(fullName);
                    s.setEmail(email);
                    s.setPassword(password);
                    s.setRoleId(Integer.parseInt(departmentRaw));
                    s.setActive(true);

                    result = dao.insertStaff(s);

                    if (result) {
                        session.setAttribute("successMessage", "Thêm nhân viên thành công!");
                    } else {
                        session.setAttribute("errorMessage", "Thêm thất bại!");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    session.setAttribute("errorMessage", "Dữ liệu không hợp lệ!");
                }

                response.sendRedirect(request.getContextPath() + "/admin/staff");
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Dữ liệu không hợp lệ!");
            if ("edit".equals(action)) {
                request.setAttribute("mode", "edit");
            } else if ("add".equals(action)) {
                request.setAttribute("mode", "add");
            }
        }

        response.sendRedirect(request.getContextPath() + "/admin/staff");
    }
}
