package com.aekc.mmall.service;

import com.aekc.mmall.param.AclModuleParam;

public interface SysAclModuleService {

    void save(AclModuleParam param);

    void update(AclModuleParam param);

    void delete(Integer aclModuleId);
}
