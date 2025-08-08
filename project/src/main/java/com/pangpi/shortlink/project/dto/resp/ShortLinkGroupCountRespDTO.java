package com.pangpi.shortlink.project.dto.resp;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分组统计短链接个数实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortLinkGroupCountRespDTO {


    private String gid;

    private Integer shortLinkCount;

}
