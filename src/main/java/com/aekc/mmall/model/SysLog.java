package com.aekc.mmall.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysLog extends SysBase {

    private Integer type;

    private Integer targetId;

    private Integer status;

}