package com.aaryan.coronavirustracker.configcc;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;
import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

    @Value("${server.adminMailSender.email}")
    private static String EMAIL_UTILITY_MAIL_SENDER;

    @Value("${server.adminMailSender.password}")
    private static String EMAIL_UTILITY_MAIL_PASSWORD;

    @Bean
    public JavaMailSender getJavaMailSender()
    {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(25);


        mailSender.setUsername(EMAIL_UTILITY_MAIL_SENDER);
        mailSender.setPassword("9890010090maa");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        mailSender.setJavaMailProperties(props);
        return mailSender;
    }



}
