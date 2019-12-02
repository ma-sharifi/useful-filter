package com.example.dto;

/**
 * Created by me-sharifi on 10/4/2017.
 */
public enum MessageLevel {//+
    NONE("NONE", "NONE", 0),
    FATAL("خطای مهلک", "FATAL", 1),
    ERROR("خطا", "ERROR", 2),//500 , 400 , 401, 403
    WARN("هشدار", "WARN", 3),
    INFO("اطلاعات", "INFO", 4);//200

    String discFa;
    String discEn;
    int value;

    MessageLevel(String discFa, String discEn, int value) {
        this.discFa = discFa;
        this.discEn = discEn;
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    public static MessageLevel valueOf(int v) {
        for(MessageLevel o : MessageLevel.values()) {
            if(o.getValue() == v)
                return o;
        }
        return NONE;
    }
}
