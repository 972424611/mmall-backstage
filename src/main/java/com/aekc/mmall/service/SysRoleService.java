package com.aekc.mmall.service;

import com.aekc.mmall.model.SysRole;
import com.aekc.mmall.model.SysUser;
import com.aekc.mmall.param.RoleParam;

import java.util.List;

public interface SysRoleService {

    void save(RoleParam param);

    void update(RoleParam param);

    List<SysRole> getAll();

    List<SysRole> getRoleListByUserId(int userId);

    List<SysRole> getRoleListByAclId(int aclId);

    List<SysUser> getUserListByRoleList(List<SysRole> roleList);
}
