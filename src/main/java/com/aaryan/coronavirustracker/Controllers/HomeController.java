package com.aaryan.coronavirustracker.Controllers;

import com.aaryan.coronavirustracker.Domain.Token;
import com.aaryan.coronavirustracker.Domain.UserModel;
import com.aaryan.coronavirustracker.Model.LoginUser;
import com.aaryan.coronavirustracker.Repository.TokenRepository;
import com.aaryan.coronavirustracker.models.LocationStats;
import com.aaryan.coronavirustracker.models.UserModelDto;
import com.aaryan.coronavirustracker.services.CoronaVirusApiCall;
import com.aaryan.coronavirustracker.services.JmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@Controller
public class HomeController {

    @Autowired
    private JmsService jmsService;

    @Autowired
    private CoronaVirusApiCall coronaVirusApiCall;

    @Autowired
    private TokenRepository tokenRepository;

    private RestTemplate restTemplate;

    public HomeController(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }


    private UserModelDto userModelDto;

    private String authToken;


    @PostConstruct
    public void postConstruct() {
        this.userModelDto = new UserModelDto();
        this.authToken = "random";
    }


    @GetMapping("/")
    public String home(Model model) throws IOException, InterruptedException {

        List<LocationStats> allstats = coronaVirusApiCall.getCovidCases();
        int sum = allstats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
        int totalnewCases = allstats.stream().mapToInt(stat -> stat.getDiffFromPreviousDay()).sum();

        model.addAttribute("test", "test");
        model.addAttribute("locationStats", allstats);
        model.addAttribute("sum", sum);
        model.addAttribute("totalnewCases", totalnewCases);


        return "home";
    }

    @GetMapping("/safetyCheck")
    public String doSafetyCheck(Model model) {
        model.addAttribute("userObject", new UserModelDto());
        return "SafetyChecker";
    }

    //todo this part will invoke a mail service as well that will notify the user that has provided details
    //todo it will send details of the weather in xyz pincode region where the user lives
    @GetMapping("/postProcessing")
    public String processResult(@Valid @ModelAttribute("userObject") UserModelDto userModelDto, Errors result, Model model) {

        if (result.hasErrors()) {

            return "SafetyChecker";
        } else {
            System.out.println("inside ");
            this.userModelDto = userModelDto;
            //ZIP zip=this.coronaVirusDataService.weatherRestTemplateCall(Integer.parseInt(userModel.getPincode()));


            this.authToken = UUID.randomUUID().toString();

            System.out.println("auth token is: " + this.authToken);
            //jmsService.sendObjectUserSaveCommand(userModelDto,this.authToken);
            userModelDto.setToken(this.authToken);
            this.restTemplate.postForObject(String.valueOf(URI.create("http://localhost:8081/covid/data/postUser")), userModelDto, userModelDto.getClass());


            return "Result";

        }

    }

    @GetMapping("/mailAuthentication")
    public String mailConfirmationChecker(@RequestParam("token") String token, Model model) {

        if (token.contentEquals(this.authToken)) {

            return "redirect:/";
        } else

            return "Result";


    }


    @GetMapping("/login")
    public String loginService(Model model) {

        model.addAttribute("loginuser", LoginUser.builder().build());

        return "Login";
    }

    @GetMapping("/loginCheck")
    public String LoginCheck(@Valid @ModelAttribute LoginUser loginUser, BindingResult result) {
        if (result.hasErrors())
            return "Login";


        else {

            ResponseEntity<List<UserModel>> verification = this.restTemplate.exchange("http://localhost:8081/covid/data/login/user", HttpMethod.GET, null, new ParameterizedTypeReference<List<UserModel>>() {
            });

            for (UserModel userModel : verification.getBody()) {
                if (loginUser.getFirstname().contentEquals(userModel.getFirstName()) && loginUser.getPassword().contentEquals(userModel.getPassword()));
                        return "successlogin";
            }

            return "Login";
        }


    }

}



