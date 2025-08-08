package com.pangpi.shortlink.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pangpi.shortlink.project.dao.entity.ShortLinkDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短链接分页查询实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortLinkPageReqDTO extends Page<ShortLinkDO> {

    /**
     * 分组id
     */
    private String gid;

}
