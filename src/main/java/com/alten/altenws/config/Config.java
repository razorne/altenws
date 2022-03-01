package com.alten.altenws.config;

import com.alten.altenws.business.AltenWSService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * This class is the spring config class, as it is declared in the web.xml
 *
 * @author aconti
 */
@Configuration
//enables support for @Controller-annotated classes that use @RequestMapping to map incoming requests to a certain method
@EnableWebMvc
//the packages to be scanned to process annotations
@ComponentScan(basePackages = {"com.alten.altenws.controller", "com.alten.jwtsecurity"})
public class Config {
    @Bean
    public AltenWSService altenWSService() {
        return new AltenWSService();
    }
}
