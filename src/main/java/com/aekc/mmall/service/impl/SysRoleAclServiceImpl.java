package com.aekc.mmall.service.impl;

import com.aekc.mmall.common.RequestHolder;
import com.aekc.mmall.dao.SysLogMapper;
import com.aekc.mmall.dao.SysRoleAclMapper;
import com.aekc.mmall.enums.LogType;
import com.aekc.mmall.model.SysLogWithBLOBs;
import com.aekc.mmall.model.SysRoleAcl;
import com.aekc.mmall.security.InvocationSecurityMetadataSource;
import com.aekc.mmall.service.SysRoleAclService;
import com.aekc.mmall.utils.IpUtil;
import com.aekc.mmall.utils.JsonUtil;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class SysRoleAclServiceImpl implements SysRoleAclService {

    @Autowired
    private SysRoleAclMapper sysRoleAclMapper;

    @Autowired
    private SysLogMapper sysLogMapper;

    @Override
    public void changeRoleAcls(Integer roleId, List<Integer> aclIdList) {
        List<Integer> originAclIdList = sysRoleAclMapper.selectAclIdListByRoleId(roleId);
        ;
        if (originAclIdList.size() == aclIdList.size()) {
            Set<Integer> originAclIdSet = Sets.newHashSet(originAclIdList);
            Set<Integer> aclIdSet = Sets.newHashSet(aclIdList);
            originAclIdSet.removeAll(aclIdSet);
            if (CollectionUtils.isEmpty(originAclIdSet)) {
                return;
            }
        }
        updateRoleAcls(roleId, aclIdList);
        saveRoleAclLog(roleId, originAclIdList, aclIdList);
        // 提示权限更新了
        InvocationSecurityMetadataSource.update = true;
    }

    @Transactional(rollbackFor = Exception.class)
    protected void updateRoleAcls(int roleId, List<Integer> aclIdList) {
        sysRoleAclMapper.deleteByRoleId(roleId);
        if (CollectionUtils.isEmpty(aclIdList)) {
            return;
        }
        List<SysRoleAcl> roleAclList = new ArrayList<>();
        for (Integer aclId : aclIdList) {
            SysRoleAcl roleAcl = new SysRoleAcl();
            roleAcl.setAclId(aclId);
            roleAcl.setRoleId(roleId);
            roleAcl.setOperator(RequestHolder.getCurrentUser().getUsername());
            roleAcl.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
            roleAcl.setOperateTime(new Date());
            roleAclList.add(roleAcl);
        }
        sysRoleAclMapper.batchInsert(roleAclList);
    }

    private void saveRoleAclLog(int roleId, List<Integer> before, List<Integer> after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ROLE_ACL.getType());
        sysLog.setTargetId(roleId);
        sysLog.setOldValue(before == null ? "" : JsonUtil.objectToJson(before));
        sysLog.setNewValue(after == null ? "" : JsonUtil.objectToJson(after));
        sysLog.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateTime(new Date());
        sysLog.setStatus(1);
        sysLogMapper.insertSelective(sysLog);
    }
}
