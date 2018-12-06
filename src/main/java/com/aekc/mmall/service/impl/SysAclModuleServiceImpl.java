package com.aekc.mmall.service.impl;

import com.aekc.mmall.common.BeanValidator;
import com.aekc.mmall.common.RequestHolder;
import com.aekc.mmall.dao.SysAclMapper;
import com.aekc.mmall.dao.SysAclModuleMapper;
import com.aekc.mmall.exception.AclModuleException;
import com.aekc.mmall.model.SysAclModule;
import com.aekc.mmall.param.AclModuleParam;
import com.aekc.mmall.service.SysAclModuleService;
import com.aekc.mmall.service.SysLogService;
import com.aekc.mmall.utils.IpUtil;
import com.aekc.mmall.utils.LevelUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class SysAclModuleServiceImpl implements SysAclModuleService {

    @Autowired
    private SysAclModuleMapper sysAclModuleMapper;

    @Autowired
    private SysAclMapper sysAclMapper;

    @Autowired
    private SysLogService sysLogService;

    private boolean checkExist(Integer parentId, String aclModuleName, Integer aclModuleId) {
        return sysAclModuleMapper.countByNameAndParentId(parentId, aclModuleName, aclModuleId) > 0;
    }

    private String getLevel(Integer aclModuleId) {
        SysAclModule aclModule = sysAclModuleMapper.selectByPrimaryKey(aclModuleId);
        if (aclModule == null) {
            return null;
        }
        return aclModule.getLevel();
    }

    @Override
    public void save(AclModuleParam param) {
        BeanValidator.check(param);
        if (checkExist(param.getParentId(), param.getName(), param.getId())) {
            throw new AclModuleException("同一层级下存在相同名称的权限模块");
        }
        SysAclModule aclModule = new SysAclModule();
        BeanUtils.copyProperties(param, aclModule);
        String aclModuleLevel = getLevel(param.getParentId());
        aclModule.setLevel(LevelUtil.calculateLevel(aclModuleLevel, param.getParentId()));
        aclModule.setOperator(RequestHolder.getCurrentUser().getUsername());
        aclModule.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        aclModule.setOperateTime(new Date());
        sysAclModuleMapper.insertSelective(aclModule);
        sysLogService.saveAclModuleLog(null, aclModule);
    }

    @Override
    public void update(AclModuleParam param) {
        BeanValidator.check(param);
        if (checkExist(param.getParentId(), param.getName(), param.getId())) {
            throw new AclModuleException("同一层级下存在相同名称的权限模块");
        }
        SysAclModule before = sysAclModuleMapper.selectByPrimaryKey(param.getId());
        if (before == null) {
            throw new AclModuleException("将更新的权限模块不存在");
        }
        SysAclModule after = new SysAclModule();
        BeanUtils.copyProperties(param, after);
        String aclModuleLevel = getLevel(param.getParentId());
        after.setLevel(LevelUtil.calculateLevel(aclModuleLevel, param.getParentId()));
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        updateWithChild(before, after);
        sysLogService.saveAclModuleLog(before, after);
    }

    @Transactional(rollbackFor = Exception.class)
    protected void updateWithChild(SysAclModule before, SysAclModule after) {
        String newLevelPrefix = after.getLevel();
        String oldLevelPrefix = before.getLevel();
        if (!after.getLevel().equals(before.getLevel())) {
            List<SysAclModule> aclModuleList = sysAclModuleMapper.selectChildAclModuleListByLevel(before.getLevel());
            if (CollectionUtils.isNotEmpty(aclModuleList)) {
                for (SysAclModule aclModule : aclModuleList) {
                    String level = aclModule.getLevel();
                    if (level.indexOf(oldLevelPrefix) == 0) {
                        level = newLevelPrefix + level.substring(oldLevelPrefix.length());
                        aclModule.setLevel(level);
                    }
                }
                sysAclModuleMapper.batchUpdateLevel(aclModuleList);
            }
        }
        sysAclModuleMapper.updateByPrimaryKeySelective(after);
    }

    @Override
    public void delete(Integer aclModuleId) {
        SysAclModule aclModule = sysAclModuleMapper.selectByPrimaryKey(aclModuleId);
        if (aclModule == null) {
            throw new AclModuleException("待删除的权限模块不存在, 无法删除");
        }
        if (sysAclModuleMapper.countByParentId(aclModule.getId()) > 0) {
            throw new AclModuleException("当前模块下面有子模块, 无法删除");
        }
        if (sysAclMapper.countByAclModuleId(aclModule.getId()) > 0) {
            throw new AclModuleException("当前模块下有权限点, 无法删除");
        }
        sysAclModuleMapper.deleteByPrimaryKey(aclModuleId);
    }
}
