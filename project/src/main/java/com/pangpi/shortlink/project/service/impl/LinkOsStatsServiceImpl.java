package com.pangpi.shortlink.project.service.impl;

import com.pangpi.shortlink.project.dao.entity.LinkOsStatsDO;
import com.pangpi.shortlink.project.dao.mapper.LinkOsStatsMapper;
import com.pangpi.shortlink.project.service.LinkOsStatsService; 

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 短链接监控操作系统访问状态(LinkOsStats)表服务实现类
 * @author pangpi
 * @since 2024-07-06 17:47:30
 */
@Service("linkOsStatsService")
public class LinkOsStatsServiceImpl extends ServiceImpl<LinkOsStatsMapper, LinkOsStatsDO> implements LinkOsStatsService {

}
