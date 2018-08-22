package com.aekc.mmall.service.impl;

import com.aekc.mmall.common.BeanValidator;
import com.aekc.mmall.common.RequestHolder;
import com.aekc.mmall.dao.SysRoleAclMapper;
import com.aekc.mmall.dao.SysRoleMapper;
import com.aekc.mmall.dao.SysRoleUserMapper;
import com.aekc.mmall.dao.SysUserMapper;
import com.aekc.mmall.exception.RoleException;
import com.aekc.mmall.model.SysRole;
import com.aekc.mmall.model.SysUser;
import com.aekc.mmall.param.RoleParam;
import com.aekc.mmall.service.SysLogService;
import com.aekc.mmall.service.SysRoleService;
import com.aekc.mmall.utils.IpUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRoleUserMapper sysRoleUserMapper;

    @Autowired
    private SysRoleAclMapper sysRoleAclMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysLogService sysLogService;

    private boolean checkExist(String name, Integer id) {
        return sysRoleMapper.countByName(name, id) > 0;
    }

    @Override
    public void save(RoleParam param) {
        BeanValidator.check(param);
        if(checkExist(param.getName(), param.getId())) {
            throw new RoleException("角色名称已经存在");
        }
        SysRole role = new SysRole();
        BeanUtils.copyProperties(param, role);
        role.setOperator(RequestHolder.getCurrentUser().getUsername());
        role.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        role.setOperateTime(new Date());
        sysRoleMapper.insertSelective(role);
        sysLogService.saveRoleLog(null, role);
    }

    @Override
    public void update(RoleParam param) {
        BeanValidator.check(param);
        if(checkExist(param.getName(), param.getId())) {
            throw new RoleException("角色名称已经存在");
        }
        SysRole before = sysRoleMapper.selectByPrimaryKey(param.getId());
        if(before == null) {
            throw new RoleException("待更新的角色不存在");
        }
        SysRole after = new SysRole();
        BeanUtils.copyProperties(param, after);
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        sysRoleMapper.updateByPrimaryKeySelective(after);
        sysLogService.saveRoleLog(before, after);
    }

    @Override
    public List<SysRole> getAll() {
        return sysRoleMapper.selectAllRole();
    }

    @Override
    public List<SysRole> getRoleListByUserId(int userId) {
        List<Integer> roleIdList = sysRoleUserMapper.selectRoleIdListByUserId(userId);
        if(CollectionUtils.isEmpty(roleIdList)) {
            return Lists.newArrayList();
        }
        return sysRoleMapper.selectByIdList(roleIdList);
    }

    @Override
    public List<SysRole> getRoleListByAclId(int aclId) {
        List<Integer> roleIdList = sysRoleAclMapper.selectRoleIdListByAclId(aclId);
        if(CollectionUtils.isEmpty(roleIdList)) {
            return Lists.newArrayList();
        }
        return sysRoleMapper.selectByIdList(roleIdList);
    }

    @Override
    public List<SysUser> getUserListByRoleList(List<SysRole> roleList) {
        if(CollectionUtils.isEmpty(roleList)) {
            return Lists.newArrayList();
        }
        List<Integer> roleIdList = roleList.stream().map(SysRole::getId).collect(Collectors.toList());
        List<Integer> userIdList = sysRoleUserMapper.selectUserIdListByRoleIdList(roleIdList);
        if(CollectionUtils.isEmpty(userIdList)) {
            return Lists.newArrayList();
        }
        return sysUserMapper.selectByIdList(userIdList);
    }
}
