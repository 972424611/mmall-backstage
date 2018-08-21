package com.aekc.mmall.service;

import com.aekc.mmall.dto.AclModuleLevelDto;
import com.aekc.mmall.dto.DeptLevelDto;

import java.util.List;

public interface SysTreeService {

    List<AclModuleLevelDto> userAclTree(int userId);

    List<AclModuleLevelDto> aclModuleTree();

    List<DeptLevelDto> deptTree();

    List<AclModuleLevelDto> roleTree(Integer roleId);
}
