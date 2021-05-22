package com.aaryan.coronavirustracker.Controllers;

import com.aaryan.coronavirustracker.Model.LoginUser;
import com.aaryan.coronavirustracker.Model.TempUserInfo;
import com.aaryan.coronavirustracker.Model.UserProcessModelDto.UserModelStatsDto;
import com.aaryan.coronavirustracker.models.LocationStats;
import com.aaryan.coronavirustracker.models.UserModelDto;
import com.aaryan.coronavirustracker.models.UserSessionStore;
import com.aaryan.coronavirustracker.services.CoronaVirusApiCall;
import com.aaryan.coronavirustracker.services.UserServiceBuffer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
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
import javax.servlet.http.HttpServletRequest;
import javax.sound.midi.Soundbank;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
@Slf4j
public class HomeController {

    @Autowired
    private CoronaVirusApiCall coronaVirusApiCall;

    @Autowired
    private UserServiceBuffer userServiceBuffer;

    @Autowired
    private Environment environment;

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
    public String home(Model model,HttpServletRequest request) throws IOException, InterruptedException {
        List<LocationStats> allstats;
        if(coronaVirusApiCall.getCasesBufferList().isEmpty()==false) {
            allstats = coronaVirusApiCall.getCasesBufferList();
            log.info("Cases buffer reused");
        }
        else {
            allstats = coronaVirusApiCall.getCovidCases();
            log.info("API call made for data");
        }
        int sum = allstats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
        int totalnewCases = allstats.stream().mapToInt(stat -> stat.getDiffFromPreviousDay()).sum();

//        String ip = this.userServiceBuffer.getClientIp(request);
//        if(this.userServiceBuffer.getActiveIpAddresses().containsKey(ip))
//                this.userServiceBuffer.getActiveIpAddresses().remove(ip);

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

    @GetMapping("/safetyCheckFail")
    public String userCredentialsExist(Model model){
        model.addAttribute("userObject",new UserModelDto());
        return "UserExists";
    }
    //todo this part will invoke a mail service as well that will notify the user that has provided details

    @GetMapping("/postProcessing")
    public String processResult(@Valid @ModelAttribute("userObject") UserModelDto userModelDto, Errors result, Model model) {

        int ThreadCount = Runtime.getRuntime().availableProcessors();
        ExecutorService service = Executors.newFixedThreadPool(ThreadCount);

    if (result.hasErrors()) {

            return "SafetyChecker";
        } else {
            RestTemplate restTemplate =this.restTemplate;
            this.userModelDto = userModelDto;

            System.out.println(userModelDto);


        ResponseEntity<TempUserInfo> tempUserInfo = restTemplate.exchange("http://"+environment.getProperty("covid.backend.ipaddress")+":8080/account/postUser",HttpMethod.POST,new HttpEntity<>(userModelDto),TempUserInfo.class);

        if(tempUserInfo.getBody().getUUID()=="NULL"&&tempUserInfo.getBody().getId()==Integer.MAX_VALUE)
        {
            return "redirect:/safetyCheckFail";
        }

        userServiceBuffer.getMailconfirmationTable().put(tempUserInfo.getBody().getUUID(),tempUserInfo.getBody().getId());



            return "Result";
    }

    }

    @GetMapping("/mailAuthentication")
    public String mailConfirmationChecker(@RequestParam("token") String token, Model model) {

        if (userServiceBuffer.getMailconfirmationTable().containsKey(token)) {
            //log.info("inside verification if block");
           String responseEntity = restTemplate.postForObject(String.valueOf(URI.create("http://"+environment.getProperty("covid.backend.ipaddress")+":8080/account/certifyUser/"
                    +userServiceBuffer.getMailconfirmationTable().get(token)))
                    , new ParameterizedTypeReference<String>(){},String.class);
            userServiceBuffer.getMailconfirmationTable().remove(token);
            System.out.println(responseEntity);
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
    public String LoginCheck(@Valid @ModelAttribute LoginUser loginUser, Errors result, Model model, HttpServletRequest request) {

        if (result.hasErrors()) {
            model.addAttribute("loginuser", LoginUser.builder().build());
            return "failedlogin";
        }

        else {

            ResponseEntity<List<UserModelDto>> verification = this.restTemplate.exchange("http://"+environment.getProperty("covid.backend.ipaddress")+":8080/account/login/user", HttpMethod.GET, null, new ParameterizedTypeReference<List<UserModelDto>>() {
            });
            String clientIp = userServiceBuffer.getClientIp(request);

            for (UserModelDto userModel : verification.getBody()) {
                if (loginUser.getFirstname().contentEquals(userModel.getEmail()) && loginUser.getPassword().contentEquals(userModel.getPassword())) {

                    if(userServiceBuffer.getActiveIpAddresses().containsKey(clientIp)==false) {
                        UserModelStatsDto temp = this.coronaVirusApiCall.getLiveuserRequest(userModel);
                        userServiceBuffer.getActiveIpAddresses().put(clientIp,new UserSessionStore(new HashSet<String>(Collections.singleton(loginUser.getFirstname())),1,System.currentTimeMillis()));
                        log.info("first time request made by this ip");
                        model.addAttribute("data", temp);
                        return "successlogin";
                    }
                    else if(userServiceBuffer.getActiveIpAddresses().containsKey(clientIp)
                            && userServiceBuffer.getActiveIpAddresses().get(clientIp).getEmailList().contains(loginUser.getFirstname())==false){
                        log.info("2 different emailids logged in from the same ip");
                        userServiceBuffer.getActiveIpAddresses().get(clientIp).getEmailList().add(loginUser.getFirstname());
                        UserModelStatsDto temp = this.coronaVirusApiCall.getLiveuserRequest(userModel);
                        model.addAttribute("data", temp);
                        return "successlogin";
                    }
                    else if(userServiceBuffer.getActiveIpAddresses().containsKey(clientIp)
                        && userServiceBuffer.getActiveIpAddresses().get(clientIp).getEmailList().contains(loginUser.getFirstname())==true){
                            log.info("this ip has visited before");
                        if(userServiceBuffer.getActiveIpAddresses().get(clientIp).getLoginAccessCount()==3){
                            log.info("user exceeded its 3 time request limit");
                            userServiceBuffer.getActiveIpAddresses().get(clientIp).setPrevTimeOfLoginCheck(System.currentTimeMillis());
                            return "ExhaustedLoginCalls";
                        }
                        else{

                            int val = userServiceBuffer.getActiveIpAddresses().get(clientIp).getLoginAccessCount();
                            userServiceBuffer.getActiveIpAddresses().get(clientIp).setLoginAccessCount(val+1);
                            userServiceBuffer.getActiveIpAddresses().get(clientIp).setPrevTimeOfLoginCheck(System.currentTimeMillis());
                            UserModelStatsDto temp = this.coronaVirusApiCall.getLiveuserRequest(userModel);
                            model.addAttribute("data", temp);
                            log.info("same ip requested for data. "+(val+1));
                            return "successlogin";
                        }
                    }
                }

            }




        }

        return "redirect:/failedLogin";
    }

    @GetMapping("/failedLogin")
    public String loginFailed(Model model){
        model.addAttribute("loginuser", LoginUser.builder().build());
        return "failedlogin";
    }

    @GetMapping("/APISource")
    public String getApiSourcePage(){
        return "APIs";
    }

}



