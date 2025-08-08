package com.pangpi.shortlink.admin.remote.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短链接分页查询实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortLinkPageReqDTO extends Page {

    /**
     * 分组id
     */
    private String gid;

}
