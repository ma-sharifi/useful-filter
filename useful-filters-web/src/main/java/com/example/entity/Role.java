package com.example.entity;

/**
 * @author Mahdi Sharifi
 * @version 1.0.1
 * https://www.linkedin.com/in/mahdisharifi/
 * @since 19/11/2019
 */
public enum Role { //TODO convert to entity
    admin, user, guest;
    public final static  String Admin="admin";
    public final static  String User="user";
    public final static  String Guest="guest";

//    public static Role valueOf(String name) {
//        for (Role role : Role.values()) {
//            if (role.name().equalsIgnoreCase(name))
//                return role;
//        }
//        return null;
//    }
}
