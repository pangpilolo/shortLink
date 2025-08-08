package com.pangpi.shortlink.project.service.impl;

import com.pangpi.shortlink.project.dao.entity.LinkNetworkStatsDO;
import com.pangpi.shortlink.project.dao.mapper.LinkNetworkStatsMapper;
import com.pangpi.shortlink.project.service.LinkNetworkStatsService; 

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * (LinkNetworkStats)表服务实现类
 * @author pangpi
 * @since 2024-07-06 19:43:12
 */
@Service("linkNetworkStatsService")
public class LinkNetworkStatsServiceImpl extends ServiceImpl<LinkNetworkStatsMapper, LinkNetworkStatsDO> implements LinkNetworkStatsService {

}
