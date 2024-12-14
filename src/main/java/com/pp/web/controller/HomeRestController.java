package com.pp.web.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeRestController {

    @RequestMapping("/")
    public String home() {
        return "ok";
    }
}
