package com.aekc.mmall.service.impl;

import com.aekc.mmall.beans.PageQuery;
import com.aekc.mmall.beans.PageResult;
import com.aekc.mmall.common.BeanValidator;
import com.aekc.mmall.common.RequestHolder;
import com.aekc.mmall.dao.*;
import com.aekc.mmall.dto.SearchLogDto;
import com.aekc.mmall.enums.LogType;
import com.aekc.mmall.exception.LogException;
import com.aekc.mmall.model.*;
import com.aekc.mmall.param.SearchLogParam;
import com.aekc.mmall.service.SysLogService;
import com.aekc.mmall.service.SysRoleAclService;
import com.aekc.mmall.service.SysRoleUserService;
import com.aekc.mmall.utils.IpUtil;
import com.aekc.mmall.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class SysLogServiceImpl implements SysLogService {

    @Autowired
    private SysLogMapper sysLogMapper;

    @Autowired
    private SysDeptMapper sysDeptMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysAclModuleMapper sysAclModuleMapper;

    @Autowired
    private SysAclMapper sysAclMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRoleAclService sysRoleAclService;

    @Autowired
    private SysRoleUserService sysRoleUserService;

    private Object recover(Object before, SysLogWithBLOBs sysLogWithBLOBs) {
        if(before == null) {
            throw new LogException("待还原的部门已经不存在了");
        }
        if(StringUtils.isBlank(sysLogWithBLOBs.getNewValue())
                || StringUtils.isBlank(sysLogWithBLOBs.getOldValue())) {
            throw new LogException("新增和删除操作不做还原");
        }
        SysDept after = JsonUtil.jsonToPojo(sysLogWithBLOBs.getOldValue(), SysDept.class);
        assert after != null;
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateTime(new Date());
        return after;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void recover(int id) {
        SysLogWithBLOBs sysLogWithBLOBs = sysLogMapper.selectByPrimaryKey(id);
        if(sysLogWithBLOBs == null) {
            throw new LogException("待还原的记录不存在");
        }
        LogType logType = LogType.getLogType(sysLogWithBLOBs.getType());
        assert logType != null;
        switch(logType) {
            case TYPE_DEPT:
                SysDept beforeDept = sysDeptMapper.selectByPrimaryKey(sysLogWithBLOBs.getTargetId());
                SysDept afterDept = (SysDept) recover(beforeDept, sysLogWithBLOBs);
                sysDeptMapper.updateByPrimaryKeySelective(afterDept);
                saveDeptLog(beforeDept, afterDept);
                break;
            case TYPE_USER:
                SysUser beforeUser = sysUserMapper.selectByPrimaryKey(sysLogWithBLOBs.getTargetId());
                SysUser afterUser = (SysUser) recover(beforeUser, sysLogWithBLOBs);
                sysUserMapper.updateByPrimaryKeySelective(afterUser);
                saveUserLog(beforeUser, afterUser);
                break;
            case TYPE_ACL_MODULE:
                SysAclModule beforeAclModule = sysAclModuleMapper.selectByPrimaryKey(sysLogWithBLOBs.getTargetId());
                SysAclModule afterAclModule = (SysAclModule) recover(beforeAclModule, sysLogWithBLOBs);
                sysAclModuleMapper.updateByPrimaryKeySelective(afterAclModule);
                saveAclModuleLog(beforeAclModule, afterAclModule);
                break;
            case TYPE_ACL:
                SysAcl beforeAcl = sysAclMapper.selectByPrimaryKey(sysLogWithBLOBs.getTargetId());
                SysAcl afterAcl = (SysAcl) recover(beforeAcl, sysLogWithBLOBs);
                sysAclMapper.updateByPrimaryKeySelective(afterAcl);
                saveAclLog(beforeAcl, afterAcl);
                break;
            case TYPE_ROLE:
                SysRole beforeRole = sysRoleMapper.selectByPrimaryKey(sysLogWithBLOBs.getTargetId());
                SysRole afterRole = (SysRole) recover(beforeRole, sysLogWithBLOBs);
                sysRoleMapper.updateByPrimaryKeySelective(afterRole);
                saveRoleLog(beforeRole, afterRole);
                break;
            case TYPE_ROLE_ACL:
                SysRole aclRole = sysRoleMapper.selectByPrimaryKey(sysLogWithBLOBs.getTargetId());
                if(aclRole == null) {
                    throw new LogException("角色已经不存在了");
                }
                sysRoleAclService.changeRoleAcls(sysLogWithBLOBs.getTargetId(),
                        JsonUtil.jsonToPojo(sysLogWithBLOBs.getOldValue(), List.class));
            case TYPE_ROLE_USER:
                SysRole userRole = sysRoleMapper.selectByPrimaryKey(sysLogWithBLOBs.getTargetId());
                if(userRole == null) {
                    throw new LogException("角色已经不存在了");
                }
                sysRoleUserService.changeRoleUsers(sysLogWithBLOBs.getTargetId(),
                        JsonUtil.jsonToPojo(sysLogWithBLOBs.getOldValue(), List.class));
            default:
                break;
        }
    }

    private SysLogWithBLOBs save(Object before, Object after, int beforeId, int afterId, int type) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(type);
        sysLog.setTargetId(after == null ? beforeId : afterId);
        sysLog.setOldValue(before == null ? "" : JsonUtil.objectToJson(before));
        sysLog.setNewValue(after == null ? "" : JsonUtil.objectToJson(after));
        sysLog.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateTime(new Date());
        sysLog.setStatus(1);
        return sysLog;
    }

    private void saveDeptLog(SysDept before, SysDept after) {
        sysLogMapper.
                insertSelective(save(before, after, before.getId(), after.getId(), LogType.TYPE_DEPT.getType()));
    }

    private void saveUserLog(SysUser before, SysUser after) {
        sysLogMapper.
                insertSelective(save(before, after, before.getId(), after.getId(), LogType.TYPE_USER.getType()));
    }

    private void saveAclModuleLog(SysAclModule before, SysAclModule after) {
        sysLogMapper.
                insertSelective(save(before, after, before.getId(), after.getId(), LogType.TYPE_ACL_MODULE.getType()));
    }

    private void saveAclLog(SysAcl before, SysAcl after) {
        sysLogMapper.
                insertSelective(save(before, after, before.getId(), after.getId(), LogType.TYPE_ACL.getType()));
    }

    private void saveRoleLog(SysRole before, SysRole after) {
        sysLogMapper.
                insertSelective(save(before, after, before.getId(), after.getId(), LogType.TYPE_ROLE.getType()));
    }

    @Override
    public PageResult<SysLogWithBLOBs> searchPageList(SearchLogParam param, PageQuery page) {
        BeanValidator.check(page);
        SearchLogDto dto = new SearchLogDto();
        dto.setType(param.getType());
        if(StringUtils.isNotBlank(param.getBeforeSeg())) {
            dto.setBeforeSeg("%" + param.getBeforeSeg() + "%");
        }
        if(StringUtils.isNotBlank(param.getAfterSeg())) {
            dto.setAfterSeg("%" + param.getAfterSeg() + "%");
        }
        if(StringUtils.isNotBlank(param.getOperator())) {
            dto.setOperator("%" + param.getOperator() + "%");
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if(StringUtils.isNotBlank(param.getFromTime())) {
                dto.setFromTime(dateFormat.parse(param.getFromTime()));
            }
            if(StringUtils.isNotBlank(param.getToTime())) {
                dto.setToTime(dateFormat.parse(param.getToTime()));
            }
        } catch (Exception e) {
            throw new LogException("传入的日期格式有问题, 正确的格式为: yyyy-MM-dd HH:mm:ss");
        }
        int count = sysLogMapper.countBySearchDto(dto);
        if(count > 0) {
            List<SysLogWithBLOBs> logList = sysLogMapper.selectPageListBySearchDto(dto, page);
            PageResult<SysLogWithBLOBs> pageResult = new PageResult<>();
            pageResult.setTotal(count);
            pageResult.setData(logList);
            return pageResult;
        }
        return new PageResult<>();
    }


}
