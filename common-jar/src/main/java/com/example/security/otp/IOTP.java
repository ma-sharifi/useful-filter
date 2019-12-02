package com.example.security.otp;

import java.security.GeneralSecurityException;
import java.util.Base64;

/**
 * @author me-sharifi on 6/11/2019 at 4:23 PM.
 */
public interface IOTP {
    public static int INTERVAL = 30;

    public byte[] generateKey();

    public String generateToken(byte[] key) throws GeneralSecurityException;

    public boolean validateToken(byte[] key, String otp) throws GeneralSecurityException;

    default String generateKeyStr() {// default method
        return Base64.getEncoder().encodeToString(generateKey());
    }

    default long getTimeSequence(int timeOffset) {
        long seq = System.currentTimeMillis() / 1000;
        return seq / INTERVAL;
    }

    default long getTimeSequence() {
        long seq = System.currentTimeMillis() / 1000;
        return seq / INTERVAL;
    }

}
