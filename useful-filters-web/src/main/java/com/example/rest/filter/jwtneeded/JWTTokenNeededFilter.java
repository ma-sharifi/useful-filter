package com.example.rest.filter.jwtneeded;

import com.example.entity.User;
import com.example.rest.endpoint.jwt.KeyGenerator;
import com.example.rest.filter.securityprincipal.SecurityContextCustom;
import com.example.util.AppUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.log4j.Logger;
import com.example.serializer.GsonModel;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.security.Key;
import java.util.List;

/**
 * @author Antonio Goncalves
 * http://www.antoniogoncalves.org
 * --
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
@JWTTokenNeeded
public class JWTTokenNeededFilter implements ContainerRequestFilter {

    // ======================================
    // =          Injection Points          =
    // ======================================

    @Inject
    private Logger logger;

    @Inject
    private KeyGenerator keyGenerator;
    private static final String REALM = "TEST";
    private static final String AUTHENTICATION_SCHEME = "Bearer";
    // ======================================
    // =          Business methods          =
    // ======================================

    @Override
    public void filter(ContainerRequestContext requestContext)  {

        // Get the HTTP Authorization header from the request
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        logger.info("#### authorizationHeader : " + authorizationHeader);
//
//        // Check if the HTTP Authorization header is present and formatted correctly
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.info("#### invalid authorizationHeader : " + authorizationHeader);
            throw new NotAuthorizedException("Authorization header must be provided");
        }
        // Extract the token from the HTTP Authorization header
        String token = authorizationHeader.substring("Bearer".length()).trim();
        UserJwtClaims userJwtClaims = parseJWT(token);
        if(userJwtClaims!=null){
            User user= AppUtil.userMap.get(userJwtClaims.getUsername());
            System.err.println("##user filter jwt: "+user);
            requestContext.setSecurityContext(new SecurityContextCustom(user, AUTHENTICATION_SCHEME));//set Security Context

        }else
            throw new NotAuthorizedException("NOT AUTHORIZE :-(");
//        System.out.println("#userJwtClaims: " + userJwtClaims);

    }
    private void validateToken(String token) throws Exception {
        // Check if the token was issued by the server and if it's not expired
        // Throw an Exception if the token is invalid
    }
    private boolean isTokenBasedAuthentication(String authorizationHeader) {
        // Check if the Authorization header is valid
        // It must not be null and must be prefixed with "Bearer" plus a whitespace
        // The authentication scheme comparison must be case-insensitive
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                .startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }
    private void abortWithUnauthorized(ContainerRequestContext requestContext) {
        // Abort the filter chain with a 401 status code response
        // The WWW-Authenticate header is sent along with the response
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .header(HttpHeaders.WWW_AUTHENTICATE,
                                AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"")
                        .build());
    }
    private UserJwtClaims parseJWT(String jwt) {
        Claims claims = null;
        try {
            Key key = keyGenerator.generateKey();
            //This line will throw an exception if it is not a signed JWS (as expected)
            claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(jwt).getBody();


//        System.out.println("Subject: " + claims.getSubject());
//        System.out.println("Issuer: " + claims.getIssuer());
//        System.out.println("Expiration: " + claims.getExpiration());
//        System.out.println("app-token: " + claims.get("app-token"));
//        System.out.println("uuid: " + claims.get("uuid"));
//        System.out.println("scope: " + claims.get("scope"));
//        System.out.println("****claims: "+claims);
            UserJwtClaims jwtClaims = new UserJwtClaims();
            jwtClaims.mobileNo = (String) claims.get("mobile_no");
            jwtClaims.uuid = (String) claims.get("uuid");
            jwtClaims.roles = (List) claims.get("roles");
            jwtClaims.username = claims.getSubject();

            return jwtClaims;
        } catch (Exception ex) {
//            ex.printStackTrace();
        }
        return null;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    class UserJwtClaims extends GsonModel {
        String username;
        String uuid;
        String mobileNo;
        List<String> roles;
    }
}