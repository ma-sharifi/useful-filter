package com.example.security.otp;

import java.security.GeneralSecurityException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author me-sharifi on 6/11/2019 at 4:23 PM.
 */
public class STOTP implements IOTP {
    private final int keyLength;
    private final String algorithm;

    public STOTP() {
        this.keyLength = 32;
        this.algorithm = "HmacSHA256";
    }

    public STOTP(int keyLength) {
        this.keyLength = keyLength;
        this.algorithm = "HmacSHA256";
    }

    public STOTP(int keyLength, String algorithm) {
        this.keyLength = keyLength;
        this.algorithm = algorithm;
    }

    public STOTP(OTPAlgorithm otpAlgorithm) {
        this.keyLength = otpAlgorithm.getKeyLength();
        this.algorithm = otpAlgorithm.getAlgorithm();
    }


    @Override
    public byte[] generateKey() {
        byte[] key = new byte[keyLength];
        ThreadLocalRandom.current().nextBytes(key);
        return key;
    }

    @Override
    public String generateToken(byte[] key) throws GeneralSecurityException {//HmacSHA256
        PasscodeGenerator passcodeGenerator = new PasscodeGenerator(new MACGenerator(key, algorithm));
        return passcodeGenerator.generateResponseCode(getTimeSequence());
    }

    @Override
    public boolean validateToken(byte[] key, String otp) throws GeneralSecurityException {
        PasscodeGenerator passcodeGenerator = new PasscodeGenerator(new MACGenerator(key, algorithm));
        long timeSequence = getTimeSequence();
        boolean isValid = passcodeGenerator.verifyResponseCode(timeSequence, otp);
        System.out.println("Expected otp is: " + passcodeGenerator.generateResponseCode(timeSequence, null)
                + " Client sent: " + otp + " ,validate: " + isValid + " ,interval: " + timeSequence);
        if (!isValid) {
            isValid = passcodeGenerator.verifyTimeoutCode(timeSequence, otp);
            System.out.println("Try time window " + isValid);
        }
        return isValid;
    }
}
