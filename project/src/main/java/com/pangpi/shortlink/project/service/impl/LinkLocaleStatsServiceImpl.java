package com.pangpi.shortlink.project.service.impl;

import com.pangpi.shortlink.project.dao.entity.LinkLocaleStatsDO;
import com.pangpi.shortlink.project.dao.mapper.LinkLocaleStatsMapper;
import com.pangpi.shortlink.project.service.LinkLocaleStatsService; 

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * (LinkLocaleStats)表服务实现类
 * @author pangpi
 * @since 2024-07-05 20:55:53
 */
@Service("linkLocaleStatsService")
public class LinkLocaleStatsServiceImpl extends ServiceImpl<LinkLocaleStatsMapper, LinkLocaleStatsDO> implements LinkLocaleStatsService {

}
