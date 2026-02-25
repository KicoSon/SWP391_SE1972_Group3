package controller.sales;

import dal.PipelineDAO;
import model.sales.Pipeline;
import model.sales.PipelineStage;
import model.sales.LostReason;
import model.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/sales/pipeline-config")
public class PipelineConfigServlet extends HttpServlet {

    private PipelineDAO pipelineDAO;

    @Override
    public void init() throws ServletException {
        pipelineDAO = new PipelineDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
        if (userSession == null || !userSession.isStaff()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!userSession.isAdmin() && !userSession.hasRole("SALES_MANAGER")) {
            response.sendError(403, "Only Manager can configure pipeline");
            return;
        }

        try {
            List<Pipeline> pipelines = pipelineDAO.getAll();
            int pipelineId = 1;
            String plParam = request.getParameter("pipelineId");
            if (plParam != null && !plParam.isEmpty()) pipelineId = Integer.parseInt(plParam);

            request.setAttribute("pipelines", pipelines);
            request.setAttribute("stages", pipelineDAO.getStagesByPipelineId(pipelineId));
            request.setAttribute("currentPipelineId", pipelineId);
            request.setAttribute("lostReasons", pipelineDAO.getAllLostReasons());
            request.getRequestDispatcher("/sales/pipeline-config.jsp").forward(request, response);
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
        if (!userSession.isAdmin() && !userSession.hasRole("SALES_MANAGER")) {
            response.sendError(403); return;
        }

        String action = request.getParameter("action");
        int pipelineId = Integer.parseInt(request.getParameter("pipelineId"));

        try {
            if ("addStage".equals(action)) {
                PipelineStage s = new PipelineStage();
                s.setPipelineId(pipelineId);
                s.setStageName(request.getParameter("stageName"));
                s.setColor(request.getParameter("color"));
                s.setWon("on".equals(request.getParameter("isWon")));
                s.setLost("on".equals(request.getParameter("isLost")));
                List<PipelineStage> existing = pipelineDAO.getStagesByPipelineId(pipelineId);
                s.setOrderIndex(existing.size() + 1);
                pipelineDAO.insertStage(s);
            } else if ("deleteStage".equals(action)) {
                pipelineDAO.deleteStage(Integer.parseInt(request.getParameter("stageId")));
            } else if ("addLostReason".equals(action)) {
                LostReason lr = new LostReason();
                lr.setReason(request.getParameter("reason"));
                pipelineDAO.insertLostReason(lr);
            } else if ("toggleLostReason".equals(action)) {
                int lrId = Integer.parseInt(request.getParameter("lrId"));
                boolean active = "true".equals(request.getParameter("active"));
                pipelineDAO.toggleLostReason(lrId, active);
            }
            response.sendRedirect(request.getContextPath() + "/sales/pipeline-config?pipelineId=" + pipelineId);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
    }
}
