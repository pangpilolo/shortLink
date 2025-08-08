package com.pangpi.shortlink.admin.controller;

import com.pangpi.shortlink.convention.result.Result;
import com.pangpi.shortlink.convention.result.Results;
import com.pangpi.shortlink.admin.dto.req.GroupSortReqDTO;
import com.pangpi.shortlink.admin.dto.req.GroupUpdateReqDTO;
import com.pangpi.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.pangpi.shortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shortlink/admin")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    /**
     * 新增分组
     */
    @PostMapping("/v1/group")
    public Result<Void> addGroup(@RequestParam("groupName") String groupName) {
        groupService.addGroup(groupName);
        return Results.success();
    }

    @GetMapping("/v1/group")
    public Result<List<ShortLinkGroupRespDTO>> getGroupList() {
        List<ShortLinkGroupRespDTO> res = groupService.getGroupList();
        return Results.success(res);
    }

    @PutMapping("/v1/group")
    public Result<Void> updateGroup(@RequestBody GroupUpdateReqDTO requestParam) {
        groupService.updateGroup(requestParam);
        return Results.success();
    }

    @DeleteMapping("/v1/group")
    public Result<Void> deleteGroup(@RequestParam String pid) {
        groupService.deleteGroup(pid);
        return Results.success();
    }

    @PostMapping("/v1/group/sort")
    public Result<Void> sortGroup(@RequestParam List<GroupSortReqDTO> requestParam) {
        groupService.sortGroup(requestParam);
        return Results.success();
    }

}
