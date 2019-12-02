package com.example.rest.filter.securityprincipal;

/**
 * Created by me-sharifi on 2/17/2018.
 */

import com.example.entity.Role;
import com.example.entity.User;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
//https://simplapi.wordpress.com/2015/09/19/jersey-jax-rs-securitycontext-in-action/
//https://dzone.com/articles/custom-security-context-injax-rs

/**
 * Custom Security Context.
 *
 * @author Deisss (MIT License)
 */
public class SecurityContextCustom implements SecurityContext {
    private User user;
    private String scheme;

    public SecurityContextCustom(User user, String scheme) {
        this.user = user;
        this.scheme = scheme;
    }

    @Override
    public Principal getUserPrincipal() {
        return this.user;
    }

    @Override
    public boolean isUserInRole(String s) {
        if (user.getRoles() != null)
            for (Role role : user.getRoles()) {
                if (role.name().equalsIgnoreCase(s))
                    return true;
            }
        System.err.println("##isUserInRole: " + s + " ," + ",user->roles: " + user.getRoles() + " ,contains? " + user.getRoles().contains(s));
        return false;
    }
//    @Override
//    public boolean isUserInRole(String role) {
//
//        if (null == session || !session.isActive()) {
//            // Forbidden
//            Response denied = Response.status(Response.Status.FORBIDDEN).entity("Permission Denied").build();
//            throw new WebApplicationException(denied);
//        }
//
//        try {
//            // this user has this role?
//            return user.getRoles().contains(User.Role.valueOf(role));
//        } catch (Exception e) {
//        }
//
//        return false;
//    }

    @Override
    public boolean isSecure() {
        return "https".equals(this.scheme);
    }

    @Override
    public String getAuthenticationScheme() {
        return scheme;
    }
}