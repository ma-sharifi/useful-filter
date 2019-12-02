package com.example.rest.listener;

import com.example.entity.Role;
import com.example.entity.User;
import com.example.rest.endpoint.jwt.PasswordUtils;
import com.example.util.AppUtil;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author Mahdi Sharifi
 * @version 1.0.0
 * https://www.linkedin.com/in/mahdisharifi/
 * @since 17/11/2019
 */
@WebListener
public class MyServletContextListener implements ServletContextListener {
    @Inject
    private Logger logger;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("------------------ " + MyServletContextListener.class.getSimpleName() + " Initializing... -------------");
        for (Role role : Role.values()) {
            User user = new User(role.name(), PasswordUtils.digestPassword("123456"));
            user.setRoles(Collections.singletonList(role));
            user.setFirstName("name_"+role);
            AppUtil.userMap.put(user.getUsername(), user);
        }
        AppUtil.userMap.forEach((key,value)-> System.out.println("#user: " + key +" value: " + value));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("------------------ " + MyServletContextListener.class.getSimpleName() + " destroyed -----------------");
    }
}
