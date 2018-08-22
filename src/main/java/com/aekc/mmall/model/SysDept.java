package com.aekc.mmall.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysDept extends SysBase {

    private String name;

    private Integer parentId;

    private String level;

    private Integer seq;

    private String remark;

}