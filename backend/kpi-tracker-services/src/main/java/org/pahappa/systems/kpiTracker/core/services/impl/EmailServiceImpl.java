package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.services.EmailService;
import org.pahappa.systems.kpiTracker.core.services.MailSettingService;
import org.pahappa.systems.kpiTracker.models.MailSetting;
import org.sers.webutils.model.exception.OperationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@Transactional
public class EmailServiceImpl implements EmailService {

    private static final Logger LOGGER = Logger.getLogger(EmailServiceImpl.class.getName());

    @Autowired
    private MailSettingService mailSettingService;
    @Override
    public void sendEmail(String to, String subject, String body) throws OperationFailedException {

        MailSetting mailSetting = mailSettingService.getMailSetting();
        if (mailSetting == null) {
            throw new OperationFailedException("Email settings not configured. Please configure mail settings in the system.");
        }

        final String username = mailSetting.getSenderAddress();
        final String password = mailSetting.getSenderPassword();

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // For TLS
        props.put("mail.smtp.host", mailSetting.getSenderSmtpHost());
        props.put("mail.smtp.port", mailSetting.getSenderSmtpPort());

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

            LOGGER.log(Level.INFO, "Successfully sent email to: {0}", to);
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Failed to send email to " + to + ": " + e.getMessage(), e);
            throw new OperationFailedException("The system could not send the email: " + e.getMessage());
        }
    }
}