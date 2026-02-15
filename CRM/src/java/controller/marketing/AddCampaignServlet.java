package controller.marketing;

import dal.CampaignDAO;
import model.Campaign;
import model.UserSession;

import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 50
)

@WebServlet("/marketingg/addCampaign")
public class AddCampaignServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "images";

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
                "/marketingg/addCampaign.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            request.setCharacterEncoding("UTF-8");

            String name = request.getParameter("name");

            String description =
                    request.getParameter("description");

            String start =
                    request.getParameter("startDate");

            String end =
                    request.getParameter("endDate");

            String status =
                    request.getParameter("status");

            SimpleDateFormat sdf =
                    new SimpleDateFormat("yyyy-MM-dd");

            Date today =
                    sdf.parse(sdf.format(new Date()));

            Date startDate = null;

            Date endDate = null;


            CampaignDAO dao =
                    new CampaignDAO();


            // =========================
            // VALIDATE NAME
            // =========================

            if (name == null || name.trim().isEmpty()) {

                request.setAttribute("error",
                        "Tên campaign không được để trống");

                request.getRequestDispatcher(
                        "/marketingg/addCampaign.jsp")
                        .forward(request, response);

                return;
            }


            // =========================
            // VALIDATE TRÙNG TÊN
            // =========================

            if (dao.isCampaignNameExist(name)) {

                request.setAttribute("error",
                        "Tên campaign đã tồn tại");

                request.getRequestDispatcher(
                        "/marketingg/addCampaign.jsp")
                        .forward(request, response);

                return;
            }


            // =========================
            // VALIDATE START DATE
            // =========================

            if (start == null || start.isEmpty()) {

                request.setAttribute("error",
                        "Ngày bắt đầu không được để trống");

                request.getRequestDispatcher(
                        "/marketingg/addCampaign.jsp")
                        .forward(request, response);

                return;
            }

            startDate = sdf.parse(start);


            if (startDate.before(today)) {

                request.setAttribute("error",
                        "Ngày bắt đầu không được nhỏ hơn hôm nay");

                request.getRequestDispatcher(
                        "/marketingg/addCampaign.jsp")
                        .forward(request, response);

                return;
            }



            // =========================
            // VALIDATE END DATE
            // =========================

            if (end != null && !end.isEmpty()) {

                endDate = sdf.parse(end);

                if (endDate.before(startDate)) {

                    request.setAttribute("error",
                            "Ngày kết thúc phải sau ngày bắt đầu");

                    request.getRequestDispatcher(
                            "/marketingg/addCampaign.jsp")
                            .forward(request, response);

                    return;
                }

            }



            // =========================
            // HANDLE FILE
            // =========================

            Part filePart =
                    request.getPart("banner");

            String fileName =
                    System.currentTimeMillis()
                    + "_"
                    + filePart.getSubmittedFileName();

            String uploadPath =
                    getServletContext().getRealPath("")
                    + UPLOAD_DIR;

            java.io.File uploadDir =
                    new java.io.File(uploadPath);

            if (!uploadDir.exists())
                uploadDir.mkdir();


            filePart.write(uploadPath
                    + java.io.File.separator
                    + fileName);


            String bannerUrl =
                    "/images/" + fileName;



            // =========================
            // GET STAFF
            // =========================

            HttpSession session =
                    request.getSession(false);

            UserSession userSession =
                    (UserSession)
                            session.getAttribute(
                                    "userSession");

            long staffId =
                    userSession.getUserId();



            // =========================
            // SAVE
            // =========================

            Campaign c =
                    new Campaign();

            c.setName(name);

            c.setDescription(description);

            c.setBannerUrl(bannerUrl);

            c.setStartDate(startDate);

            c.setEndDate(endDate);

            c.setStatus(status);

            c.setCreatedBy(staffId);


            dao.insertCampaign(c);



            // =========================
            // SUCCESS
            // =========================

            response.sendRedirect(
                    request.getContextPath()
                    + "/marketing/campaignmanagement");



        } catch (Exception e) {

            e.printStackTrace();

            request.setAttribute("error",
                    "Lỗi hệ thống");

            request.getRequestDispatcher(
                    "/marketingg/addCampaign.jsp")
                    .forward(request, response);

        }

    }

}
