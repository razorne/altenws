package com.alten.altenws.config;

import com.alten.altenws.business.AltenWSService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 *
 * @author aconti
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.alten.altenws.controller", "com.alten.jwtsecurity"})
public class Config {
    @Bean
    public AltenWSService altenWSService() {
        return new AltenWSService();
    }
}
