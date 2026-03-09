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
        try (FileInputStream inStream = new FileInputStream(downloadFile); OutputStream outStream = response.getOutputStream()) {

            // 1. Xác định loại file (MIME Type)
            String mimeType = getServletContext().getMimeType(downloadFile.getAbsolutePath());
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
            response.setContentType(mimeType);
            response.setContentLength((int) downloadFile.length());

            // 2. Xử lý tên file (Cắt bỏ timestamp để hiển thị tên gốc đẹp hơn)
            // Ví dụ: 172663_avatar.png -> avatar.png
            String originalName = fileName.contains("_") ? fileName.substring(fileName.indexOf("_") + 1) : fileName;

            // 3. QUAN TRỌNG: Điều chỉnh Content-Disposition
            // Nếu là ảnh (image/png, image/jpeg...) -> Dùng 'inline' để hiện preview
            // Các file khác -> Dùng 'attachment' để ép tải về
            String disposition = "attachment"; // Mặc định là TẢI VỀ
            String mode = request.getParameter("mode"); // Lấy tham số từ URL

            // Chỉ bật chế độ "Xem ngay" (inline) nếu:
            // 1. Là file ảnh
            // 2. VÀ KHÔNG CÓ yêu cầu tải về (mode != download)
            if (mimeType.startsWith("image/") && !"download".equals(mode)) {
                disposition = "inline";
            }
            // --------------------

            String headerKey = "Content-Disposition";
            String headerValue = String.format("%s; filename=\"%s\"", disposition, originalName);
            response.setHeader(headerKey, headerValue);

            // 4. Bơm dữ liệu ra
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
