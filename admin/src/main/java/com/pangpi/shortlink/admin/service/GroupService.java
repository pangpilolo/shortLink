package com.pangpi.shortlink.admin.service;
 
import com.pangpi.shortlink.admin.dao.entity.GroupDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pangpi.shortlink.admin.dto.req.GroupSortReqDTO;
import com.pangpi.shortlink.admin.dto.req.GroupUpdateReqDTO;
import com.pangpi.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;

import java.util.List;


/**
 * (Group)表服务接口
 * @author pangpi
 * @since 2024-06-27 20:11:55
 */
public interface GroupService extends IService<GroupDO> {

    /***
     * 新增短链接分组
     * @param groupName 分组名称
     */
    void addGroup(String groupName);

    /***
     * 直接携带用户名新增短链接分组
     * @param groupName 分组名称
     * @param username 用户名
     */
    void addGroup(String username,String groupName);

    /**
     * 根据上下文查询当前登录用户的短链接分组
     * @return 分组集合
     */
    List<ShortLinkGroupRespDTO> getGroupList();

    /**
     * 修改分组信息
     * @param requestParam 修改的分组信息
     */
    void updateGroup(GroupUpdateReqDTO requestParam);

    /**
     * 删除短链接分组
     * @param pid 短链接分组id
     */
    void deleteGroup(String pid);

    /**
     * 根据参数将分组排序
     * @param requestParam 分组集合
     */
    void sortGroup(List<GroupSortReqDTO> requestParam);
}
