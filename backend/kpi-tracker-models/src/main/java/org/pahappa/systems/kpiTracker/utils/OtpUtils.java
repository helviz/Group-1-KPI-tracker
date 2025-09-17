package org.pahappa.systems.kpiTracker.utils;

import java.security.SecureRandom;
import java.text.DecimalFormat;

public class OtpUtils {

    /**
     * Generates a secure 6-digit numeric OTP (one time password)
     * @return A string representing the 6-digit OTP
     */
    public static String generateOTP(){
        // Using SecureRandom for cryptographically strong random numbers
        return new DecimalFormat("000000").format(new SecureRandom().nextInt(999999));
    }
}
