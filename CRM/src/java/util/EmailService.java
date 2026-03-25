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
import java.util.Date;
import java.util.Collection;

public class EmailService {
    public static String lastError = "";

    private static final String SENDER_EMAIL = "haicvhe181052@fpt.edu.vn";
    private static final String APP_PASSWORD = "celo ljup vjqa plzx";

    public static boolean sendEmail(String toEmail, String subject, String bodyHTML, Collection<Part> fileParts) {
        // Cấu hình Gmail SMTP qua STARTTLS.
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

            boolean hasAttachments = false;
            if (fileParts != null) {
                for (Part part : fileParts) {
                    if (part.getSize() > 0 && part.getSubmittedFileName() != null && !part.getSubmittedFileName().isEmpty()) {
                        hasAttachments = true;
                        break;
                    }
                }
            }

            if (hasAttachments) {
                // Có đính kèm: tạo multipart gồm phần nội dung HTML + các part file.
                Multipart multipart = new MimeMultipart();
                MimeBodyPart textPart = new MimeBodyPart();
                textPart.setContent(bodyHTML, "text/html; charset=UTF-8");
                multipart.addBodyPart(textPart);
                for (Part part : fileParts) {
                    if (part.getSize() > 0 && part.getSubmittedFileName() != null && !part.getSubmittedFileName().isEmpty()) {

                        MimeBodyPart attachPart = new MimeBodyPart();
                        String fileName = part.getSubmittedFileName();

                        // Stream trực tiếp file upload (Part) sang mail attachment.
                        DataSource source = new DataSource() {
                            @Override
                            public InputStream getInputStream() throws IOException {
                                return part.getInputStream();
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
                msg.setContent(multipart);
            } else {
                // Gửi email đơn giản chỉ có text/html, không đính kèm
                msg.setContent(bodyHTML, "text/html; charset=UTF-8");
            }

            Transport.send(msg);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            // Ghi lại stacktrace để controller có thể hiển thị/chẩn đoán khi gửi lỗi.
            lastError = e.toString() + "\n";
            for (StackTraceElement element : e.getStackTrace()) {
                lastError += element.toString() + "\n";
            }
            if (e.getCause() != null) {
                lastError += "Caused by: " + e.getCause().toString() + "\n";
            }
            return false;
        }
    }
}
