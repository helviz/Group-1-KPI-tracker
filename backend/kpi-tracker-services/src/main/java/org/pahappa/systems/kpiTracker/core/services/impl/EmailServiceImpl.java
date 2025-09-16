package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.services.EmailService;
import org.pahappa.systems.kpiTracker.core.services.MailSettingService;
import org.pahappa.systems.kpiTracker.models.MailSetting;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.server.core.utils.MailUtils; // [1, 2]
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@Transactional
public class KpiEmailServiceImpl implements EmailService {

    private static final Logger LOGGER = Logger.getLogger(KpiEmailServiceImpl.class.getName());

    @Autowired
    private MailSettingService mailSettingService;
    @Override
    public void sendEmail(String to, String subject, String body) throws OperationFailedException {

        MailSetting mailSetting = mailSettingService.getMailSetting();
        if (mailSetting == null) {
            throw new OperationFailedException("Email settings not configured. Please configure mail settings in the system.");
        }

        try {

            MailUtils.sendEmail(to, subject, body);
            LOGGER.log(Level.INFO, "Successfully sent email to: {0}", to);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to send email to " + to + ": " + ex.getMessage(), ex);
            throw new OperationFailedException("The system could not send the email: " + ex.getMessage());
        }
    }
}