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
        if (userSession == null || (!userSession.isSaleStaff() && !userSession.isAdmin())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!userSession.isAdmin()) {
            response.sendError(403, "Only Manager can configure pipeline");
            return;
        }

        try {
            List<Pipeline> pipelines = pipelineDAO.getAll();
            int pipelineId = 1;
            String plParam = request.getParameter("pipelineId");
            if (plParam != null && !plParam.isEmpty()) {
                pipelineId = SalesInputValidator.parsePositiveInt("Pipeline", plParam);
            }

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
        if (userSession == null || (!userSession.isSaleStaff() && !userSession.isAdmin())) {
            response.sendRedirect(request.getContextPath() + "/login"); return;
        }
        if (!userSession.isAdmin()) {
            response.sendError(403); return;
        }

        String action = SalesInputValidator.normalize(request.getParameter("action"));
        String pipelineIdRaw = request.getParameter("pipelineId");
        int pipelineId = SalesInputValidator.parsePositiveIntOrDefault("Pipeline", pipelineIdRaw, 1);

        try {
            if ("addStage".equals(action)) {
                PipelineStage s = new PipelineStage();
                s.setPipelineId(pipelineId);
                s.setStageName(SalesInputValidator.requireText("Tên stage", request.getParameter("stageName"), 2, 100));
                s.setColor(SalesInputValidator.parseRequiredColor(request.getParameter("color")));
                s.setWon(SalesInputValidator.parseBoolean(request.getParameter("isWon")));
                s.setLost(SalesInputValidator.parseBoolean(request.getParameter("isLost")));
                if (s.isWon() && s.isLost()) {
                    throw new IllegalArgumentException("Stage không thể vừa Won vừa Lost");
                }
                List<PipelineStage> existing = pipelineDAO.getStagesByPipelineId(pipelineId);
                s.setOrderIndex(existing.size() + 1);
                pipelineDAO.insertStage(s);
            } else if ("updateStage".equals(action)) {
                PipelineStage s = new PipelineStage();
                s.setId(SalesInputValidator.parsePositiveInt("Stage", request.getParameter("stageId")));
                s.setPipelineId(pipelineId);
                s.setStageName(SalesInputValidator.requireText("Tên stage", request.getParameter("stageName"), 2, 100));
                s.setColor(SalesInputValidator.parseRequiredColor(request.getParameter("color")));
                s.setOrderIndex(SalesInputValidator.parsePositiveIntOrDefault("Thứ tự", request.getParameter("orderIndex"), 1));
                s.setWon(SalesInputValidator.parseBoolean(request.getParameter("isWon")));
                s.setLost(SalesInputValidator.parseBoolean(request.getParameter("isLost")));
                if (s.isWon() && s.isLost()) {
                    throw new IllegalArgumentException("Stage không thể vừa Won vừa Lost");
                }
                pipelineDAO.updateStage(s);
            } else if ("deleteStage".equals(action)) {
                pipelineDAO.deleteStage(SalesInputValidator.parsePositiveInt("Stage", request.getParameter("stageId")));
            } else if ("addLostReason".equals(action)) {
                LostReason lr = new LostReason();
                lr.setReason(SalesInputValidator.requireText("Lý do thất bại", request.getParameter("reason"), 3, 500));
                pipelineDAO.insertLostReason(lr);
            } else if ("toggleLostReason".equals(action)) {
                String idParam = request.getParameter("lrId");
                if (idParam == null || idParam.isEmpty()) {
                    idParam = request.getParameter("lostReasonId");
                }
                int lrId = SalesInputValidator.parsePositiveInt("Lý do thất bại", idParam);
                boolean active = SalesInputValidator.parseBoolean(request.getParameter("active"));
                pipelineDAO.toggleLostReason(lrId, active);
            } else {
                throw new IllegalArgumentException("Thao tác không hợp lệ");
            }
            response.sendRedirect(request.getContextPath() + "/sales/pipeline-config?pipelineId=" + pipelineId);
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMsg", e.getMessage());
            try {
                request.setAttribute("pipelines", pipelineDAO.getAll());
                request.setAttribute("stages", pipelineDAO.getStagesByPipelineId(pipelineId));
                request.setAttribute("currentPipelineId", pipelineId);
                request.setAttribute("lostReasons", pipelineDAO.getAllLostReasons());
                request.getRequestDispatcher("/sales/pipeline-config.jsp").forward(request, response);
            } catch (Exception ex) {
                response.sendError(400, e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
    }
}
