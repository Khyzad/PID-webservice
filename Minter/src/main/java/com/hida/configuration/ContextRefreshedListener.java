package com.hida.configuration;

import com.hida.controller.MinterController;
import com.hida.service.MinterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
 
/**
 * 
 * @author lruffin
 */
@Component
public class ContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent>{      
    
    @Autowired
    MinterService Service;
    
    /* 
     * Logger; logfile to be stored in resource folder    
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ContextRefreshedListener.class);
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try{
        Service.generateCache();
        }catch(Exception exception){
            LOGGER.error("Exception caught during context refresh", exception);
        }
    }
}
