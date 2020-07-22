package com.aaryan.coronavirustracker.Controllers;

import com.aaryan.coronavirustracker.models.LocationStats;
import com.aaryan.coronavirustracker.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
    public String home(Model model) throws IOException, InterruptedException {
        List<LocationStats> allstats=coronaVirusDataService.getAllStats();
        int sum=allstats.stream().mapToInt(stat->stat.getLatestTotalCases()).sum();
        int totalnewCases=allstats.stream().mapToInt(stat->stat.getDiffFromPreviousDay()).sum();

        model.addAttribute("test","test");
        model.addAttribute("locationStats",coronaVirusDataService.getAllStats());
        model.addAttribute("sum",sum);
        model.addAttribute("totalnewCases",totalnewCases);

        //coronaVirusDataService.weatherStatusCall();
        coronaVirusDataService.weatherRestTemplateCall();
        return "home";
    }



}
