package com.example.rest.endpoint;

/**
 * Created by me-sharifi on 2/17/2018.
 */
import com.example.entity.Role;
import com.example.entity.User;
import com.example.rest.filter.jwtneeded.JWTTokenNeeded;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * Simple resource which rely on two roles: "user" and "admin".
 *
 * @author Deisss (MIT License)
 */
@Path("com/example/security")
@PermitAll
@JWTTokenNeeded
public class SecurityResource {
    @Context SecurityContext sc;
    @GET
    @Path("user")
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    public Response user() {
        return Response.ok(getUser().toJSON()).build();
    }

    @GET
    @Path("all")
    @RolesAllowed({Role.Admin,Role.Guest,Role.User})
    @Produces(MediaType.APPLICATION_JSON)
    public Response all() {
        return Response.ok(getUser().toJSON()).build();
    }

    @GET
    @Path("admin")
    @RolesAllowed({Role.Admin})
    @Produces(MediaType.APPLICATION_JSON)
    public Response admin() {
        return Response.ok(getUser().toJSON()).build();
    }
    @GET
    @Path("guest")
    @RolesAllowed({Role.Guest})
    @Produces(MediaType.APPLICATION_JSON)
    public Response guset() {
        return Response.ok(getUser().toJSON()).build();
    }
    @GET
    @Path("deny-all")
    @DenyAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response doDenyAll() {
        return Response.ok(getUser().toJSON()).build();
    }
    @GET
    @Path("permit-all")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response doPermitAll() {
        return Response.ok(getUser().toJSON()).build();
    }

    private User getUser(){
        return  (User) sc.getUserPrincipal();
    }
}
