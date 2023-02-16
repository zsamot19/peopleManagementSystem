package com.tomasz.peopledbweb.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Repository("error")
public class ErrorController {
    @GetMapping
    public String getErrorPage(){
        return "error";
    }
}
