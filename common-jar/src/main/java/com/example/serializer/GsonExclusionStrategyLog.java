package com.example.serializer;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * @author Mahdi Sharifi
 * @version 1.0.0
 * @since 11/11/2019
 * extracted from paypal
 */
//https://howtoprogram.xyz/2016/10/16/ignore-or-exclude-field-in-gson/
public class GsonExclusionStrategyLog implements ExclusionStrategy {

    public boolean shouldSkipField(FieldAttributes f) {
        return (f.getAnnotation(GsonExcludeFieldLog.class) != null) ;
    }

    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }

}