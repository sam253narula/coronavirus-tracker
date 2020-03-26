package com.information.coronavirustracker.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.information.coronavirustracker.models.LocationStats;
import com.information.coronavirustracker.services.CoronaVirusDataService;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
    public String home(Model model) {
        List<LocationStats> allStats = coronaVirusDataService.getAllStats();
        List<LocationStats> indianStats = coronaVirusDataService.getIndianStats();
        //Mapping Indian data below:
        int totalReportedCasesInIndia = indianStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
        int totalNewCasesInIndia = indianStats.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();
        
        //Mapping World data below:
        int totalReportedCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
        int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();
        
        
        model.addAttribute("locationStats", allStats);
        model.addAttribute("indianStats", indianStats);
      
        //world data below:
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("totalNewCases", totalNewCases);
        
        //India data below:
        model.addAttribute("totalReportedCasesInIndia", totalReportedCasesInIndia);
        model.addAttribute("totalNewCasesInIndia", totalNewCasesInIndia);

        return "home";
    }
    
    
}
