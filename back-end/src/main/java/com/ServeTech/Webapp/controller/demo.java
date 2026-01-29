package com.ServeTech.Webapp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class demo {

    @RequestMapping("/test")
    public String test(){
        return "Hello World";
    }
}
