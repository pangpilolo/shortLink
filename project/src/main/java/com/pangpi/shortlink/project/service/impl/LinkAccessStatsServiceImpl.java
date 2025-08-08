package com.pangpi.shortlink.project.service.impl;

import com.pangpi.shortlink.project.dao.entity.LinkAccessStatsDO;
import com.pangpi.shortlink.project.dao.mapper.LinkAccessStatsMapper;
import com.pangpi.shortlink.project.service.LinkAccessStatsService; 

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * (LinkAccessStats)表服务实现类
 * @author pangpi
 * @since 2024-07-04 20:05:30
 */
@Service
public class LinkAccessStatsServiceImpl extends ServiceImpl<LinkAccessStatsMapper, LinkAccessStatsDO> implements LinkAccessStatsService {

}
