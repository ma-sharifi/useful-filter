package com.example.error;

/**
 * @author Mahdi Sharifi
 * @version 1.0.1
 * https://www.linkedin.com/in/mahdisharifi/
 * @since 19/11/2019
 */
public enum ResultCode { //Client App used this. you can save this in a database
    SUCCESS(ErrorCode.SUCCESS, null, true),

    INVALID_AUTHENTICATION(ErrorCode.INVALID_AUTHENTICATION, null, true),
    INVALID_REQUEST(ErrorCode.INVALID_REQUEST, null, true),

    INTERNAL_ERROR(ErrorCode.INTERNAL_ERROR, null, true);

    private ErrorCode loweLevel;//will been used by frontend
    private ErrorCode highLevel;//is used by backend(this)
    private Boolean enabled;

    ResultCode(ErrorCode loweLevel, ErrorCode highLevel, Boolean enabled) {
        this.loweLevel = loweLevel;
        this.highLevel = highLevel;
        this.enabled = enabled;
    }
}
