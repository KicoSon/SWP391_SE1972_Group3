package util;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class SendMail {

    public static void sendOTP(String toEmail, String otp) {

        final String fromEmail = "crmsystemgroup3@gmail.com";
        final String password = "smlq cncj jedn lycp";

        Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props,
                new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {

            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(fromEmail));

            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(toEmail)
            );

            message.setSubject("OTP Reset Password");

            message.setText("Your OTP code is: " + otp);

            Transport.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}