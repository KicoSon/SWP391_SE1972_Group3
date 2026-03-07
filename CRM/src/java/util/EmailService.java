/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.util.Properties;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;

public class EmailService {

    // CẤU HÌNH GMAIL (Thay bằng email và app password của bạn)
    private static final String SENDER_EMAIL = "haicvhe181052@fpt.edu.vn"; // <--- THAY CÁI NÀY
    private static final String APP_PASSWORD = "celo ljup vjqa plzx";     // <--- THAY CÁI 16 KÝ TỰ VÀO ĐÂY

    public static boolean sendEmail(String toEmail, String subject, String bodyHTML) {
        // 1. Cấu hình SMTP Server
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // Host của Gmail
        props.put("mail.smtp.port", "587"); // Port TLS
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // Bắt buộc dùng TLS

        // 2. Tạo Session (Phiên làm việc)
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD);
            }
        });

        try {
            // 3. Tạo nội dung Email
            MimeMessage msg = new MimeMessage(session);
            
            // Header
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            // Người gửi (Kèm tên hiển thị cho đẹp)
            msg.setFrom(new InternetAddress(SENDER_EMAIL, "CRM System")); 
            
            // Người nhận
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            
            // Tiêu đề & Nội dung
            msg.setSubject(subject, "UTF-8");
            msg.setContent(bodyHTML, "text/html; charset=UTF-8"); // Gửi dạng HTML
            msg.setSentDate(new Date());

            // 4. Gửi đi
            Transport.send(msg);
            System.out.println("Email sent successfully to: " + toEmail);
            return true;

        } catch (Exception e) {
            e.printStackTrace(); // In lỗi ra console để debug
            return false;
        }
    }
    
    // Hàm main để test thử xem có gửi được không (Chạy file này -> Run File)
    public static void main(String[] args) {
        // Thay email nhận bằng email cá nhân khác của bạn để test
        sendEmail("email_nhan_test@gmail.com", "Test Email from CRM", "<h1>Xin chào!</h1><p>Đây là mail test.</p>");
    }
}