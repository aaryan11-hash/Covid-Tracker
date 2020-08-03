package com.aaryan.coronavirustracker.services;

import com.aaryan.coronavirustracker.configcc.JmsConfig;
import com.aaryan.coronavirustracker.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class JmsService {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendObjectUserSaveCommand(UserModel userModel){

        jmsTemplate.convertAndSend(JmsConfig.CORONA_MAIL_MSG,userModel);

    }



}
