package com.example.rest.filter.dynamicfeature;

//https://github.com/jersey/jersey/blob/master/core-server/src/main/java/org/glassfish/jersey/server/filter/RolesAllowedDynamicFeature.java
//https://stackoverflow.com/questions/26777083/best-practice-for-rest-token-based-authentication-with-jax-rs-and-jersey

import com.example.entity.Role;
import com.example.rest.filter.auhtorize.RolesAllowedRequestFilter;
import com.example.rest.filter.jwtneeded.JWTTokenNeededFilter;
import com.example.rest.filter.limit.amount.amount.AllowedAmountNeeded;
import com.example.rest.filter.limit.amount.amount.AmountAllowedPerTime;
import org.apache.log4j.Logger;
import org.glassfish.jersey.server.model.AnnotatedMethod;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Path;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;
import java.util.*;

/**
 * A {@link DynamicFeature} supporting the {@code javax.annotation.security.RolesAllowed},
 * {@code javax.annotation.security.PermitAll} and {@code javax.annotation.security.DenyAll}
 * on resource methods and sub-resource methods.
 * <p/>
 * The {@link javax.ws.rs.core.SecurityContext} is utilized, using the
 * {@link javax.ws.rs.core.SecurityContext#isUserInRole(String) } method,
 * to ascertain if the user is in one
 * of the roles declared in by a {@code &#64;RolesAllowed}. If a user is in none of
 * the declared roles then a 403 (Forbidden) response is returned.
 * <p/>
 * If the {@code &#64;DenyAll} annotation is declared then a 403 (Forbidden) response
 * is returned.
 * <p/>
 * If the {@code &#64;PermitAll} annotation is declared and is not overridden then
 * this filter will not be applied.
 * <p/>
 * If a user is not authenticated and annotated method is restricted for certain roles then a 403
 * (Not Authenticated) response is returned.
 *
 * @author Paul Sandoz
 * @author Martin Matula
 * @author Mahdi Sharifi
 * @DenyAll on the method takes precedence over @RolesAllowed and @PermitAll on the class.
 * @RolesAllowed on the method takes precedence over @PermitAll on the class.
 * @PermitAll on the method takes precedence over @RolesAllowed on the class.
 * @DenyAll can't be attached to classes.
 * @RolesAllowed on the class takes precedence over @PermitAll on the class.
 */
@Provider
public class RolesAllowedDynamicFeature implements DynamicFeature {
    private static Logger logger = Logger.getLogger(RolesAllowedDynamicFeature.class);

    @Override
    public void configure(final ResourceInfo resourceInfo, final FeatureContext configuration) {
        Method method = resourceInfo.getResourceMethod();
        Path pathMethodRuntime = method.getAnnotation(Path.class);
        Path pathClassRuntime = method.getDeclaringClass().getAnnotation(Path.class);
        if (pathClassRuntime != null && pathMethodRuntime != null) {
//            logger.warn("*found path: " + pathClassRuntime.value() + "/" + pathMethodRuntime.value() + " please check with Admin or main developer for activating this path");
            if ("application.wadl".equalsIgnoreCase(pathClassRuntime.value())) {
                logger.info("##### " + pathClassRuntime.value() + " Found");
                configuration.register(new JWTTokenNeededFilter());
            }

            // Get the resource method which matches with the requested URL
            // Extract the roles declared by it
            final AnnotatedMethod annotatedMethod = new AnnotatedMethod(resourceInfo.getResourceMethod());
            // DenyAll on the method take precedence over RolesAllowed and PermitAll
            if (annotatedMethod.isAnnotationPresent(DenyAll.class)) {
                configuration.register(new RolesAllowedRequestFilter());
                return;
            }

            // RolesAllowed on the method takes precedence over PermitAll
            RolesAllowed rolesAllowed = annotatedMethod.getAnnotation(RolesAllowed.class);
            if (rolesAllowed != null) {
                List<Role> rolesMethod = new ArrayList<>();
                for (String roleString : rolesAllowed.value()) {
                    rolesMethod.add(Role.valueOf(roleString));
                }
                configuration.register(new RolesAllowedRequestFilter(rolesMethod));
                return;
            }

            // PermitAll takes precedence over RolesAllowed on the class
            if (annotatedMethod.isAnnotationPresent(PermitAll.class)) {
                // Do nothing.
                return;
            }
            // Get the resource class which matches with the requested URL
            // DenyAll can't be attached to classes
            // RolesAllowed on the class takes precedence over PermitAll
            rolesAllowed = resourceInfo.getResourceClass().getAnnotation(RolesAllowed.class);
            if (rolesAllowed != null) {
                List<Role> rolesClass = new ArrayList<>();
                for (String roleString : rolesAllowed.value()) {
                    rolesClass.add(Role.valueOf(roleString));
                }
                configuration.register(new RolesAllowedRequestFilter(rolesClass));
            }
//            AllowedAmountNeeded allowedAmountNeeded = resourceInfo.getResourceClass().getAnnotation(AllowedAmountNeeded.class);
            AllowedAmountNeeded allowedAmountNeeded = resourceInfo.getResourceMethod().getAnnotation(AllowedAmountNeeded.class);
            if (annotatedMethod.isAnnotationPresent(AllowedAmountNeeded.class)) {
                System.out.println("##allowedAmountNeeded: "+ allowedAmountNeeded);
                System.out.println("##annotatedMethod: "+annotatedMethod);
                configuration.register(new AmountAllowedPerTime(allowedAmountNeeded.amountLimit(), allowedAmountNeeded.timeWindow(), allowedAmountNeeded.timeUnit(), allowedAmountNeeded.parameterName()));
                return;
            }
        }
    }


}