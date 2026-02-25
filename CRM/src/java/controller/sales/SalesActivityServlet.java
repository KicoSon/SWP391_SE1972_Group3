package controller.sales;

import dal.ActivityDAO;
import dal.OpportunityDAO;
import model.activity.Activity;
import model.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.text.SimpleDateFormat;

@WebServlet("/sales/activity-create")
public class SalesActivityServlet extends HttpServlet {

    private ActivityDAO    activityDAO;
    private OpportunityDAO opportunityDAO;

    @Override
    public void init() throws ServletException {
        activityDAO    = new ActivityDAO();
        opportunityDAO = new OpportunityDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
        if (userSession == null || !userSession.isSaleStaff()) {
            response.sendRedirect(request.getContextPath() + "/login"); return;
        }

        try {
            int oppId = Integer.parseInt(request.getParameter("opportunityId"));
            request.setAttribute("opportunity", opportunityDAO.getById(oppId));
//            request.setAttribute("activities", activityDAO.getActivitiesByOpportunityId(oppId));
            request.getRequestDispatcher("/sales/sales-activity-form.jsp").forward(request, response);
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
        if (userSession == null || !userSession.isSaleStaff()) {
            response.sendRedirect(request.getContextPath() + "/login"); return;
        }

        try {
            int oppId = Integer.parseInt(request.getParameter("opportunityId"));

            Activity activity = new Activity();
            activity.setTitle(request.getParameter("title"));
            activity.setType(request.getParameter("type"));
            activity.setDescription(request.getParameter("description"));
            activity.setCreatedBy(userSession.getStaff().getId());
            activity.setOpportunityId(oppId);
            activity.setStatus("Planned");
            activity.setPriority(request.getParameter("priority") != null ? request.getParameter("priority") : "Medium");

            String dueDateStr = request.getParameter("dueDate");
            if (dueDateStr != null && !dueDateStr.isEmpty()) {
                java.util.Date parsed = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(dueDateStr);
                activity.setDueDate(new java.sql.Timestamp(parsed.getTime()));
            }

//            activityDAO.insertActivity(activity); // calls overloaded version with no participants
            response.sendRedirect(request.getContextPath() + "/sales/opportunity-detail?id=" + oppId);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
    }
}
