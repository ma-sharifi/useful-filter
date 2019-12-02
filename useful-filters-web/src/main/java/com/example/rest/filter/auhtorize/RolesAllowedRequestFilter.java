package com.example.rest.filter.auhtorize;

import com.example.entity.Role;
import com.example.entity.User;
import org.glassfish.jersey.server.internal.LocalizationMessages;

import javax.annotation.Priority;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mahdi Sharifi
 * @since 23/11/2019
 * @version 1.0.0
 * https://www.linkedin.com/in/mahdisharifi/
 */
@Priority(Priorities.AUTHORIZATION) // authorization filter - should go after any authentication filters
public class RolesAllowedRequestFilter implements ContainerRequestFilter {

    private final boolean denyAll;
    private final List<Role> rolesAllowed;

    public RolesAllowedRequestFilter() {
        this.denyAll = true;
        this.rolesAllowed = null;
    }

    public RolesAllowedRequestFilter(final List<Role> rolesAllowed) {
        this.denyAll = false;
        this.rolesAllowed = (rolesAllowed != null) ? rolesAllowed : new ArrayList<>();
    }

    @Override
    public void filter(final ContainerRequestContext requestContext) {
        System.out.println("#rolesAllowed: "+rolesAllowed +" ,denyAll: "+denyAll);
        Principal principal =requestContext.getSecurityContext().getUserPrincipal();
        User user= (User) principal;
        System.err.println("user FILTER: "+user);
        if (!denyAll) {
            if (rolesAllowed != null && rolesAllowed.size() > 0 && !isAuthenticated(requestContext)) {
                throw new ForbiddenException(LocalizationMessages.USER_NOT_AUTHORIZED());
            }
            for (Role role : rolesAllowed) {
                if (requestContext.getSecurityContext().isUserInRole(role.name())) {
                    return;
                }
            }
        }
        throw new ForbiddenException(LocalizationMessages.USER_NOT_AUTHORIZED());
    }

    private static boolean isAuthenticated(final ContainerRequestContext requestContext) {
        return requestContext.getSecurityContext().getUserPrincipal() != null;
    }
}
//    private RolesAllowed getAnnotation() {
//        RolesAllowed annotation = resourceInfo.getResourceClass().getAnnotation(RolesAllowed.class);
//        if (resourceInfo.getResourceMethod().getAnnotation(RolesAllowed.class) != null) {
//            annotation = resourceInfo.getResourceMethod().getAnnotation(RolesAllowed.class);
//        }
//        return annotation;
//    }
