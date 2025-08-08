package com.pangpi.shortlink.admin.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * (Group)表实体类
 *
 * @author pangpi
 * @since 2024-06-27 20:11:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortLinkGroupRespDTO implements Serializable {

    //分组标识
    private String gid;
    //分组名称
    private String name;
    //分组排序
    private Integer sortOrder;
    //当前短链接分组下有多少短链接
    private Integer shortLinkCount;
}
