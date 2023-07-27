package me.utku.webscrapinghoneypotBE.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping(value = {"","/","/static/**","/templates/**"})
    public String home(){
        return "index";
    }
}
