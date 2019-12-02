package com.example.security;

import java.security.MessageDigest;
import java.util.Base64;

/**
 * @author Antonio Goncalves
 *         http://www.antoniogoncalves.org
 *         --
 */

public class PasswordUtils {

    // ======================================
    // =          Business methods          =
    // ======================================

    public static String digestPassword(String plainTextPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");//MD2,MD5,SHA-1,SHA-256,SHA-384,SHA-512
            md.update(plainTextPassword.getBytes("UTF-8"));
            byte[] passwordDigest = md.digest();
            return new String(Base64.getEncoder().encode(passwordDigest));
        } catch (Exception e) {
            throw new RuntimeException("Exception encoding password", e);
        }
    }

    public static void main(String[] args) {
        System.out.println(digestPassword("1234"));
        System.out.println(digestPassword("Hell"));
    }
}