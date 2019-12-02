package com.example.rest.filter.limit.amount.amount;

import com.example.dto.ResponseDto;
import com.example.rest.filter.limit.amount.amount.model.LimitValueCounter;

import javax.annotation.Priority;
import javax.ws.rs.container.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Mahdi Sharifi
 * @version 1.0.0
 * @since 11/11/2019
 */
//@Provider
@Priority(15)
//@AllowedAmountNeeded
public class AmountAllowedPerTime implements ContainerRequestFilter, ContainerResponseFilter {
    private final long amountLimit;
    private final int timeWindow;
    private final TimeUnit timeUnit;
    private final String parameterName;//account-no

    private static Map<String, LimitValueCounter> cache = new ConcurrentHashMap<>();//you must replace it with Coherence , terracotta , redis in distributed architecture
    private static ThreadLocal<Long> requestTime = ThreadLocal.withInitial(() -> System.currentTimeMillis() / 1000);

    public AmountAllowedPerTime(long amountLimit, int timeWindow, TimeUnit timeUnit, String parameterName) {
        this.amountLimit = amountLimit;
        this.timeWindow = timeWindow;
        this.timeUnit = timeUnit;
        this.parameterName = parameterName;

    }

    public AmountAllowedPerTime() {
        this.amountLimit = 5;
        this.timeWindow = 2;
        this.timeUnit = TimeUnit.MINUTES;
        this.parameterName = "account-no";
    }

    private String getKey(ContainerRequestContext requestContext) {
        return parameterName + ":" + requestContext.getUriInfo().getPathParameters().get(parameterName).get(0);//(account-no , 205660443)
    }

    //if limitValueCounter has reached to amountLimit abort the request
    @Override
    public void filter(ContainerRequestContext requestContext) {
        requestTime.set( System.currentTimeMillis() / 1000);//very important
        long now = requestTime.get();
        ResponseDto responseDto = new ResponseDto(0);
        long amountParameterValue=Long.parseLong(requestContext.getHeaderString("Amount"));
        if (amountParameterValue== 0)//amount is less than expected!
        {
            responseDto.setStatusCode(4060004);
            responseDto.setMessage("#Amount is zero!");
            requestContext.abortWith(Response.status(406)
                    .header("Status-Code", responseDto.getStatusCode())//status code responseDto
                    .entity(responseDto.toJSON())
                    .build());
            return;
        }
        String key = getKey(requestContext);
        LimitValueCounter limitValueCounter = cache.get(key);
        if (limitValueCounter == null || (limitValueCounter != null && limitValueCounter.getExpireAt() < (now))) {
            putToCache(key, now);//initialize cache
        } else {
            checkConditions(now, responseDto, amountParameterValue, limitValueCounter);
            if (responseDto.getStatusCode() != 0) {
                refuseRequest(requestContext, now, responseDto, limitValueCounter);
            }
        }
    }

    //if service response was success, we are going to change amount in the cache
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        long now = requestTime.get();
        String amountParameterValue = requestContext.getHeaderString("Amount");
        String key=getKey(requestContext);
        LimitValueCounter limitValueCounter = cache.get(key);
        if (responseContext.getStatus() == Response.Status.OK.getStatusCode()) {//you must do this block in cache processor!
            long amountRemain = limitValueCounter.getRemain() - Long.parseLong(amountParameterValue);
            limitValueCounter.setRemain(amountRemain);
            cache.put(key, limitValueCounter);
            responseContext.getHeaders().add("Amount-Remain-Counter", amountRemain);//add to header
        }
        if (limitValueCounter != null && limitValueCounter.getExpireAt() < (now))//time is up
        {
            putToCache(key, now);//rest initialize cache
        }
    }

    private void putToCache(String key, long now) {
        long expireAt = timeUnit.toSeconds(timeWindow) + now;//convert time window to second
        LimitValueCounter amountLimitValue = new LimitValueCounter(amountLimit, expireAt);
        System.out.println("#amountLimitValue: "+amountLimitValue);
        cache.put(key, amountLimitValue);
    }

    private void checkConditions(long now, ResponseDto responseDto, long amountParameterValue, LimitValueCounter limitValueCounter) {
        if (limitValueCounter.getRemain() <= 0)//amount is up
        {
            responseDto.setStatusCode(4060002);
            responseDto.setMessage("#Amount is up, current amount is: " + limitValueCounter.getRemain() + " you must wait for " + (limitValueCounter.getExpireAt() - now) + " s.");
        }
        if ((limitValueCounter.getRemain() - amountParameterValue) < 0)//10-2
        {
            responseDto.setStatusCode(4060003);
            responseDto.setMessage("#Amount remaining: " + limitValueCounter.getRemain() + " you must wait for " + (limitValueCounter.getExpireAt() - now) + " s.");
        }
    }



    private void refuseRequest(ContainerRequestContext requestContext, long now, ResponseDto responseDto, LimitValueCounter limitValueCounter) {
        requestContext.abortWith(Response.status(406)
                .header("Status-Code", responseDto.getStatusCode())//status code responseDto
                .header("Amount-Remain-Timeout", (limitValueCounter.getExpireAt() - now))
                .header("Amount-Remain-Counter", limitValueCounter.getRemain())
                .entity(responseDto.toJSON())
                .build());
    }

}


