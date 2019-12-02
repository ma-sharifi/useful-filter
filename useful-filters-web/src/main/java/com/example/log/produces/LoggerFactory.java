package com.example.log.produces;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * @author Mahdi Sharifi
 * @since 17/11/2019
 * @version 1.0.0
 * https://www.linkedin.com/in/mahdisharifi/
 */

public class LoggerFactory {

    static {
        BasicConfigurator.configure();
    }

    @Produces
    public Logger produceLog(InjectionPoint ip) {
        return Logger.getLogger(ip.getMember().getDeclaringClass().getName());
    }

    @Produces
    @LogType
    public Logger produceCriticalLogger(InjectionPoint injectionPoint) {
        return Logger.getLogger(injectionPoint.getAnnotated().getAnnotation(LogType.class).value().getDesc());
    }

}

