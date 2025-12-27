package com.app.demo.Utils;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
public class DateFormat {
    @Bean
    public String getDate(){
        java.util.Date currentDate = new java.util.Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-dd HH:mm:ss");
        return formatter.format(currentDate);
    }
}