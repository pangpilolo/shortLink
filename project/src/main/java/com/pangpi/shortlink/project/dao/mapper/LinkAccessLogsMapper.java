package com.pangpi.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pangpi.shortlink.project.dao.entity.LinkAccessLogsDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pangpi.shortlink.project.dao.entity.LinkAccessStatsDO;
import com.pangpi.shortlink.project.dto.req.ShortLinkGroupStatsAccessRecordReqDTO;
import com.pangpi.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import com.pangpi.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * (LinkAccessLogs)表数据库访问层
 * @author pangpi
 * @since 2024-07-06 19:00:38
 */
public interface LinkAccessLogsMapper extends BaseMapper<LinkAccessLogsDO> {

    /**
     * 根据短链接获取指定日期内高频访问IP数据
     */
    List<HashMap<String, Object>> listTopIpByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内高频访问IP数据
     */
    List<HashMap<String, Object>> listTopIpByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);

    /**
     * 根据短链接获取指定日期内新旧访客数据
     */
    HashMap<String, Object> findUvTypeCntByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 获取用户信息是否新老访客
     */
    List<Map<String, Object>> selectUvTypeByUsers(
            @Param("gid") String gid,
            @Param("fullShortUrl") String fullShortUrl,
            @Param("enableStatus") Integer enableStatus,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("userAccessLogsList") List<String> userAccessLogsList
    );

    /**
     * 获取分组用户信息是否新老访客
     */
    List<Map<String, Object>> selectGroupUvTypeByUsers(
            @Param("gid") String gid,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("userAccessLogsList") List<String> userAccessLogsList
    );

    /**
     * 根据短链接获取指定日期内PV、UV、UIP数据
     */
    LinkAccessStatsDO findPvUvUidStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内PV、UV、UIP数据
     */
    LinkAccessStatsDO findPvUvUidStatsByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);

    IPage<LinkAccessLogsDO> selectGroupPage(@Param("param") ShortLinkGroupStatsAccessRecordReqDTO requestParam);
}
