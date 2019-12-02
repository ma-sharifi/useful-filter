package com.example.security.otp;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MACGenerator implements Signer{
    private byte[] key;
    private final String algorithm;//HmacSHA256

    public MACGenerator(byte[] key, String algorithm) {
        this.key = key;
        this.algorithm = algorithm;
    }
    /**
     * Generates a hash-based message authentication code
     * @param key The secret key
     * @param data   The message to generate the hmac for
     * @return A hash-based message authentication code
     * @throws NoSuchAlgorithmException If the algorithm is not supported by the provider
     * @throws InvalidKeyException      If the key is invalid
     */
    public byte[] create(byte[] key, byte[] data) throws RuntimeException {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, algorithm);
            Mac mac = Mac.getInstance(algorithm);
            mac.init(secretKeySpec);
            byte[] out = mac.doFinal(data);
            return out;
        } catch (Exception e) {
            throw new RuntimeException("Exception accourd in generates a hash-based message authentication code ", e);
        }
    }
    public byte[] create(String secretKey, String data)
            throws NoSuchAlgorithmException, InvalidKeyException {
        return create(secretKey.getBytes(),data.getBytes());
    }

    @Override
    public byte[] sign(byte[] data) throws GeneralSecurityException {
        return create(key, data);
    }
}
