package com.pangpi.shortlink.admin.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.pangpi.shortlink.admin.common.biz.user.UserContext;
import com.pangpi.shortlink.admin.common.constant.RedisCacheConstant;
import com.pangpi.shortlink.admin.common.constant.WebConstants;
import com.pangpi.shortlink.admin.remote.dto.resp.ShortLinkGroupCountRespDTO;
import com.pangpi.shortlink.admin.remote.ShortLinkRemoteService;
import com.pangpi.shortlink.convention.exception.ClientException;
import com.pangpi.shortlink.convention.exception.ServiceException;
import com.pangpi.shortlink.admin.dao.entity.GroupDO;
import com.pangpi.shortlink.admin.dao.mapper.GroupMapper;
import com.pangpi.shortlink.admin.dto.req.GroupSortReqDTO;
import com.pangpi.shortlink.admin.dto.req.GroupUpdateReqDTO;
import com.pangpi.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.pangpi.shortlink.admin.service.GroupService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pangpi.shortlink.convention.result.Result;
import com.pangpi.shortlink.convention.util.BeanCopyUtils;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * (Group)表服务实现类
 *
 * @author pangpi
 * @since 2024-06-27 20:11:55
 */
@RequiredArgsConstructor
@Service("groupService")
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    private final ShortLinkRemoteService shortLinkRemoteService;
    private final RedissonClient redissonClient;


    @Value("${short-link.group.max-num}")
    private Integer groupMaxNum;

    @Override
    public void addGroup(String groupName) {
        addGroup(UserContext.getUsername(), groupName);
    }

    @Override
    public void addGroup(String username, String groupName) {
        RLock lock = redissonClient.getLock(String.format(RedisCacheConstant.LOCK_GROUP_CREATE_KEY, username));
        lock.lock();
        try {
            // 检查当前的分组数量是否超过配置
            LambdaQueryWrapper<GroupDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(GroupDO::getUsername, username)
                    .eq(GroupDO::getDelFlag, WebConstants.NO_DELETE_FLAG);
            long count = count(queryWrapper);
            if (count >= groupMaxNum) {
                throw new ClientException(String.format("已超出最大分组数：%d", groupMaxNum));
            }
            GroupDO groupDO = GroupDO.builder()
                    .gid(IdUtil.simpleUUID())
                    .name(groupName)
                    .username(username)
                    .sortOrder(0)
                    .build();
            if (!save(groupDO)) {
                throw new ServiceException("分组保存失败");
            }
        } catch (Throwable throwable) {

        } finally {
            lock.unlock();
        }

    }

    @Override
    public List<ShortLinkGroupRespDTO> getGroupList() {
        LambdaUpdateWrapper<GroupDO> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, WebConstants.NO_DELETE_FLAG);
        queryWrapper.orderByDesc(GroupDO::getSortOrder).orderByDesc(GroupDO::getUpdateTime);
        List<GroupDO> groupDOList = list(queryWrapper);

        // 转换为最后返回的实体集合
        List<ShortLinkGroupRespDTO> res = BeanCopyUtils.copyBeanList(groupDOList, ShortLinkGroupRespDTO.class);
        // 获取分组统计
        List<String> gidList = groupDOList.stream().map(GroupDO::getGid).collect(Collectors.toList());
        Result<List<ShortLinkGroupCountRespDTO>> result = shortLinkRemoteService.listShortLinkGroupCount(gidList);
        // 转换map以及填充值
        Map<String, Integer> groupCountMap = result.getData().stream().collect(Collectors.toMap(ShortLinkGroupCountRespDTO::getGid, ShortLinkGroupCountRespDTO::getShortLinkCount));
        res.forEach(item -> {
            item.setShortLinkCount(groupCountMap.getOrDefault(item.getGid(), null));
        });
        return res;
    }

    @Override
    public void updateGroup(GroupUpdateReqDTO requestParam) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, WebConstants.NO_DELETE_FLAG)
                .eq(GroupDO::getGid, requestParam.getGid())
                .set(GroupDO::getName, requestParam.getName());
        update(updateWrapper);
    }

    @Override
    public void deleteGroup(String pid) {
        // 采用逻辑删除
        LambdaUpdateWrapper<GroupDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(GroupDO::getGid, pid)
                .eq(GroupDO::getDelFlag, WebConstants.NO_DELETE_FLAG)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .set(GroupDO::getDelFlag, WebConstants.DELETE_FLAG);
        update(updateWrapper);
    }

    @Transactional
    @Override
    public void sortGroup(List<GroupSortReqDTO> requestParam) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = new LambdaUpdateWrapper<>();
        requestParam.forEach(item -> {
            updateWrapper.eq(GroupDO::getUsername, UserContext.getUsername())
                    .eq(GroupDO::getGid, item.getPid())
                    .eq(GroupDO::getDelFlag, WebConstants.NO_DELETE_FLAG)
                    .set(GroupDO::getSortOrder, item.getSortOrder());
            update(updateWrapper);
            updateWrapper.clear();
        });
    }
}
