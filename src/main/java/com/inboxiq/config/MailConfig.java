package com.inboxiq.config;

import jakarta.mail.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class MailConfig {

    String host = "imap.gmail.com";
    String username = "illimitedingeniouscoders@gmail.com";
    String password = "myxl afor eyih wkub";



    @Bean
    public Session mailSession() {
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        return Session.getDefaultInstance(props, null);
    }
}
