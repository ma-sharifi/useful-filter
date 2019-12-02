package com.example.rest.filter.timewindow;

import com.example.dto.ResponseDto;
import com.example.error.ErrorCode;
import com.example.util.AppUtil;
import com.example.util.HttpConstant;
import com.example.util.HttpHelper;
import com.example.util.Language;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author Mahdi Sharifi
 * @version 1.0.0
 * https://www.linkedin.com/in/mahdisharifi/
 * @since 23/11/2019
 */
@Provider
@Priority(10)
@TimeWindowNeeded
public class TimeWindowFilter implements javax.ws.rs.container.ContainerRequestFilter {
    //http://stackoverflow.com/questions/26777083/best-practice-for-rest-token-based-authentication-with-jax-rs-and-jersey
    private static final Logger LOGGER = Logger.getLogger("apiKeyFilter");

    @Context
    public UriInfo uriInfo;

    @Context
    private ResourceInfo resourceInfo;


    protected TimeWindowNeeded getAnnotation() {
        TimeWindowNeeded timeWindow = resourceInfo.getResourceClass().getAnnotation(TimeWindowNeeded.class);
        if (resourceInfo.getResourceMethod().getAnnotation(TimeWindowNeeded.class) != null) {//Method Annotation Can Override Class Annotation
            timeWindow = resourceInfo.getResourceMethod().getAnnotation(TimeWindowNeeded.class);
        }
        return timeWindow;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        ResponseDto responseDto = new ResponseDto();
        String accountNo= HttpHelper.geParameterValueFromPathParam(requestContext, HttpConstant.ACCOUNT_NO_PATH);
        Language lang= HttpHelper.getLanguageFromQueryParam(requestContext);
//        List langQueryParam = requestContext.getUriInfo().getQueryParameters().get("lang");
        String psuDateHeaderRequest = HttpHelper.geParameterValueFromHeader(requestContext,HttpConstant.PSU_DATE);//2019-01-11T17:04+0330
//        String psuDateHeaderRequest = requestContext.getHeaderString(HttpConstant.PSU_DATE);//2019-01-11T17:04+0330
        long reqTimeDiff=0;
        try {
//            if (!AppUtil_V2.isFullStr.test(psuDateHeaderRequest)) {//TODO because validator filter
//                responseDto.setStatusCode(ErrorCodeEnum.INVALID_DATE.getStatusCode());
//            }
            long requestTime = 0;
            try {
                DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
                DateTime dateTime = parser.parseDateTime(psuDateHeaderRequest);
                requestTime = dateTime.getMillis() / 1000;
            } catch (Exception e) {
                responseDto.setStatusCode(ErrorCode.INVALID_DATE.getStatusCode());
            }

            if (responseDto.getStatusCode() == 0) {

                long nnow = System.currentTimeMillis() / 1000;
                reqTimeDiff = Math.abs((nnow) - requestTime);
                if (reqTimeDiff > getAnnotation().value()) {// AppUtil_V2.REQUEST_VALID_TIME_WINDOW_SECOND
                    responseDto.setStatusCode(ErrorCode.REQUEST_IS_NOT_IN_TIME_WINDOW.getStatusCode());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error("Exception Occured in Time Window: " + ex);
            responseDto.setStatusCode(ErrorCode.REQUEST_IS_NOT_IN_TIME_WINDOW.getStatusCode());
        }
        if (responseDto.getStatusCode() != 0) {
            String currentDateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZ").format(new Date());
            Response.ResponseBuilder builder = createError(responseDto, lang);
            responseDto.setDevMessage("You are "+reqTimeDiff+" second out of window time! windows time is: "+ AppUtil.REQUEST_VALID_TIME_WINDOW_SECOND+" second");
            builder.header("Current-Date", currentDateTime);
            builder.entity(responseDto.toJSON());
            requestContext.abortWith(builder.build());
        }

    }

    private Response.ResponseBuilder createError(ResponseDto responseDto, Language lang) {

        AppUtil.mapError(responseDto, lang);
        LOGGER.error("Request is not in time window " + "Class_Method name: " + resourceInfo.getResourceClass().getName() + "_" + resourceInfo.getResourceMethod().getName() + " response message: " + responseDto);//TODO PRINT HEADERS
        return Response.status(responseDto.getHttpStatus())
                .header("Status-Code", responseDto.getStatusCode());//status code responseDto

    }

}
