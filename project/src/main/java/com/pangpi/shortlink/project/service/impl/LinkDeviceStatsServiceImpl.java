package com.pangpi.shortlink.project.service.impl;

import com.pangpi.shortlink.project.dao.entity.LinkDeviceStatsDO;
import com.pangpi.shortlink.project.dao.mapper.LinkDeviceStatsMapper;
import com.pangpi.shortlink.project.service.LinkDeviceStatsService; 

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * (LinkDeviceStats)表服务实现类
 * @author pangpi
 * @since 2024-07-06 19:36:35
 */
@Service("linkDeviceStatsService")
public class LinkDeviceStatsServiceImpl extends ServiceImpl<LinkDeviceStatsMapper, LinkDeviceStatsDO> implements LinkDeviceStatsService {

}
