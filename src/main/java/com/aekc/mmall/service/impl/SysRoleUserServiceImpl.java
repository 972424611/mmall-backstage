package com.aekc.mmall.service.impl;

import com.aekc.mmall.common.RequestHolder;
import com.aekc.mmall.dao.SysLogMapper;
import com.aekc.mmall.dao.SysRoleUserMapper;
import com.aekc.mmall.dao.SysUserMapper;
import com.aekc.mmall.enums.LogType;
import com.aekc.mmall.model.SysLogWithBLOBs;
import com.aekc.mmall.model.SysRoleUser;
import com.aekc.mmall.model.SysUser;
import com.aekc.mmall.service.SysRoleUserService;
import com.aekc.mmall.utils.IpUtil;
import com.aekc.mmall.utils.JsonUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SysRoleUserServiceImpl implements SysRoleUserService {

    @Autowired
    private SysRoleUserMapper sysRoleUserMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysLogMapper sysLogMapper;

    @Override
    public List<SysUser> getListByRoleId(int roleId) {
        List<Integer> userIdList = sysRoleUserMapper.selectUserIdListByRoleId(roleId);
        if(CollectionUtils.isEmpty(userIdList)) {
            return Lists.newArrayList();
        }
        return sysUserMapper.selectByIdList(userIdList);
    }

    @Override
    public void changeRoleUsers(int roleId, List<Integer> userIdList) {
        List<Integer> originUserIdList = sysRoleUserMapper.selectUserIdListByRoleId(roleId);
        if(originUserIdList.size() == userIdList.size()) {
            Set<Integer> originUserIdSet = new HashSet<>(originUserIdList);
            Set<Integer> userIdSet = new HashSet<>(userIdList);
            originUserIdSet.removeAll(userIdSet);
            if(CollectionUtils.isEmpty(originUserIdSet)) {
                return;
            }
        }
        updateRoleUsers(roleId, userIdList);
        saveRoleUserLog(roleId, originUserIdList, userIdList);
    }

    @Transactional(rollbackFor = Exception.class)
    protected void updateRoleUsers(int roleId, List<Integer> userIdList) {
        sysRoleUserMapper.deleteByRoleId(roleId);
        if(CollectionUtils.isEmpty(userIdList)) {
            return;
        }
        List<SysRoleUser> roleUserList = new ArrayList<>();
        for(Integer userId : userIdList) {
            SysRoleUser roleUser = new SysRoleUser();
            roleUser.setRoleId(roleId);
            roleUser.setUserId(userId);
            roleUser.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
            roleUser.setOperateTime(new Date());
            roleUser.setOperator(RequestHolder.getCurrentUser().getUsername());
            roleUserList.add(roleUser);
        }
        sysRoleUserMapper.batchInsert(roleUserList);
    }

    private void saveRoleUserLog(int roleId, List<Integer> before, List<Integer> after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ROLE_USER.getType());
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
