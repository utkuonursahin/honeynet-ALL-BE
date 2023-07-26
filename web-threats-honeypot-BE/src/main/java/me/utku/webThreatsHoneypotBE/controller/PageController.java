package me.utku.webThreatsHoneypotBE.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping(value = {"/", "/login","/dashboard","/signup","/reset-password","/static/**","/templates/**"})
    public String home(){
        return "index";
    }
}
