package com.example.util;

import javax.ws.rs.core.Response;

import static javax.ws.rs.Priorities.AUTHENTICATION;
import static javax.ws.rs.Priorities.AUTHORIZATION;

/**
 * Created by me-sharifi on 10/21/2017.
 */
public class HttpConstant {
    public static final String AUTHORIZATION_PROPERTY = "Authorization";
    public static final String AUTHENTICATION_SCHEME_BEARER = "Bearer";
    public static final String AUTHENTICATION_SCHEME_BASIC = "Basic";


    public static final String X_API_KEY = "X-API-Key";
    public static final String DIGEST = "Digest";
    public static final String X_REQUEST_ID = "X-Request-ID";
    public static final String REFERENCE_NO = "Reference-NO";
    public static final String TRANSFER_ID = "Transfer-ID";
    public static final String TRANSFER_TYPE = "Transfer-Type";
    public static final String SMS_PASSWORD = "SMS-Pass";

    public static final String CLIENT_SECRET = "Client-Secret";
    public static final String CLIENT_ID = "Client-ID";
    public static final String AMOUNT = "Amount";
    public static final String PAYMENT_ID = "Payment-ID";


    public static final String PSU_DEVICE_ID = "PSU-Device-ID";
    public static final String PSU_USER_AGENT = "PSU-User-Agent";
    public static final String PSU_IP_ADDRESS = "PSU-IP-Address";
    public static final String PSU_DATE = "PSU-Date";
    public static final String PSU_DEVICE_OS = "PSU-Device-OS";
    public static final String PSU_DEVICE_OS_VERSION = "PSU-Device-OS-Version";
    public static final String TPP_IP_ADDRESS = "TPP-IP-Address";

    public static final String STATUS_CODE = "Status-Code";

    //PATH PARAM
    public static final String ACCOUNT_NO_PATH = "account-no";
    public static final String IBAN_NO_PATH = "iban-no";
    public static final String CARD_NO_PATH = "card-no";
    public static final String ACCOUNT_NO_DESTINATION_PATH = "destination-account-no";


    //QUERY STRING
    public static final String LANGUAGE_QUERY = "lang";

    //SERVLET ATTRIBUTE
    public static final String OAUTH_REQUEST_ATTRIBUTE = "OAUTH_REQUEST";
    public static final String OAUTH_RESPONSE_ATTRIBUTE = "OAUTH_RESPONSE";
    public static final String SERVER_REQUEST_COUNTER_ATTRIBUTE = "SERVER_REQUEST_COUNTER";
    /**
     * Security authentication filter/interceptor priority.
     */

    private static final Response ACCESS_DENIED = Response.status(Response.Status.UNAUTHORIZED)
            .entity("You cannot access this resource").build();

//    public static String addMessage(HttpHeaders headers, ResponseMessage message) {
//        String messageListStr = headers.getHeaderString(HttpConstant.MESSAGE_LIST);
//        List<ResponseMessage> messageList = new ArrayList<>();
//        ResponseMessage[] messageArray = null;
//        if (messageListStr != null && messageListStr.length() > 0 && JSONFormatter.isJsonValid(messageListStr)) {
//            messageArray = JSONFormatter.fromJSON(messageListStr, ResponseMessage[].class);
//        }
//        if (messageArray != null) {
//            for (ResponseMessage responseMessage : messageArray) {
//                messageList.add(responseMessage);
//            }
//        }
//        messageList.add(message);
//        return JSONFormatter.toJSON(messageList);
//    }

    //
//    public static String addMessage(ContainerRequestContext requestContext, ResponseMessage message) {
//        String messageListStr = requestContext.getHeaderString(HttpConstant.MESSAGE_LIST);
//
//        List<ResponseMessage> messageList = null;
//        if (messageListStr == null || messageListStr.isEmpty()) {
//            messageList = new ArrayList<>();
//        } else {
//            ResponseMessage[] messageArray = JSONFormatter.fromJSON(messageListStr, ResponseMessage[].class);
//            for (ResponseMessage responseMessage : messageArray) {
//                messageList.add(responseMessage);
//            }
//            requestContext.getHeaders().remove(HttpConstant.MESSAGE_LIST);
//        }
//        messageList.add(message);
//
//        return JSONFormatter.toJSON(messageList);
//    }

//    public static String addMessage(ContainerResponseContext responseContext, ResponseMessage message) {
//        String messageListStr = responseContext.getHeaderString(HttpConstant.MESSAGE_LIST);
//
//        List<ResponseMessage> messageList = null;
//        if (messageListStr == null || messageListStr.isEmpty()) {
//            messageList = new ArrayList<>();
//        } else {
//            ResponseMessage[] messageArray = JSONFormatter.fromJSON(messageListStr, ResponseMessage[].class);
//            for (ResponseMessage responseMessage : messageArray) {
//                messageList.add(responseMessage);
//            }
//            responseContext.getHeaders().remove(HttpConstant.MESSAGE_LIST);
//        }
//        messageList.add(message);
//
//        return JSONFormatter.toJSON(messageList);
//    }

    //https://stackoverflow.com/questions/26777083/best-practice-for-rest-token-based-authentication-with-jax-rs-and-jersey
//    public static void abortWithBadRequest(ContainerRequestContext requestContext, ResponseMessage message) {// Abort the filter chain with a 401 status code
//        // The "WWW-Authenticate" header is sent along with the response
//        String messageList = addMessage(requestContext, message);
//        requestContext
//                .abortWith(Response
//                        .status(Response.Status.BAD_REQUEST)
//                        .header(HttpConstant.MESSAGE_LIST, messageList)
//                        .entity("")
//                        .build());
//    }
    //https://stackoverflow.com/questions/26777083/best-practice-for-rest-token-based-authentication-with-jax-rs-and-jersey
//    public static void abortWithBadRequest(ContainerRequestContext requestContext) {
//        ResponseMessage message = new ResponseMessage();
//        // Abort the filter chain with a 401 status code
//        // The "WWW-Authenticate" header is sent along with the response
//        message.setStatus(ResponseStatusEnum.InvalidRequest.getValue());
//        requestContext
//                .abortWith(Response
//                        .status(Response.Status.BAD_REQUEST)
//                        .header(HttpConstant.MESSAGE_RESPONSE, message.toJSON())
//                        .entity("")
//                        .build());
//    }

    //https://stackoverflow.com/questions/26777083/best-practice-for-rest-token-based-authentication-with-jax-rs-and-jersey
//    public static void abortWithUnauthorized(ContainerRequestContext requestContext) {
//        ResponseMsgDto message = new ResponseMsgDto();
//        // Abort the filter chain with a 401 status code
//        // The "WWW-Authenticate" header is sent along with the response
//        message.setStatusCode(1001502);
//        //----------------
//        String messageList = addMessage(requestContext, message);
////        message.setStatus(2000110);
//        requestContext
//                .abortWith(Response
//                        .status(Response.Status.UNAUTHORIZED)
//                        .header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME)
//                        .header(HttpConstant.MESSAGE_LIST, messageList)
//                        .entity("")
//                        .build());
//    }
    private static final Response ACCESS_FORBIDDEN = Response.status(Response.Status.FORBIDDEN)
            .entity("Access blocked for all users !!").build();

    //-------------------------------OA Filter Priorities-----------------------------------
    public static final int FILTER_VALIDATOR = AUTHENTICATION + 1;
    public static final int FILTER_LOG = AUTHENTICATION + 2;
    public static final int FILTER_TIME_WINDOW = AUTHENTICATION + 3;
    public static final int FILTER_APIKEY = AUTHENTICATION + 4;
    public static final int FILTER_IDEMPOTENT = AUTHENTICATION + 5;
    public static final int FILTER_DIGEST = AUTHENTICATION + 6;
    public static final int FILTER_RATE_CONTROL = AUTHENTICATION + 7;
    public static final int FILTER_OAUTH = AUTHORIZATION + 1;






    //    public static void abortWithBadRequest1(int status,ContainerRequestContext requestContext) {
//        ResponseMessage message = new ResponseMessage();
//        // Abort the filter chain with a 401 status code
//        // The "WWW-Authenticate" header is sent along with the response
//        message.setStatus(ResponseStatusEnum.InvalidRequest.getValue());
//        requestContext
//                .abortWith(Response
//                        .status(Response.Status.BAD_REQUEST)
//                        .header(HttpConstant.MESSAGE_RESPONSE, message.toJSON())
//                        .entity("")
//                        .build());
//    }
    // Check if the HTTP Authorization header is present and formatted correctly
    public static boolean isTokenBasedAuthentication(String authorizationHeader) {
        // Check if the Authorization header is valid
        // It must not be null and must be prefixed with "Bearer" plus a whitespace
        // Authentication scheme comparison must be case-insensitive
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                .startsWith(AUTHENTICATION_SCHEME_BEARER.toLowerCase() + " ");
    }


}
