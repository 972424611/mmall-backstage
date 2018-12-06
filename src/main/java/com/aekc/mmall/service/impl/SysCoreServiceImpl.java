package com.aekc.mmall.service.impl;

import com.aekc.mmall.common.RequestHolder;
import com.aekc.mmall.dao.SysAclMapper;
import com.aekc.mmall.dao.SysRoleAclMapper;
import com.aekc.mmall.dao.SysRoleUserMapper;
import com.aekc.mmall.model.SysAcl;
import com.aekc.mmall.model.SysUser;
import com.aekc.mmall.service.SysCoreService;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysCoreServiceImpl implements SysCoreService {

    @Autowired
    private SysAclMapper sysAclMapper;

    @Autowired
    private SysRoleUserMapper sysRoleUserMapper;

    @Autowired
    private SysRoleAclMapper sysRoleAclMapper;

    @Override
    public List<SysAcl> getCurrentUserAclList() {
        int userId = RequestHolder.getCurrentUser().getId();
        return getUserAclList(userId);
    }

    @Override
    public List<SysAcl> getRoleAclList(int roleId) {
        List<Integer> aclIdList = sysRoleAclMapper.selectAclIdListByRoleId(roleId);
        if (CollectionUtils.isEmpty(aclIdList)) {
            return Lists.newArrayList();
        }
        return sysAclMapper.selectByIdList(aclIdList);
    }

    @Override
    public List<SysAcl> getUserAclList(int userId) {
        if (isSuperAdmin()) {
            return sysAclMapper.selectAllAcl();
        }
        List<Integer> userRoleIdList = sysRoleUserMapper.selectRoleIdListByUserId(userId);
        if (CollectionUtils.isEmpty(userRoleIdList)) {
            return Lists.newArrayList();
        }
        List<Integer> userAclIdList = new ArrayList<>();
        for (Integer roleId : userRoleIdList) {
            userAclIdList.addAll(sysRoleAclMapper.selectAclIdListByRoleId(roleId));
        }
        if (CollectionUtils.isEmpty(userAclIdList)) {
            return Lists.newArrayList();
        }
        Set<Integer> userAclIdSet = new HashSet<>(userAclIdList);
        return sysAclMapper.selectByIdList(new ArrayList<>(userAclIdSet));
    }

    private boolean isSuperAdmin() {
        // 这里自定义超级管理员规则
        // 可以从配置文件取出, 可以设定某个用户, 或者某个角色
        //SysUser sysUser = RequestHolder.getCurrentUser();
        //return "admin".equals(sysUser.getUsername());
        return true;
    }

    @Override
    public boolean hasUrlAcl(String url) {
        if (isSuperAdmin()) {
            return true;
        }
        List<SysAcl> aclList = sysAclMapper.selectByUrl(url);
        if (CollectionUtils.isEmpty(aclList)) {
            return true;
        }
        // 这里可以使用下面的getCurrentUserAclListFromCache()
        List<SysAcl> userAclList = getCurrentUserAclList();
        Set<Integer> userAclIdSet = userAclList.stream().map(SysAcl::getId).collect(Collectors.toSet());
        // 只要一个权限点有权限, 那么我们就认为有访问权限
        boolean hasValidAcl = true;
        for (SysAcl acl : aclList) {
            // 判断一个用户是否具有某个权限点的访问权限
            if (acl == null || acl.getStatus() != 1) {
                //权限点无效
                continue;
            }
            hasValidAcl = false;
            if (userAclIdSet.contains(acl.getId())) {
                return true;
            }
        }
        // 如果hasValidAcl为true, 说明该url对应的所有权限点不存在或者为停用状态, 这样也认为用户也拥有访问该url的权限
        return hasValidAcl;
    }
}
