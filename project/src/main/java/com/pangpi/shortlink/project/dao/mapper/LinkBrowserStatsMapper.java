package com.pangpi.shortlink.project.dao.mapper;

import com.pangpi.shortlink.project.dao.entity.LinkBrowserStatsDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pangpi.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import com.pangpi.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;


/**
 * (LinkBrowserStats)表数据库访问层
 * @author pangpi
 * @since 2024-07-06 18:29:30
 */
public interface LinkBrowserStatsMapper extends BaseMapper<LinkBrowserStatsDO> {

    /**
     * 访问浏览器新增
     */
    void shortLinkBrowserStates(@Param("stats") LinkBrowserStatsDO linkBrowserStatsDO);

    /**
     * 根据短链接获取指定日期内浏览器监控数据
     */
    List<HashMap<String, Object>> listBrowserStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内浏览器监控数据
     */
    List<HashMap<String, Object>> listBrowserStatsByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);
}
