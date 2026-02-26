package controller.marketing;

import dal.CampaignDAO;
import model.Campaign;

import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@MultipartConfig

@WebServlet("/marketingg/editCampaign")

public class EditCampaignServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "images";

    @Override

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        long id
                = Long.parseLong(request.getParameter("id"));

        CampaignDAO dao
                = new CampaignDAO();

        Campaign c
                = dao.getCampaignById(id);

        request.setAttribute("campaign", c);

        request.getRequestDispatcher(
                "/marketingg/editCampaign.jsp")
                .forward(request, response);

    }

    @Override

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            request.setCharacterEncoding("UTF-8");

            long id
                    = Long.parseLong(request.getParameter("id"));

            String name
                    = request.getParameter("name");

            String description
                    = request.getParameter("description");

            String status
                    = request.getParameter("status");

            String start
                    = request.getParameter("startDate");

            String end
                    = request.getParameter("endDate");

            String oldBanner
                    = request.getParameter("oldBanner");

            SimpleDateFormat sdf
                    = new SimpleDateFormat("yyyy-MM-dd");

            Date today
                    = sdf.parse(sdf.format(new Date()));

            Date startDate
                    = sdf.parse(start);

            Date endDate = null;

            CampaignDAO dao
                    = new CampaignDAO();

            Campaign old
                    = dao.getCampaignById(id);

            /* VALIDATE NAME */
            if (name == null || name.trim().isEmpty()) {

                request.setAttribute("error",
                        "Tên không được để trống");

                request.setAttribute("campaign", old);

                request.getRequestDispatcher(
                        "/marketingg/editCampaign.jsp")
                        .forward(request, response);

                return;

            }

            /* VALIDATE TRÙNG */
            if (!name.equalsIgnoreCase(old.getName())
                    && dao.isCampaignNameExist(name)) {

                request.setAttribute("error",
                        "Tên đã tồn tại");

                request.setAttribute("campaign", old);

                request.getRequestDispatcher(
                        "/marketingg/editCampaign.jsp")
                        .forward(request, response);

                return;

            }

            /* VALIDATE START */
            if (startDate.before(today)) {

                request.setAttribute("error",
                        "Start phải >= hôm nay");

                request.setAttribute("campaign", old);

                request.getRequestDispatcher(
                        "/marketingg/editCampaign.jsp")
                        .forward(request, response);

                return;

            }

            /* VALIDATE END */
            if (end != null && !end.isEmpty()) {

                endDate = sdf.parse(end);

                if (endDate.before(startDate)) {

                    request.setAttribute("error",
                            "End phải sau Start");

                    request.setAttribute("campaign", old);

                    request.getRequestDispatcher(
                            "/marketingg/editCampaign.jsp")
                            .forward(request, response);

                    return;

                }

            }

            /* UPLOAD FILE */
            Part filePart
                    = request.getPart("banner");

            String bannerUrl
                    = oldBanner;

            if (filePart != null
                    && filePart.getSize() > 0) {

                String fileName
                        = System.currentTimeMillis()
                        + "_"
                        + filePart.getSubmittedFileName();

                String uploadPath
                        = getServletContext()
                                .getRealPath("")
                        + UPLOAD_DIR;

                java.io.File uploadDir
                        = new java.io.File(uploadPath);

                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }

                filePart.write(uploadPath
                        + java.io.File.separator
                        + fileName);

                bannerUrl
                        = "/images/" + fileName;

            }

            /* UPDATE */
            Campaign c
                    = new Campaign();

            c.setId(id);

            c.setName(name);

            c.setDescription(description);

            c.setBannerUrl(bannerUrl);

            c.setStartDate(startDate);

            c.setEndDate(endDate);

            c.setStatus(status);

            dao.updateCampaign(c);

            HttpSession session = request.getSession();

            session.setAttribute("success",
                    "Cập nhật Campaign thành công");

            response.sendRedirect(
                    request.getContextPath()
                    + "/marketing/campaignmanagement");

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

}
