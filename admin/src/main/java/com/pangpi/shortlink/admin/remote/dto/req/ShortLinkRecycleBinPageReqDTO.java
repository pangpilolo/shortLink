package com.pangpi.shortlink.admin.remote.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 回收站短链接分页请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortLinkRecycleBinPageReqDTO extends Page implements Serializable {

    /**
     * 分组标识
     */
    private List<String> gidList;

}
