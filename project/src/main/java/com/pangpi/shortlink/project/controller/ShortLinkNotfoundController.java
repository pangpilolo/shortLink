package com.pangpi.shortlink.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ShortLinkNotfoundController {



    @RequestMapping("/page/notfound")
    public String notfound() {
        return "notfound";
    }

}
