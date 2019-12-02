//package com.example.rest.filter.limit.amount.amount;
//
//import com.example.dto.ResponseDto;
//import com.example.rest.filter.limit.amount.amount.model.LimitValueCounter;
//
//import javax.ws.rs.container.*;
//import javax.ws.rs.core.Context;
//import javax.ws.rs.core.Response;
//import javax.ws.rs.ext.Provider;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * @author Mahdi Sharifi
// * @version 1.0.0
// * @since 11/11/2019
// */
////@Provider
////@AllowedAmountNeeded
//public class AllowedAmountPerTime implements ContainerRequestFilter, ContainerResponseFilter {
//
//    @Context
//    private ResourceInfo resourceInfo;
//
//    private static Map<String, LimitValueCounter> cache = new ConcurrentHashMap<>();//you must replace it with Coherence , terracotta , redis in distributed architecture
//
//    private static ThreadLocal<Long> requestTime = ThreadLocal.withInitial(() -> System.currentTimeMillis() / 1000);
//
//    private String getKey(ContainerRequestContext requestContext) {
//        String keyParameterValue = requestContext.getUriInfo().getPathParameters().get(getAnnotation().parameterName()).get(0);
//        return getAnnotation().parameterName() + ":" + keyParameterValue;//(account-no , 205660443)
//    }
//
//    @Override
//    public void filter(ContainerRequestContext requestContext) {
//        Runnable thread;
//        long now = requestTime.get();
//        ResponseDto responseDto = new ResponseDto();
//        String key=getKey(requestContext);
//        String valueParameterValue = requestContext.getHeaderString(getAnnotation().amountParameterName());
//        LimitValueCounter limitValue = cache.get(key);
//        if (limitValue == null) {
//            putToCache(key, now);
//        } else {
//            checkConditions(now, responseDto, valueParameterValue, limitValue);
//            if (responseDto.getStatusCode() != 0) {
//                requestContext.abortWith(Response.status(406)
//                        .header("Status-Code", responseDto.getStatusCode())//status code responseDto
//                        .header("Amount-Remain-Timeout", limitValue.getExpireAt() - now)
//                        .entity(responseDto.toJSON())
//                        .build());
//            }
//        }
//    }
//
//    @Override
//    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
//        long now = requestTime.get();
//        String amountParameterValue = requestContext.getHeaderString(getAnnotation().amountParameterName());
//        String key=getKey(requestContext);
//        LimitValueCounter limitValue = cache.get(key);
//        if (responseContext.getStatus() == Response.Status.OK.getStatusCode()) {//you must do this block in cache processor!
//            long amountRemain = limitValue.getRemain() - Long.parseLong(amountParameterValue);
//            limitValue.setRemain(amountRemain);
//            cache.put(key, limitValue);
//            responseContext.getHeaders().add("Amount-Remain", amountRemain);
//        }
//        if (limitValue != null && limitValue.getExpireAt() < (now))//time is up
//        {
//            putToCache(key, now);
//        }
//    }
//
//    private LimitValueCounter putToCache(String key, long now) {
//        long expireAt = getAnnotation().timeUnit().toSeconds(getAnnotation().timeWindow()) + now;//convert time window to second
//        LimitValueCounter amountLimitValue = new LimitValueCounter(getAnnotation().amountLimit(), expireAt);
//        cache.put(key, amountLimitValue);
//        return amountLimitValue;
//    }
//
//    private AllowedAmountNeeded getAnnotation() {
//        AllowedAmountNeeded annotation = resourceInfo.getResourceClass().getAnnotation(AllowedAmountNeeded.class);
//        if (resourceInfo.getResourceMethod().getAnnotation(AllowedAmountNeeded.class) != null) {
//            annotation = resourceInfo.getResourceMethod().getAnnotation(AllowedAmountNeeded.class);
//        }
//        return annotation;
//    }
//
//    private void checkConditions(long now, ResponseDto responseDto, String amountParameterValue, LimitValueCounter limitValue) {
//        if (limitValue.getRemain() <= 0)//amount is up
//        {
//            responseDto.setStatusCode(4060002);
//            responseDto.setMessage("#Amount is up, current amount is: " + limitValue.getRemain() + " you must wait for " + (limitValue.getExpireAt() - now) + " s.");
//        }
//        if ((limitValue.getRemain() - Long.parseLong(amountParameterValue)) < 0)//10-2
//        {
//            responseDto.setStatusCode(4060003);
//            responseDto.setMessage("#Amount remaining: " + limitValue.getRemain() + " you must wait for " + (limitValue.getExpireAt() - now) + " s.");
//        }
//    }
////    private Response.ResponseBuilder createError(LanguageEnum lang) {
////        ResponseDto responseDto = new ResponseDto();
////        responseDto.setStatusCode(ErrorCodeEnum.INVALID_DIGEST.getStatusCode());
////        AppUtil.mapError(responseDto, lang);
////        LOGGER.error("OA: Aborted Request when call DigestNeededFilter " + "Class_Method name: " + resourceInfo.getResourceClass().getName() + "_" + resourceInfo.getResourceMethod().getName() + " response message: " + responseDto + " ,Header: " + httpHeaders.getRequestHeaders());//TODO PRINT HEADERS
////        return Response.status(responseDto.getHttpStatus())
////                .header(HttpConstant.STATUS_CODE, responseDto.getStatusCode())//status code responseDto
////                .entity(responseDto.toJSON());
////    }
//
//}
//
