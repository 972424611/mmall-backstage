package com.aekc.mmall.service;

import com.aekc.mmall.model.SysUser;

import java.util.List;

public interface SysRoleUserService {

    List<SysUser> getListByRoleId(int roleId);

    void changeRoleUsers(int roleId, List<Integer> userIdList);
}
