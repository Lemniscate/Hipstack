package com.github.lemniscate.hipstack.web.controller;

import com.github.lemniscate.hipstack.domain.user.UserAccount;
import com.github.lemniscate.hipstack.svc.SecurityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;

/**
 * Created by dave on 2/23/15.
 */
@Controller
public class ContentController {

    @Inject
    private SecurityService securityService;

    @RequestMapping("/")
    public String getRoot(Model model){
        return "home";
    }
    
    @RequestMapping("/app")
    public String getApp(Model model){
        UserAccount user = securityService.getCurrentUser();

        model.addAttribute("isDeployed", false);
        model.addAttribute("angularModule", "hipstack.app");
        model.addAttribute("user", user);
        return "app/views/app";
    }
    
}
