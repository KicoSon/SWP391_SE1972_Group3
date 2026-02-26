/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

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

    // Phải khớp với thư mục bạn đã lưu ở Bước 1
    private static final String UPLOAD_DIR = "D:" + File.separator + "CRM_Uploads";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Nhận tên file từ URL (Ví dụ: /download?file=170810293_BaoGia.pdf)
        String fileName = request.getParameter("file");
        
        if (fileName == null || fileName.isEmpty()) {
            response.getWriter().print("Không tìm thấy file!");
            return;
        }

        // Tìm file trên ổ cứng
        File downloadFile = new File(UPLOAD_DIR + File.separator + fileName);
        
        if (!downloadFile.exists()) {
            response.getWriter().print("File không tồn tại trên hệ thống!");
            return;
        }

        // Mở luồng đọc file từ ổ D
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
            String originalName = fileName.substring(fileName.indexOf("_") + 1);
            String headerValue = String.format("attachment; filename=\"%s\"", originalName);
            response.setHeader(headerKey, headerValue);

            // Bơm dữ liệu từ ổ D ra ngoài cho người dùng
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}