package com.aekc.mmall.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysLogWithBLOBs extends SysLog {

    private String oldValue;

    private String newValue;
}