package com.pangpi.shortlink.project.controller;


import com.pangpi.shortlink.convention.result.Result;
import com.pangpi.shortlink.convention.result.Results;
import com.pangpi.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.pangpi.shortlink.project.dto.resp.linkStats.ShortLinkStatsRespDTO;
import com.pangpi.shortlink.project.service.ShortLinkStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/short-link")
@RequiredArgsConstructor
public class LinkStatsController {

    private final ShortLinkStatsService shortLinkStatsService;

    public Result<ShortLinkStatsRespDTO> shortLinkStats(ShortLinkStatsReqDTO reqDTO) {
        ShortLinkStatsRespDTO res = shortLinkStatsService.oneShortLinkStats(reqDTO);
        return Results.success(res);
    }

}
