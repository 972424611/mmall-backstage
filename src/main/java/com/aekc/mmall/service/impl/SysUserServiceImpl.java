package com.aekc.mmall.service.impl;

import com.aekc.mmall.beans.PageQuery;
import com.aekc.mmall.beans.PageResult;
import com.aekc.mmall.common.BeanValidator;
import com.aekc.mmall.common.RequestHolder;
import com.aekc.mmall.dao.SysUserMapper;
import com.aekc.mmall.exception.UserException;
import com.aekc.mmall.model.SysUser;
import com.aekc.mmall.param.UserParam;
import com.aekc.mmall.service.SysLogService;
import com.aekc.mmall.service.SysUserService;
import com.aekc.mmall.utils.IpUtil;
import com.aekc.mmall.utils.SecurityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysLogService sysLogService;

    private boolean checkEmailExist(String mail, Integer userId) {
        return sysUserMapper.countByMail(mail, userId) > 0;
    }

    private boolean checkTelephoneExist(String telephone, Integer userId) {
        return sysUserMapper.countByTelephone(telephone, userId) > 0;
    }

    @Override
    public void save(UserParam param) {
        BeanValidator.check(param);
        if(checkTelephoneExist(param.getTelephone(), param.getId())) {
            throw new UserException("电话已被占用");
        }
        if(checkEmailExist(param.getMail(), param.getId())) {
            throw new UserException("邮箱已被占用");
        }
        String password = SecurityUtil.encrypt(SecurityUtil.randomPassword());
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(param, sysUser);
        sysUser.setPassword(password);
        sysUser.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysUser.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysUser.setOperateTime(new Date());
        //TODO; sendEmail提示用户注册成功，并告知账号密码
        //MailUtil.send(new Mail());
        sysUserMapper.insertSelective(sysUser);
        sysLogService.saveUserLog(null, sysUser);
    }

    @Override
    public void update(UserParam param) {
        BeanValidator.check(param);
        if(checkTelephoneExist(param.getTelephone(), param.getId())) {
            throw new UserException("电话已被占用");
        }
        if(checkEmailExist(param.getMail(), param.getId())) {
            throw new UserException("邮箱已被占用");
        }
        SysUser before = sysUserMapper.selectByPrimaryKey(param.getId());
        if(before == null) {
            throw new UserException("待更新的用户不存在");
        }
        SysUser after = new SysUser();
        BeanUtils.copyProperties(param, after);
        after.setPassword(before.getPassword());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateTime(new Date());
        sysUserMapper.updateByPrimaryKeySelective(after);
        sysLogService.saveUserLog(before, after);
    }

    @Override
    public SysUser findByKeyword(String keyword) {
        return sysUserMapper.selectByKeyword(keyword);
    }

    @Override
    public PageResult<SysUser> getPageByDeptId(int deptId, PageQuery pageQuery) {
        BeanValidator.check(pageQuery);
        int count = sysUserMapper.countByDeptId(deptId);
        if(count > 0) {
            List<SysUser> list = sysUserMapper.selectPageByDeptId(deptId, pageQuery);
            PageResult<SysUser> pageResult = new PageResult<>();
            pageResult.setTotal(count);
            pageResult.setData(list);
            return pageResult;
        }
        return new PageResult<>();
    }

    @Override
    public List<SysUser> getAll() {
        return sysUserMapper.selectAllUser();
    }

    @Override
    public SysUser findByUserId(int userId) {
        return sysUserMapper.selectByPrimaryKey(userId);
    }

}
