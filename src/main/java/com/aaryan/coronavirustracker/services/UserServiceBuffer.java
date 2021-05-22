package com.aaryan.coronavirustracker.services;

import com.aaryan.coronavirustracker.models.UserSessionStore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.PostConstruct;
import javax.persistence.Column;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.HashSet;

@Service
@Data
@Getter
@Setter
@Slf4j
@Component(value = "singleton")
public class UserServiceBuffer {

    private HashMap<String,Integer> mailconfirmationTable;
    private HashMap<String, UserSessionStore> activeIpAddresses;


    @PostConstruct
    public void initializeMethod(){
        this.mailconfirmationTable = new HashMap<>();
        this.activeIpAddresses = new HashMap<>();

    }

    public String getClientIp(HttpServletRequest request) {

        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        return remoteAddr;
    }

    @Scheduled(fixedDelay = 10000)
    public void resetUserLoginAccessFeature(){
        this.getActiveIpAddresses().values().stream()
                .forEach(this::checkUserLoginLimitStatus);
    }

    public void checkUserLoginLimitStatus(UserSessionStore userSessionStore){
        Long prevLoginTime = userSessionStore.getPrevTimeOfLoginCheck();
        Long currentTime = System.currentTimeMillis();
        if(currentTime-prevLoginTime>=120000 && userSessionStore.getLoginAccessCount()==3) {
            userSessionStore.setLoginAccessCount(0);
            userSessionStore.setPrevTimeOfLoginCheck(currentTime);
            log.info("user"+ userSessionStore.getEmailList()+"granted access");
        }
    }
}
