package com.aaryan.coronavirustracker.services;

import com.aaryan.coronavirustracker.configcc.JmsConfig;
import com.aaryan.coronavirustracker.models.UserModelDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class JmsService {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendObjectUserSaveCommand(UserModelDto userModelDto, String authToken){

        userModelDto.setUuid(authToken);

        try {
            jmsTemplate.convertAndSend(JmsConfig.CORONA_MAIL_MSG,objectMapper.writeValueAsString(userModelDto));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        System.out.println("sending message");;
        /*jmsTemplate.send(JmsConfig.CORONA_MAIL_MSG, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                System.out.println("inside send message block");;
                Message message= null;
                message = session.createObjectMessage(userModel);

                return message;
            }
        }
        );*/


        System.out.println("message sent!!");;

    }



}
