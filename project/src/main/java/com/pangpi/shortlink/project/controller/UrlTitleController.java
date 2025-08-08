package com.pangpi.shortlink.project.controller;

import com.pangpi.shortlink.convention.result.Result;
import com.pangpi.shortlink.convention.result.Results;
import com.pangpi.shortlink.project.service.UrlTitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/short-link")
public class UrlTitleController {


    private final UrlTitleService urlTitleService;

    @GetMapping("/v1/title")
    public Result<String> getTitleByUrl(@RequestParam("url") String url) {
        return Results.success(urlTitleService.getTitleByUrl(url));
    }

}
