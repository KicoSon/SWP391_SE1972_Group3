/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import jakarta.activation.DataSource;
import java.util.Properties;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Collection;

public class EmailService {

    // CẤU HÌNH GMAIL (Thay bằng email và app password của bạn)
    private static final String SENDER_EMAIL = "haicvhe181052@fpt.edu.vn"; // <--- THAY CÁI NÀY
    private static final String APP_PASSWORD = "celo ljup vjqa plzx";     // <--- THAY CÁI 16 KÝ TỰ VÀO ĐÂY

    // HÀM GỬI MAIL TỔNG QUÁT (Có file hoặc không đều OK)
    public static boolean sendEmail(String toEmail, String subject, String bodyHTML, Collection<Part> fileParts) {

        // 1. Cấu hình SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD);
            }
        });

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(SENDER_EMAIL, "Sales CRM System"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            msg.setSubject(subject, "UTF-8");
            msg.setSentDate(new Date());

            // 2. TẠO MULTIPART (Chứa cả nội dung và file)
            Multipart multipart = new MimeMultipart();

            // --- PHẦN 1: NỘI DUNG TEXT (Luôn có) ---
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(bodyHTML, "text/html; charset=UTF-8");
            multipart.addBodyPart(textPart);

            // --- PHẦN 2: FILE ĐÍNH KÈM (Kiểm tra xem có file không) ---
            if (fileParts != null && !fileParts.isEmpty()) {
                // Dùng vòng lặp để đính kèm từng file một
                for (Part part : fileParts) {
                    if (part.getSize() > 0 && part.getSubmittedFileName() != null && !part.getSubmittedFileName().isEmpty()) {

                        MimeBodyPart attachPart = new MimeBodyPart();
                        String fileName = part.getSubmittedFileName();
                        InputStream is = part.getInputStream();

                        DataSource source = new DataSource() {
                            @Override
                            public InputStream getInputStream() throws IOException {
                                return is;
                            }

                            @Override
                            public java.io.OutputStream getOutputStream() throws IOException {
                                throw new IOException("Read-only");
                            }

                            @Override
                            public String getContentType() {
                                return "application/octet-stream";
                            }

                            @Override
                            public String getName() {
                                return fileName;
                            }
                        };

                        attachPart.setDataHandler(new jakarta.activation.DataHandler(source));
                        attachPart.setFileName(fileName);
                        multipart.addBodyPart(attachPart);
                    }
                }
            }

            msg.setContent(multipart);
            Transport.send(msg);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
