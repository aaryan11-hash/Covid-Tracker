package com.aaryan.coronavirustracker.Controllers;

import com.aaryan.coronavirustracker.models.LocationStats;
import com.aaryan.coronavirustracker.models.UserModel;
import com.aaryan.coronavirustracker.models.weatherApiModel.ZIP;
import com.aaryan.coronavirustracker.services.CoronaVirusDataService;
import com.aaryan.coronavirustracker.services.MailingService;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
public class HomeController {

  @Autowired
  private CoronaVirusDataService coronaVirusDataService;

  @Autowired
  private MailingService mailingService;

  private UserModel userModel;

    private String authToken;



    @PostConstruct
    public void construct(){
        this.authToken=new String();
        this.userModel=new UserModel();

    }

    @GetMapping("/")
    public String home(Model model) throws IOException, InterruptedException {
        List<LocationStats> allstats=coronaVirusDataService.getAllStats();
        int sum=allstats.stream().mapToInt(stat->stat.getLatestTotalCases()).sum();
        int totalnewCases=allstats.stream().mapToInt(stat->stat.getDiffFromPreviousDay()).sum();

        model.addAttribute("test","test");
        model.addAttribute("locationStats",coronaVirusDataService.getAllStats());
        model.addAttribute("sum",sum);
        model.addAttribute("totalnewCases",totalnewCases);



        return "home";
    }

    @GetMapping("/safetyCheck")
    public String doSafetyCheck(Model model){
        model.addAttribute("userObject",new UserModel());
        return "SafetyChecker";
    }

    //todo this part will invoke a mail service as well that will notify the user that has provided details
    //todo it will send details of the weather in xyz pincode region where the user lives
    @GetMapping("/postProcessing")
    public String processResult(@Valid @ModelAttribute("userObject") UserModel userModel, Errors result, Model model){

        if(result.hasErrors())

            return "SafetyChecker";

        else{
            this.userModel=userModel;
            ZIP zip=this.coronaVirusDataService.weatherRestTemplateCall(Integer.parseInt(userModel.getPincode()));

            model.addAttribute("ZIP",zip);
            System.out.println("user email"+this.userModel.getEmail());




            this.authToken=mailingService.sendMail(this.userModel.getEmail());





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
