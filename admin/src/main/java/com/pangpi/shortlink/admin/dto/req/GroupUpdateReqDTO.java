package com.pangpi.shortlink.admin.dto.req;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * (Group)表实体类
 *
 * @author pangpi
 * @since 2024-06-27 20:11:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupUpdateReqDTO implements Serializable {

    //分组标识
    private String gid;
    //分组名称
    private String name;

}
