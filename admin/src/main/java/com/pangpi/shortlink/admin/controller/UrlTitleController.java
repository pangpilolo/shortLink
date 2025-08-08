package com.pangpi.shortlink.admin.controller;

import com.pangpi.shortlink.admin.remote.ShortLinkRemoteService;
import com.pangpi.shortlink.convention.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/short-link/admin")
public class UrlTitleController {


    private final ShortLinkRemoteService shortLinkService;

    @GetMapping("/title")
    public Result<String> getTitleByUrl(@RequestParam("url") String url) {
        return shortLinkService.getTitleByUrl(url);
    }

}
