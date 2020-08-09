package com.aaryan.coronavirustracker.Controllers;

import com.aaryan.coronavirustracker.models.LocationStats;
import com.aaryan.coronavirustracker.models.UserModelDto;
import com.aaryan.coronavirustracker.services.CoronaVirusApiCall;
import com.aaryan.coronavirustracker.services.JmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
public class HomeController {

    @Autowired
    private JmsService jmsService;

    @Autowired
    private CoronaVirusApiCall coronaVirusApiCall;


    private UserModelDto userModelDto;

    private String authToken;


    @PostConstruct
    public void postConstruct(){
        this.userModelDto =new UserModelDto();
        this.authToken="random";
    }


    @GetMapping("/")
    public String home(Model model) throws IOException, InterruptedException {

        List<LocationStats> allstats=coronaVirusApiCall.getCovidCases();
        int sum=allstats.stream().mapToInt(stat->stat.getLatestTotalCases()).sum();
        int totalnewCases=allstats.stream().mapToInt(stat->stat.getDiffFromPreviousDay()).sum();

        model.addAttribute("test","test");
        model.addAttribute("locationStats",allstats);
        model.addAttribute("sum",sum);
        model.addAttribute("totalnewCases",totalnewCases);



        return "home";
    }

    @GetMapping("/safetyCheck")
    public String doSafetyCheck(Model model){
        model.addAttribute("userObject",new UserModelDto());
        return "SafetyChecker";
    }

    //todo this part will invoke a mail service as well that will notify the user that has provided details
    //todo it will send details of the weather in xyz pincode region where the user lives
    @GetMapping("/postProcessing")
    public String processResult(@Valid @ModelAttribute("userObject") UserModelDto userModelDto, Errors result, Model model){

        if(result.hasErrors())

            return "SafetyChecker";

        else{
            this.userModelDto = userModelDto;
            //ZIP zip=this.coronaVirusDataService.weatherRestTemplateCall(Integer.parseInt(userModel.getPincode()));


            this.authToken=UUID.randomUUID().toString();

            System.out.println("auth token is: "+this.authToken);
            jmsService.sendObjectUserSaveCommand(userModelDto,this.authToken);


            return "Result";

        }

    }

    @GetMapping("/mailAuthentication")
    public String mailConfirmationChecker(@RequestParam("token") String token,Model model){

        if(token.contentEquals(this.authToken)) {

            return "redirect:/";
        }
        else

            return "Result";


    }



}
