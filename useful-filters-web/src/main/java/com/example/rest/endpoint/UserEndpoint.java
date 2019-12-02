package com.example.rest.endpoint;

import com.example.entity.Role;
import com.example.rest.endpoint.jwt.KeyGenerator;
import com.example.rest.endpoint.jwt.PasswordUtils;
import com.example.entity.User;
import com.example.rest.filter.jwtneeded.JWTTokenNeeded;
import com.example.util.AppUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.log4j.Logger;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.security.Key;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

/**
 * @author Antonio Goncalves
 * http://www.antoniogoncalves.org
 * --
 */
@Path("/users")
public class UserEndpoint {

    // ======================================
    // =          Injection Points          =
    // ======================================

    @Context
    private UriInfo uriInfo;

    @Inject
    private Logger logger;

    @Inject
    private KeyGenerator keyGenerator;

    @Context
    private SecurityContext securityContext;
    // ======================================
    // =          Business methods          =
    // ======================================

    @POST
    @Path("login")
    @Consumes(APPLICATION_FORM_URLENCODED)
    @Produces(APPLICATION_JSON)
    public Response authenticateUser(@FormParam("username") String username,
                                     @FormParam("password") String password) {
        try {
            // Authenticate the user using the credentials provided
            User user = AppUtil.userMap.get(username);
            if (user == null || !user.getPassword().equals(PasswordUtils.digestPassword(password)))
                return Response.status(Response.Status.NOT_FOUND).build();
            String token = issueToken(user);// Issue a token for the user
            // Return the token on the response
            return Response.ok(user.toJSON()).header(AUTHORIZATION, "Bearer " + token).build();
        } catch (Exception e) {
            return Response.status(UNAUTHORIZED).build();
        }
    }

    @GET
    @JWTTokenNeeded
    @RolesAllowed({Role.User,Role.Admin})
    @Path("{username}")
    public Response findById(@PathParam("username") String username) throws Exception {
        Principal principal =securityContext.getUserPrincipal();
        User user= (User) principal;
//        System.err.println("##user2: "+user);
//        System.err.println("#principal: "+principal);
//        if ( securityContext.isUserInRole(Role.admin.name())) {//TODO FOR PROD && securityContext.isSecure()
//            System.out.println("##"+securityContext.getUserPrincipal()+" #accessed customer database.");
//        }
//        System.out.println("#isSecure: "+securityContext.isSecure() +" ,isUserInRole Admin: "+securityContext.isUserInRole("Admin")+
//                " ,isUserInRole Test: "+securityContext.isUserInRole("Test")+
//                " ,isUserInRole Visitor: "+securityContext.isUserInRole("Visitor"));
//        return Response.ok(securityContext.getUserPrincipal().getName()+","+securityContext.getAuthenticationScheme()+","+securityContext.isSecure()+","+securityContext.isUserInRole("Admin")+" accessed customer database.").build();
        if (principal == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(user.toJSON()).build();
    }

    private String issueToken(User user) {
        Key key = keyGenerator.generateKey();
        String jwtToken = Jwts.builder()
                .setSubject(user.getUsername())
                .claim("uuid", user.getUuid())
                .claim("mobile_no", user.getMobileNo())
                .claim("roles", user.getRoles())
                .setIssuer(uriInfo.getAbsolutePath().toString())
                .setIssuedAt(new Date())
                .setExpiration(toDate(LocalDateTime.now().plusMinutes(10L)))
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
        logger.info("#### generating token for a key : " + jwtToken + " - " + key);
        return jwtToken;
    }

    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

//    private User authenticate(String username, String password) throws SecurityException {
//        User user = AppUtil.userMap.get(username);
////        if (user.getPassword().equals(PasswordUtils.digestPassword(password)))
//        if (user.getPassword().equals(password))
//            return user;
//        throw new SecurityException("Invalid user/password");
//    }

    // ======================================
    // =          Injection Points          =
    // ======================================


    // ======================================
    // =          Private methods           =
    // ======================================


}
