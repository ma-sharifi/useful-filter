package com.example.security.otp;

import lombok.Getter;

/**
 * @author MSH on 6/27/2019 at 03:41 PM.
 */
@Getter
public enum OTPAlgorithm {
    SHA1(0,"HmacSHA1",20),
    SHA256(1,"HmacSHA256",32),
    SHA512(2,"HmacSHA512",64);

    private Integer value;
    private String algorithm;
    private int keyLength;

    OTPAlgorithm(int value,String algorithm, int keyLength) {
        this.value = value;
        this.algorithm = algorithm;
        this.keyLength = keyLength;
    }
    public static OTPAlgorithm getEnum(Integer i) {
        for (OTPAlgorithm type : OTPAlgorithm.values()) {
            if (type.value.equals(i)) {
                return type;
            }
        }
        return null;
    }

}
