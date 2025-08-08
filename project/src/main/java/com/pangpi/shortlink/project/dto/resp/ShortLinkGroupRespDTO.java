package com.pangpi.shortlink.project.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortLinkGroupRespDTO implements Serializable {

    /**
     * 分组标识
     */
    private String pid;

    /**
     * 分组名称
     */
    private String name;


}
