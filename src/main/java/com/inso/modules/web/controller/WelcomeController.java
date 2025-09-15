package com.inso.modules.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WelcomeController {

    @RequestMapping({"", "/", "/index.html"})
    public String index()
    {
//        if(SystemRunningMode.isCryptoMode())
//        {
            return "redirect:/mining/index.html";
//        }
//        return "redirect:/h5/index.html";
    }

}
