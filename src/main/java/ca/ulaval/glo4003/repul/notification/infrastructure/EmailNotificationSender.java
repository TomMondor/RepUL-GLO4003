package ca.ulaval.glo4003.repul.notification.infrastructure;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.notification.domain.Account;
import ca.ulaval.glo4003.repul.notification.domain.NotificationSender;

public class EmailNotificationSender implements NotificationSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailNotificationSender.class);

    private final String smtpHost = "smtp.gmail.com";
    private final String smtpProtocol = "TLSv1.2";
    private final int smtpPort = 587;
    private Properties properties;
    private String senderEmail;
    private String senderPassword;

    public EmailNotificationSender() {
        try {
            setProperties();
            try (BufferedReader reader = new BufferedReader(new FileReader(".env"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("EMAIL_SENDER=")) {
                        this.senderEmail = line.substring(13);
                    } else if (line.startsWith("PASSWORD_SENDER=")) {
                        this.senderPassword = line.substring(16);
                    }
                }
            } catch (IOException e) {
                LOGGER.info("Could not read .env file that contains the sender email and password.");
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            LOGGER.info("Error setting up email notification sender.");
            throw new RuntimeException(e);
        }
    }

    public void send(Account account, String messageBody) {
        try {
            Message message = createMessage(account, messageBody);
            Transport.send(message);
            LOGGER.info("Email sent successfully to {}.", account.email().value());
        } catch (MessagingException e) {
            LOGGER.info("Error sending email to {}.", account.email().value());
        }
    }

    private void setProperties() {
        properties = new Properties();
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.protocols", smtpProtocol);
        properties.put("mail.smtp.starttls.enable", "true");
    }

    private Message createMessage(Account account, String messageBody) throws MessagingException {
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(this.senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(account.email().value()));
            // TODO: 2023-10-20 change the email subject
            message.setSubject("REPUL Notif");
            message.setText(messageBody);
        } catch (MessagingException e) {
            LOGGER.info("Error sending email to {}.", account.email().value());
            throw e;
        }
        return message;
    }
}
