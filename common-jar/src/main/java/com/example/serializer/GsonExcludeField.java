package com.example.serializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Mahdi Sharifi
 * @version 1.0.0
 * @since 11/11/2019
 * extracted from paypal
 */
//https://howtoprogram.xyz/2016/10/16/ignore-or-exclude-field-in-gson/

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface GsonExcludeField {
}
