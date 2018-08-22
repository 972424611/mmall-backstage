package com.aekc.mmall.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysRole extends SysBase {

    private String name;

    private Integer type;

    private Integer status;

    private String remark;

}