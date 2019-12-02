package com.example.util;

import com.example.dto.ResponseDto;
import com.example.entity.User;
import com.example.error.ResultCode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Mahdi Sharifi
 * @version 1.0.1
 * https://www.linkedin.com/in/mahdisharifi/
 * @since 19/11/2019
 */
public enum AppUtil {
    INSTANCE;
    public static Map<String, User> userMap=new ConcurrentHashMap<>();

    public static int REQUEST_VALID_TIME_WINDOW_SECOND = 6000;//time window

    public static void mapError(ResponseDto responseDto, Language lang) {

    }
}
