package com.aaryan.coronavirustracker.services;

import com.aaryan.coronavirustracker.models.LocationStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class CoronaVirusApiCall {
    
    private RestTemplate restTemplate;

    private List<LocationStats> CasesBufferList=new ArrayList<>();

    public static final String casesApiPath="/covid/data/casesTable";

    @Autowired
    private Environment environment;



    public CoronaVirusApiCall(RestTemplateBuilder builder){
        this.restTemplate=builder.build();
    }

    @Scheduled(cron = "* * 1 * * *")
    public List<LocationStats> getCovidCases(){

        ResponseEntity<List<LocationStats>> caseList=restTemplate
                .exchange(environment.getProperty("covid.api.url") + casesApiPath, HttpMethod.GET, null
                        , new ParameterizedTypeReference<List<LocationStats>>() {});

        this.CasesBufferList=caseList.getBody();
        return this.CasesBufferList;
    }


    
}
