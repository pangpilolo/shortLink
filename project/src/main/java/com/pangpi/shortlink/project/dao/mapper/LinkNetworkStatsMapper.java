package com.pangpi.shortlink.project.dao.mapper;

import com.pangpi.shortlink.project.dao.entity.LinkNetworkStatsDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pangpi.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import com.pangpi.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * (LinkNetworkStats)表数据库访问层
 * @author pangpi
 * @since 2024-07-06 19:43:12
 */
public interface LinkNetworkStatsMapper extends BaseMapper<LinkNetworkStatsDO> {


    /**
     * 访问网络新增
     */
    void shortLinkNetworkState(@Param("stats") LinkNetworkStatsDO linkNetworkStatsDO);

    /**
     * 根据短链接获取指定日期内访问网络监控数据
     */
    List<LinkNetworkStatsDO> listNetworkStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内访问网络监控数据
     */
    List<LinkNetworkStatsDO> listNetworkStatsByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);

}
