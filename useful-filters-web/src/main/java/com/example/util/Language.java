package com.example.util;

/**
 * @author Mahdi Sharifi
 * @version 1.0.0
 * https://www.linkedin.com/in/mahdisharifi/
 * @since 23/11/2019
 */
public enum Language {
    NONE(0),FA(1), EN(2);

    int value;

    Language(int value) {
        this.value = value;
    }

    public static Language valueOf(short v) {
        for (Language language : Language.values()) {
            if (language.getValue() == v)
                return language;
        }
        return NONE;
    }

    public int getValue() {
        return value;
    }
}
