package com.pangpi.shortlink.project.service.impl;

import com.pangpi.shortlink.project.dao.entity.LinkBrowserStatsDO;
import com.pangpi.shortlink.project.dao.mapper.LinkBrowserStatsMapper;
import com.pangpi.shortlink.project.service.LinkBrowserStatsService; 

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * (LinkBrowserStats)表服务实现类
 * @author pangpi
 * @since 2024-07-06 18:29:30
 */
@Service("linkBrowserStatsService")
public class LinkBrowserStatsServiceImpl extends ServiceImpl<LinkBrowserStatsMapper, LinkBrowserStatsDO> implements LinkBrowserStatsService {

}
