package com.aekc.mmall.dto;

import com.aekc.mmall.model.SysAclModule;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class AclModuleLevelDto extends SysAclModule {

    private List<AclModuleLevelDto> aclModuleList = new ArrayList<>();

    private List<AclDto> aclList = new ArrayList<>();

    public static AclModuleLevelDto adapt(SysAclModule aclModule) {
        AclModuleLevelDto dto = new AclModuleLevelDto();
        BeanUtils.copyProperties(aclModule, dto);
        return dto;
    }
}
