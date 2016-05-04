package com.hida.configuration;

import org.h2.server.web.WebServlet;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Acting as a dispatcher.xml, this class resolves requests into view names. 
 *
 * @author lruffin
 */
@Configuration
@ComponentScan(basePackages = "com.hida")
public class AppConfig {

    @Bean
    ServletRegistrationBean h2servletRegistration(){
        ServletRegistrationBean registrationBean = new ServletRegistrationBean( new WebServlet());
        registrationBean.addUrlMappings("/Resolver/console/*");
        return registrationBean;
    }
}
