package com.aaryan.coronavirustracker.services;


import com.aaryan.coronavirustracker.Model.IndiaStateCasesModel.IndianStates;
import com.aaryan.coronavirustracker.Model.UserProcessModelDto.UserModelStatsDto;
import com.aaryan.coronavirustracker.models.LocationStats;
import com.aaryan.coronavirustracker.models.UserModelDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


@Component
@Getter
@Setter
public class CoronaVirusApiCall {
    
    private RestTemplate restTemplate;

    private List<LocationStats> casesBufferList=new ArrayList<>();

    public static final String casesApiPath="/covid/data/";

    @Autowired
    private Environment environment;

    public CoronaVirusApiCall(RestTemplateBuilder builder){
        this.restTemplate=builder.build();
    }

    @Scheduled(cron = "0 30 1 * * *")
    public List<LocationStats> getCovidCases(){

        ResponseEntity<List<LocationStats>> caseList=restTemplate
                .exchange("http://"+environment.getProperty("covid.backend.ipaddress")+":8080"+casesApiPath+"casesTable", HttpMethod.GET, null
                        , new ParameterizedTypeReference<List<LocationStats>>() {});

        this.casesBufferList=caseList.getBody();
        return this.casesBufferList;
    }

    public List<IndianStates> getindianStatesData(){

        ResponseEntity<List<IndianStates>> indianStatesList = restTemplate
                .exchange("http://"+environment.getProperty("covid.backend.ipaddress")+":8080" + casesApiPath + "getIndianStateData", HttpMethod.GET, null, new ParameterizedTypeReference<List<IndianStates>>() {});
        return indianStatesList.getBody();
    }

    public UserModelStatsDto getLiveuserRequest(UserModelDto userModel){

        ResponseEntity<UserModelStatsDto> userModelStatsDtoResponseEntity = restTemplate
                .exchange("http://"+environment.getProperty("covid.backend.ipaddress")+":8080"+casesApiPath+"user/"+userModel.getState()+"/"+userModel.getCity(),HttpMethod.GET,null,new ParameterizedTypeReference<UserModelStatsDto>(){});
        //UserModelStatsDto userModelStatsDtoResponseEntity1 =restTemplate.getForObject(URI.create(environment.getProperty("covid.api.url")+casesApiPath+"user/{"+userModel.getState()+"}/{"+userModel.getCity()+"}"),UserModelStatsDto.class);
        return userModelStatsDtoResponseEntity.getBody();
    }




    
}
