package controller.activity;

import com.google.gson.Gson;
import dal.ActivityDAO;
import model.UserSession;
import model.activity.Activity;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ActivityCalendarApiController", urlPatterns = {"/api/activities/calendar"})
public class ActivityCalendarApiController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        HttpSession session = request.getSession();
        UserSession userSession = (UserSession) session.getAttribute("userSession");

        if (userSession == null || userSession.getStaff() == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"error\": \"Unauthorized\"}");
            out.flush();
            return;
        }

        Integer filterUserId = null;
        if (!userSession.isAdmin()) {
            filterUserId = userSession.getStaff().getId();
        }

        ActivityDAO dao = new ActivityDAO();
        List<Activity> activities = dao.getActivitiesForDashboard(filterUserId);

        StringBuilder json = new StringBuilder();
        json.append("[");
        
        boolean first = true;
        for (Activity act : activities) {
            if (act.getDueDate() == null) {
                continue;
            }

            if (!first) {
                json.append(",");
            }
            first = false;

            String title = act.getTitle() != null ? act.getTitle().replace("\"", "\\\"").replace("\n", " ").replace("\r", "") : "";
            String start = act.getDueDate().toString().split("\\.")[0].replace(" ", "T");
            
            String type = act.getType() != null ? act.getType() : "";
            String status = act.getStatus() != null ? act.getStatus() : "";

            String color = "#3B82F6";
            if ("In Progress".equals(status)) {
                color = "#F59E0B";
            } else if ("Completed".equals(status)) {
                color = "#10B981";
            } else if ("Overdue".equals(status)) {
                color = "#EF4444";
            }

            String icon = "";
            if ("Call".equals(type)) icon = "<i class=\\\"fas fa-phone-alt me-1\\\"></i>";
            else if ("Email".equals(type)) icon = "<i class=\\\"fas fa-envelope me-1\\\"></i>";
            else if ("Meeting".equals(type)) icon = "<i class=\\\"fas fa-users me-1\\\"></i>";
            else if ("Task".equals(type)) icon = "<i class=\\\"fas fa-tasks me-1\\\"></i>";
            else if ("Note".equals(type)) icon = "<i class=\\\"fas fa-sticky-note me-1\\\"></i>";

            String formattedTitle = icon + " " + title;

            json.append("{");
            json.append("\"id\":\"").append(act.getId()).append("\",");
            json.append("\"title\":\"").append(formattedTitle).append("\",");
            json.append("\"start\":\"").append(start).append("\",");
            json.append("\"backgroundColor\":\"").append(color).append("\",");
            json.append("\"className\":\"fc-event-custom\"");
            json.append("}");
        }

        json.append("]");

        out.print(json.toString());
        out.flush();
    }
}
