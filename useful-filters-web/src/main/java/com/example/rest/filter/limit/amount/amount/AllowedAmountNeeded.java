package com.example.rest.filter.limit.amount.amount;


import com.example.rest.filter.limit.ParameterType;

import javax.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author Mahdi Sharifi
 * @version 1.0.0
 * @since 11/11/2019
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@NameBinding
public @interface AllowedAmountNeeded {
    long amountLimit() default 10;

    int timeWindow() default 2;

    TimeUnit timeUnit() default TimeUnit.MINUTES;

    ParameterType parameterType() default ParameterType.PATH_PARAM;

    String parameterName() default "account-no";

    ParameterType amountParameter() default ParameterType.HEADER;

    String amountParameterName() default "Amount";
}