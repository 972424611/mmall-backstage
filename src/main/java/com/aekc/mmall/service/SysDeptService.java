package com.aekc.mmall.service;

import com.aekc.mmall.param.DeptParam;

public interface SysDeptService {

    void delete(Integer deptId);

    void save(DeptParam param);

    void update(DeptParam param);
}
