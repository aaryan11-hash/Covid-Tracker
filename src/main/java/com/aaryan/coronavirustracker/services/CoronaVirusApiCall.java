package com.aaryan.coronavirustracker.services;

import com.aaryan.coronavirustracker.Domain.UserModel;
import com.aaryan.coronavirustracker.Model.IndiaStateCasesModel.IndianStates;
import com.aaryan.coronavirustracker.Model.UserProcessModelDto.UserModelStatsDto;
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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;


@Component
public class CoronaVirusApiCall {
    
    private RestTemplate restTemplate;

    private List<LocationStats> CasesBufferList=new ArrayList<>();

    public static final String casesApiPath="/covid/data/";

    @Autowired
    private Environment environment;

    public CoronaVirusApiCall(RestTemplateBuilder builder){
        this.restTemplate=builder.build();
    }

    @Scheduled(fixedRate = 10000)
    public List<LocationStats> getCovidCases(){

        ResponseEntity<List<LocationStats>> caseList=restTemplate
                .exchange(environment.getProperty("covid.api.url") + casesApiPath+"casesTable", HttpMethod.GET, null
                        , new ParameterizedTypeReference<List<LocationStats>>() {});

        this.CasesBufferList=caseList.getBody();
        return this.CasesBufferList;
    }

    public List<IndianStates> getindianStatesData(){

        ResponseEntity<List<IndianStates>> indianStatesList = restTemplate
                .exchange(environment.getProperty("covid.api.url") + casesApiPath + "getIndianStateData", HttpMethod.GET, null, new ParameterizedTypeReference<List<IndianStates>>() {});
        return indianStatesList.getBody();
    }

    public UserModelStatsDto getLiveuserRequest(UserModel userModel){

        ResponseEntity<UserModelStatsDto> userModelStatsDtoResponseEntity = restTemplate
                .exchange(environment.getProperty("covid.api.url")+casesApiPath+"user/"+userModel.getState()+"/"+userModel.getCity(),HttpMethod.GET,null,new ParameterizedTypeReference<UserModelStatsDto>(){});
        //UserModelStatsDto userModelStatsDtoResponseEntity1 =restTemplate.getForObject(URI.create(environment.getProperty("covid.api.url")+casesApiPath+"user/{"+userModel.getState()+"}/{"+userModel.getCity()+"}"),UserModelStatsDto.class);
        return userModelStatsDtoResponseEntity.getBody();
    }




    
}
