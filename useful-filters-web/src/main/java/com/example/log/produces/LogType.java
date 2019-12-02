package com.example.log.produces;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({FIELD, METHOD, TYPE})
public @interface LogType {

    @Nonbinding
    LoggerType value() default LoggerType.TRANSACTIONS;

    enum LoggerType {
        SERVICE_TIMING(5, "serviceTiming"),
        STACK_TRACE(6, "stackTrace"),
        TRANSACTIONS(7, "transactions");

        int value;
        String desc;

        LoggerType(int value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        public int getValue() {
            return value;
        }

        public String getDesc() {
            return desc;
        }
    }
}
