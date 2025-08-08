package com.pangpi.shortlink.project.service.impl;

import com.pangpi.shortlink.project.dao.entity.LinkAccessLogsDO;
import com.pangpi.shortlink.project.dao.mapper.LinkAccessLogsMapper;
import com.pangpi.shortlink.project.service.LinkAccessLogsService; 

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * (LinkAccessLogs)表服务实现类
 * @author pangpi
 * @since 2024-07-06 19:00:38
 */
@Service("linkAccessLogsService")
public class LinkAccessLogsServiceImpl extends ServiceImpl<LinkAccessLogsMapper, LinkAccessLogsDO> implements LinkAccessLogsService {

}
