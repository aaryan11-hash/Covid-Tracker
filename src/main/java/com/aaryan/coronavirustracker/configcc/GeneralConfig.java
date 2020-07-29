package com.aaryan.coronavirustracker.configcc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAsync
@EnableScheduling
public class GeneralConfig {

    @Bean
    public TaskExecutor taskExecutor(){

        return new SimpleAsyncTaskExecutor();
    }
}
