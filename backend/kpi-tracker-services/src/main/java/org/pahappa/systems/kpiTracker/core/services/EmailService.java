package org.pahappa.systems.kpiTracker.core.services;

import org.sers.webutils.model.exception.OperationFailedException;

/**
 * Service for sending emails within the KPI Tracker system.
 */
public interface EmailService {

  
    void sendEmail(String to, String subject, String body) throws OperationFailedException;
}