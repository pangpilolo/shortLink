package com.pangpi.shortlink.project.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.pangpi.shortlink.convention.result.Result;
import com.pangpi.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.pangpi.shortlink.project.dto.resp.ShortLinkCreateRespDTO;

public class CustomBlockHandler {


    public Result<ShortLinkCreateRespDTO> createShortLinkBlockHandlerMethod(ShortLinkCreateReqDTO requestParam, BlockException blockException) {
        return new Result<ShortLinkCreateRespDTO>().setCode("B100000").setMessage("当前访问网站人数过多");
    }

}
