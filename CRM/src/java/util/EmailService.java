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
    // ── Cấu hình SMTP ─────────────────────────────────────────
    private static final String HOST = "smtp.gmail.com";
    private static final int PORT = 587;
    private static final String USERNAME = "kicoson123@gmail.com";
    private static final String PASSWORD = "xjzf ackm qbpx wclg";

    // ── Gửi email thông báo đổi status ────────────────────────
    /**
     * Gửi email khi staff đổi status ticket.
     *
     * @param toEmail email của customer
     * @param customerName tên customer (để cá nhân hóa)
     * @param ticketId ID của ticket
     * @param ticketTitle tiêu đề ticket
     * @param oldStatus trạng thái cũ
     * @param newStatus trạng thái mới
     * @return true nếu gửi thành công
     */
    public static boolean sendEmail(String toEmail, String subject, String bodyHTML, Collection<Part> fileParts) {
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
                Multipart multipart = new MimeMultipart();
                MimeBodyPart textPart = new MimeBodyPart();
                textPart.setContent(bodyHTML, "text/html; charset=UTF-8");
                multipart.addBodyPart(textPart);
                for (Part part : fileParts) {
                    if (part.getSize() > 0 && part.getSubmittedFileName() != null && !part.getSubmittedFileName().isEmpty()) {

                        MimeBodyPart attachPart = new MimeBodyPart();
                        String fileName = part.getSubmittedFileName();

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

    public static boolean sendTicketStatusUpdate(
            String toEmail,
            String customerName,
            int ticketId,
            String ticketTitle,
            String oldStatus,
            String newStatus) {

        try {
            Session session = createSession();

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME, "CRM Support System"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(toEmail));
            message.setSubject(buildSubject(ticketId, newStatus));
            message.setContent(
                    buildBody(customerName, ticketId, ticketTitle, oldStatus, newStatus),
                    "text/html; charset=UTF-8");

            Transport.send(message);
            System.out.println("Email sent to: " + toEmail + " | Ticket #" + ticketId);
            return true;

        } catch (Exception e) {
            // Không để email lỗi crash luồng chính
            System.err.println("Email send failed for ticket #" + ticketId + ": " + e.getMessage());
            return false;
        }
    }

    private static Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", String.valueOf(PORT));
        props.put("mail.smtp.ssl.trust", HOST);

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });
    }

    private static String buildSubject(int ticketId, String newStatus) {
        String statusVi;
        switch (newStatus) {
            case "In Progress":
                statusVi = "Đang xử lý";
                break;
            case "Resolved":
                statusVi = "Đã giải quyết";
                break;
            default:
                statusVi = newStatus;
        }
        return "[CRM Support] Ticket #" + ticketId + " — " + statusVi;
    }

    private static String buildBody(
            String customerName,
            int ticketId,
            String ticketTitle,
            String oldStatus,
            String newStatus) {

        // Badge màu theo status
        String badgeColor;
        String statusLabel;
        switch (newStatus) {
            case "In Progress":
                badgeColor = "#ffc107";
                statusLabel = "Đang xử lý";
                break;
            case "Resolved":
                badgeColor = "#28a745";
                statusLabel = "Đã giải quyết";
                break;
            default:
                badgeColor = "#dc3545";
                statusLabel = newStatus;
        }

        String resolvedNote = "Resolved".equals(newStatus)
                ? "<p style='background:#e8f5e9;padding:12px 16px;border-radius:8px;"
                + "color:#2e7d32;font-size:14px;'>"
                + "✅ Vấn đề của bạn đã được giải quyết. Hãy vào hệ thống để đánh giá chất lượng hỗ trợ.</p>"
                : "";

        return "<!DOCTYPE html><html><body style='font-family:Segoe UI,sans-serif;"
                + "background:#f4f6fb;padding:30px;'>"
                + "<div style='max-width:560px;margin:0 auto;background:white;"
                + "border-radius:16px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.08);'>"
                // Header
                + "<div style='background:linear-gradient(135deg,#667eea,#764ba2);"
                + "padding:28px 32px;'>"
                + "<h2 style='color:white;margin:0;font-size:20px;'>CRM Support System</h2>"
                + "<p style='color:rgba(255,255,255,0.8);margin:6px 0 0;font-size:13px;'>"
                + "Cập nhật trạng thái ticket</p>"
                + "</div>"
                // Body
                + "<div style='padding:28px 32px;'>"
                + "<p style='color:#333;font-size:15px;'>Xin chào <strong>" + customerName + "</strong>,</p>"
                + "<p style='color:#555;font-size:14px;line-height:1.6;'>"
                + "Ticket hỗ trợ của bạn vừa được cập nhật trạng thái.</p>"
                // Ticket info box
                + "<div style='background:#f8f9fc;border-radius:10px;padding:16px 20px;margin:20px 0;"
                + "border-left:4px solid #667eea;'>"
                + "<p style='margin:0 0 8px;font-size:13px;color:#888;'>Ticket #" + ticketId + "</p>"
                + "<p style='margin:0 0 12px;font-weight:600;font-size:15px;color:#333;'>"
                + ticketTitle + "</p>"
                + "<div style='display:flex;gap:10px;align-items:center;'>"
                + "<span style='font-size:12px;color:#888;'>Trạng thái mới:</span>"
                + "<span style='background:" + badgeColor + ";color:"
                + ("In Progress".equals(newStatus) ? "#333" : "white")
                + ";padding:4px 12px;border-radius:12px;font-size:12px;font-weight:600;'>"
                + statusLabel + "</span>"
                + "</div>"
                + "</div>"
                + resolvedNote
                + "<p style='color:#888;font-size:12px;margin-top:20px;'>"
                + "Bạn nhận được email này vì đã đăng ký hỗ trợ qua hệ thống CRM.</p>"
                + "</div>"
                // Footer
                + "<div style='background:#f8f9fc;padding:16px 32px;"
                + "border-top:1px solid #eee;text-align:center;'>"
                + "<p style='color:#aaa;font-size:11px;margin:0;'>"
                + "© 2026 CRM System — Hệ thống quản lý khách hàng</p>"
                + "</div>"
                + "</div></body></html>";
    }
}
