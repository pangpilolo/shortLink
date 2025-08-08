package com.pangpi.shortlink.admin.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GroupSortReqDTO implements Serializable {

    private String pid;

    private Integer sortOrder;

}
