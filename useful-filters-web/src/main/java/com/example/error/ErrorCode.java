package com.example.error;

import com.example.dto.ErrorCodeSource;
import com.example.dto.MessageLevel;
import lombok.Getter;

import javax.ws.rs.core.Response;

/**
 * @author Mahdi Sharifi
 * @version 1.0.1
 * https://www.linkedin.com/in/mahdisharifi/
 * @since 19/11/2019
 */
@Getter
public enum ErrorCode { //Backend App use This for generate error
    UNKNOWN(ErrorCodeSource.THIS, Response.Status.EXPECTATION_FAILED, -7, MessageLevel.FATAL, "ناشناخته", "UNKNOWN"),
    SUCCESS(ErrorCodeSource.THIS, Response.Status.OK, 0, null, "موفق", "Success"),

    //----------------- 400-------------
    INVALID_REQUEST(ErrorCodeSource.THIS, Response.Status.BAD_REQUEST, 0, MessageLevel.ERROR, "درخواست نامعتر", "Invalid request"), //400_000
    INVALID_REQUEST_THIRD_PARTY(ErrorCodeSource.THIRD_PARTY, Response.Status.BAD_REQUEST, 1, MessageLevel.ERROR, "درخواست نامعتر", "Invalid request"),
    //    INVALID_SERVICE_PATH(Response.Status.BAD_REQUEST, 1), //400_001
//    INVALID_IP_ADDRESS(Response.Status.BAD_REQUEST, 2),//400_001
//    INVALID_REQUEST_ID(Response.Status.BAD_REQUEST, 3),
    INVALID_DATE(ErrorCodeSource.THIS,Response.Status.BAD_REQUEST,2,MessageLevel.ERROR, "تاریخ نامعتبر است","Invalid Date"),
    REQUEST_IS_NOT_IN_TIME_WINDOW(ErrorCodeSource.THIS,Response.Status.BAD_REQUEST,3,MessageLevel.ERROR, "در پنجره زمانی قرار ندارد","REQUEST_IS_NOT_IN_TIME_WINDOW"),
//    REQUEST_IS_NOT_IN_TIME_WINDOW(Response.Status.BAD_REQUEST, 7),
    //-------------- 401 --------------
    INVALID_AUTHENTICATION(ErrorCodeSource.THIS, Response.Status.UNAUTHORIZED, 1, MessageLevel.ERROR, "خزا در اهراز هوست", "Invalid authentication"),

    //    INVALID_X_API_KEY(Response.Status.UNAUTHORIZED, 4),
//    EMPTY_CLIENT_ID(Response.Status.UNAUTHORIZED, 5),
//    EMPTY_CLIENT_SECRET(Response.Status.UNAUTHORIZED, 6),
//    EMPTY_AUTHORIZATION_PROPERTY(Response.Status.UNAUTHORIZED, 7),
//    INVALID_DIGEST(Response.Status.UNAUTHORIZED, 8);

    //---------- 403 ----------
    FORBIDDEN_ACCESS(ErrorCodeSource.THIS, Response.Status.FORBIDDEN, 0, MessageLevel.ERROR, "شما مجوز دسترسی به این منبع را نئدارید", "You cant access to this resource "),
    //----------------------
    INTERNAL_ERROR(ErrorCodeSource.THIS, Response.Status.INTERNAL_SERVER_ERROR, 0,MessageLevel.ERROR, "خطای داخلی سرور","Server internal error");

    private ErrorCodeSource source;
    private Response.Status httpStatus;
    private int statusCode;
    private MessageLevel messageLevel;
    private String descFa;
    private String descEn;

    ErrorCode(ErrorCodeSource source, Response.Status httpStatus, int statusCode, MessageLevel messageLevel, String descFa, String descEn) {
        this.source = source;
        this.httpStatus = httpStatus;
        this.statusCode = statusCode;
        this.messageLevel = messageLevel;
        this.descFa = descFa;
        this.descEn = descEn;
    }

}
