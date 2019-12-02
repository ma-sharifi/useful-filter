package com.example.rest.filter.jwtneeded;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by MSH on 02/04/2017.
 * You won't need to persist JWT tokens if you don't need to track them
 * When persisting tokens, always consider removing the old ones in order to prevent your database from growing indefinitely.
 * If you want to revoke tokens, you must keep the track of them. You don't need to store the whole token on server side,
 * store only the token identifier (that must be unique) and some metadata if you need. For the token identifier you could use UUID.
 //http://stackoverflow.com/questions/26777083/best-practice-for-rest-token-based-authentication-with-jax-rs-and-jersey
 *
 *  https://stormpath.com/blog/beginners-guide-jwts-in-java
 *  First, what is a JSON Web Token, or JWT (pronounced “jot”)? In a nutshell,
 *  a JWT is a secure and trustworthy standard for token authentication.
 *  JWTs allow you to digitally sign information (referred to as claims) with a signature and can be verified at a later time with a secret signing key
 *
 *  In this method, tokens are generated for your users after they present
 *  verifiable credentials. The initial authentication could be by
 *  username/password credentials, API keys or even tokens from another service.
 *  (Stormpath’s API Key Authentication Feature is an example of this.)
 *
 *
 */
@javax.ws.rs.NameBinding
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface JWTTokenNeeded {
}