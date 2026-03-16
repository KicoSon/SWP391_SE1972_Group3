package controller.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "FileDownloadController", urlPatterns = {"/download"})
public class FileDownloadController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String filePath = request.getParameter("file");

        if (filePath == null || filePath.isEmpty()) {
            response.getWriter().print("Không tìm thấy file!");
            return;
        }

        String uploadDir = getServletContext().getInitParameter("uploadDirectory");
        if (uploadDir == null || uploadDir.isEmpty()) {
            uploadDir = "D:/uploads";
        }

        String fileName = filePath;
        if (filePath.contains("/")) {
            fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        }

        File downloadFile = new File(uploadDir + File.separator + fileName);

        if (!downloadFile.exists()) {
            response.getWriter().print("File không tồn tại: " + downloadFile.getAbsolutePath());
            return;
        }

        try (FileInputStream inStream = new FileInputStream(downloadFile); OutputStream outStream = response.getOutputStream()) {
            String mimeType = getServletContext().getMimeType(downloadFile.getAbsolutePath());
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
            response.setContentType(mimeType);
            response.setContentLength((int) downloadFile.length());

            String originalName = fileName.contains("_") ? fileName.substring(fileName.indexOf("_") + 1) : fileName;

            String disposition = "attachment";
            String mode = request.getParameter("mode");

            if (mimeType.startsWith("image/") && !"download".equals(mode)) {
                disposition = "inline";
            }

            String headerKey = "Content-Disposition";
            String headerValue = String.format("%s; filename=\"%s\"", disposition, originalName);
            response.setHeader(headerKey, headerValue);

            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print("Lỗi khi tải file: " + e.getMessage());
        }
    }
}
