package com.aekc.mmall.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysRoleUser extends SysBase {

    private Integer roleId;

    private Integer userId;

}