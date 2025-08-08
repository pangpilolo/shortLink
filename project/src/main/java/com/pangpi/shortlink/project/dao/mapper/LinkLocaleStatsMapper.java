package com.pangpi.shortlink.project.dao.mapper;

import com.pangpi.shortlink.project.dao.entity.LinkLocaleStatsDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pangpi.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import com.pangpi.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * (LinkLocaleStats)表数据库访问层
 * @author pangpi
 * @since 2024-07-05 20:55:53
 */
public interface LinkLocaleStatsMapper extends BaseMapper<LinkLocaleStatsDO> {


    /**
     * 访问地区信息新增
     */
    void shortLinkLocaleStats(@Param("stats") LinkLocaleStatsDO linkLocaleStatsDO);

    /**
     * 根据短链接获取指定日期内地区监控数据
     */
    List<LinkLocaleStatsDO> listLocaleByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内地区监控数据
     */
    List<LinkLocaleStatsDO> listLocaleByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);
}
