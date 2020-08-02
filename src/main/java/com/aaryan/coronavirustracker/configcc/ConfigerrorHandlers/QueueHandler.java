package com.aaryan.coronavirustracker.configcc.ConfigerrorHandlers;

import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

@Component
public class QueueHandler implements ErrorHandler {


    @Override
    public void handleError(Throwable t) {
        System.out.println("error in listner");
        t.printStackTrace();
    }
}
