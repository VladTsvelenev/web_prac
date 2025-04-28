package ru.web.tsvelenev.WEB.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping(value = { "/", "/index"})
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/theaters")
    public String theaters() {
        return "theaters";
    }

    @RequestMapping(value = "/performances")
    public String performances() {
        return "performances";
    }

    @RequestMapping(value = "/purchase-history")
    public String purchaseHistory() {
        return "purchaseHistory";
    }

    @RequestMapping(value = "/login")
    public String login() {
        return "login";
    }

    @RequestMapping(value = "/register")
    public String register() {
        return "register";
    }
}