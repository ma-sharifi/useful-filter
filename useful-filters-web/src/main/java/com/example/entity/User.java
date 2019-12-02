package com.example.entity;

/**
 * Created by me-sharifi on 2/17/2018.
 */
//https://simplapi.wordpress.com/2015/09/19/jersey-jax-rs-securitycontext-in-action/


import lombok.Data;
import lombok.EqualsAndHashCode;
import com.example.serializer.GsonExcludeField;
import com.example.serializer.GsonModel;

import java.security.Principal;
import java.util.List;

/**
 * User bean.
 *
 * @author Deisss (MIT License)
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class User extends GsonModel implements Principal {
    private String uuid, firstName, lastName, username, email, mobileNo;
    @GsonExcludeField
    private String password;
    private List<Role> roles;//TODO Change List to Set

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public String getName() {
        return this.firstName + " " + this.lastName;
    }
}
