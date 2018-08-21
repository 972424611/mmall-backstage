package com.aekc.mmall.service;

import com.aekc.mmall.beans.PageQuery;
import com.aekc.mmall.beans.PageResult;
import com.aekc.mmall.param.AclParam;

public interface SysAclService {

    void save(AclParam param);

    void update(AclParam param);

    PageResult getPageByAclModuleId(int aclModuleId, PageQuery pageQuery);
}
