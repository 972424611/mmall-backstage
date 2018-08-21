package com.aekc.mmall.param;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AclModuleParam {

    private Integer id;

    @NotBlank(message = "权限模块名称不可以为空")
    @Length(min = 2, max = 64, message = "权限模块名称长度要在2-64个字之间")
    private String name;

    private Integer parentId = 0;

    @NotNull(message = "权限模块展示顺序不能为空")
    private Integer seq;

    @NotNull(message = "权限模块状态不能为空")
    @Min(value = 0, message = "权限模块状态不合法")
    @Max(value = 1, message = "权限模块状态不合法")
    private Integer status;

    @Length(max = 64, message = "权限模块备注长度要在64个字之间")
    private String remark;
}
