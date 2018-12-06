package com.aekc.mmall.service.impl;

import com.aekc.mmall.beans.PageQuery;
import com.aekc.mmall.beans.PageResult;
import com.aekc.mmall.common.BeanValidator;
import com.aekc.mmall.common.RequestHolder;
import com.aekc.mmall.dao.SysAclMapper;
import com.aekc.mmall.exception.AclException;
import com.aekc.mmall.model.SysAcl;
import com.aekc.mmall.param.AclParam;
import com.aekc.mmall.security.InvocationSecurityMetadataSource;
import com.aekc.mmall.service.SysAclService;
import com.aekc.mmall.service.SysLogService;
import com.aekc.mmall.utils.IpUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class SysAclServiceImpl implements SysAclService {

    @Autowired
    private SysAclMapper sysAclMapper;

    @Autowired
    private SysLogService sysLogService;

    private boolean checkExist(int aclModuleId, String name, Integer id) {
        return sysAclMapper.countByNameAndAclModuleId(aclModuleId, name, id) > 0;
    }

    private String generateCode() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(new Date()) + "_" + new Random().nextInt(100);
    }

    @Override
    public void save(AclParam param) {
        BeanValidator.check(param);
        if (checkExist(param.getAclModuleId(), param.getName(), param.getId())) {
            throw new AclException("当前权限模块下面存在相同名的权限点");
        }
        SysAcl acl = new SysAcl();
        BeanUtils.copyProperties(param, acl, "id");
        acl.setCode(generateCode());
        acl.setOperator(RequestHolder.getCurrentUser().getUsername());
        acl.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        acl.setOperateTime(new Date());
        sysAclMapper.insertSelective(acl);
        sysLogService.saveAclLog(null, acl);
        // 提示权限更新了
        InvocationSecurityMetadataSource.update = true;
    }

    @Override
    public void update(AclParam param) {
        BeanValidator.check(param);
        if (checkExist(param.getAclModuleId(), param.getName(), param.getId())) {
            throw new AclException("当前权限模块下面存在相同名的权限点");
        }
        SysAcl before = sysAclMapper.selectByPrimaryKey(param.getId());
        if (before == null) {
            throw new AclException("待更新的权限点不存在");
        }
        SysAcl after = new SysAcl();
        BeanUtils.copyProperties(param, after);
        after.setCode(generateCode());
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        sysAclMapper.updateByPrimaryKeySelective(after);
        sysLogService.saveAclLog(before, after);
        // 提示权限更新了
        InvocationSecurityMetadataSource.update = true;
    }

    @Override
    public PageResult getPageByAclModuleId(int aclModuleId, PageQuery pageQuery) {
        BeanValidator.check(pageQuery);
        int count = sysAclMapper.countByAclModuleId(aclModuleId);
        if (count > 0) {
            List<SysAcl> aclList = sysAclMapper.selectPageByAclModuleId(aclModuleId, pageQuery);
            PageResult<SysAcl> pageResult = new PageResult<>();
            pageResult.setTotal(count);
            pageResult.setData(aclList);
            return pageResult;
        }
        return new PageResult<SysAcl>();
    }
}
