package com.aekc.mmall.service;

import com.aekc.mmall.beans.PageQuery;
import com.aekc.mmall.beans.PageResult;
import com.aekc.mmall.model.SysUser;
import com.aekc.mmall.param.UserParam;

import java.util.List;

public interface SysUserService {

    void save(UserParam param);

    void update(UserParam param);

    SysUser findByKeyword(String keyword);

    PageResult<SysUser> getPageByDeptId(int deptId, PageQuery pageQuery);

    List<SysUser> getAll();

    SysUser findByUserId(int userId);
}
