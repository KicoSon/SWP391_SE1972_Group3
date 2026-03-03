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
        
        // Nhận tên file từ URL (Ví dụ: /download?file=uploads/170810293_BaoGia.pdf)
        String filePath = request.getParameter("file");
        
        if (filePath == null || filePath.isEmpty()) {
            response.getWriter().print("Không tìm thấy file!");
            return;
        }

        // Lấy upload directory từ context parameter
        String uploadDir = getServletContext().getInitParameter("uploadDirectory");
        if (uploadDir == null || uploadDir.isEmpty()) {
            uploadDir = "D:/uploads"; // Default fallback
        }

        // Xử lý path - trích tên file từ filePath (uploads/timestamp_filename.pdf -> timestamp_filename.pdf)
        String fileName = filePath;
        if (filePath.contains("/")) {
            fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        }

        // Tìm file trên ổ cứng
        File downloadFile = new File(uploadDir + File.separator + fileName);
        
        if (!downloadFile.exists()) {
            response.getWriter().print("File không tồn tại: " + downloadFile.getAbsolutePath());
            return;
        }

        // Mở luồng đọc file từ ổ đĩa
        try (FileInputStream inStream = new FileInputStream(downloadFile);
             OutputStream outStream = response.getOutputStream()) {
             
            // Thiết lập Headers để trình duyệt hiểu đây là một file cần tải về
            String mimeType = getServletContext().getMimeType(downloadFile.getAbsolutePath());
            if (mimeType == null) {        
                mimeType = "application/octet-stream"; // Ép tải xuống nếu không nhận diện được loại file
            }
            
            response.setContentType(mimeType);
            response.setContentLength((int) downloadFile.length());
            
            // Ép trình duyệt hiển thị hộp thoại "Save As..." với tên gốc
            String headerKey = "Content-Disposition";
            // Bỏ phần timestamp đi để lúc tải về tên file đẹp như ban đầu (cắt từ dấu _ đầu tiên)
            String originalName = fileName.contains("_") ? fileName.substring(fileName.indexOf("_") + 1) : fileName;
            String headerValue = String.format("attachment; filename=\"%s\"", originalName);
            response.setHeader(headerKey, headerValue);

            // Bơm dữ liệu từ ổ đĩa ra ngoài cho người dùng
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