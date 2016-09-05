package com.epam.client;

import com.epam.client.conf.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.SimpleCommandLinePropertySource;

/**
 * @author Ahmed_Khan
 */
public class App {
    public static void main(String[] args) {
        SimpleCommandLinePropertySource stringCommandLinePropertySource=new SimpleCommandLinePropertySource(args);
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.getEnvironment().getPropertySources().addFirst(stringCommandLinePropertySource);
        applicationContext.register(AppConfig.class);
        applicationContext.refresh();
        applicationContext.registerShutdownHook();
    }
}
