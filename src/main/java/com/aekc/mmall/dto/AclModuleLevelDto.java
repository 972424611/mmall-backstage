package com.aekc.mmall.dto;

import com.aekc.mmall.model.SysAclModule;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

//可通过callSuper=true。让其生成的方法中调用父类的方法
@Data
@EqualsAndHashCode(callSuper = true)
public class AclModuleLevelDto extends SysAclModule {

    private List<AclModuleLevelDto> aclModuleList = new ArrayList<>();

    private List<AclDto> aclList = new ArrayList<>();

    public static AclModuleLevelDto adapt(SysAclModule aclModule) {
        AclModuleLevelDto dto = new AclModuleLevelDto();
        BeanUtils.copyProperties(aclModule, dto);
        return dto;
    }
}
