package com.aaryan.coronavirustracker.configcc;


import com.aaryan.coronavirustracker.configcc.ConfigerrorHandlers.QueueHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.SimpleJmsListenerContainerFactory;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.ConnectionFactory;

@Configuration
@EnableJms
public class JmsConfig {

    @Autowired
    private Environment environment;



    public static final String CORONA_MAIL_MSG="mail-send-stimulus";

    @Bean
    public ConnectionFactory connectionFactory(){

        ActiveMQConnectionFactory connectionFactory=new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(environment.getProperty("activemq.broker-url"));
        connectionFactory.setUserName(environment.getProperty("activemq.username"));
        connectionFactory.setPassword(environment.getProperty("activemq.password"));


        connectionFactory.setTrustAllPackages(true);
        return connectionFactory;
    }

    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerFactory(@Qualifier("connectionFactory") ActiveMQConnectionFactory connectionFactory
            , QueueHandler queueHandler){
        SimpleJmsListenerContainerFactory containerFactory=new SimpleJmsListenerContainerFactory();
        containerFactory.setConnectionFactory(connectionFactory);
        containerFactory.setErrorHandler(queueHandler);
        return containerFactory;
    }

    @Bean
    public JmsTemplate jmsTemplate(){

        return new JmsTemplate(connectionFactory());
    }

    @Bean
    public ObjectMapper objectMapper(){

        return new ObjectMapper();
    }

}
