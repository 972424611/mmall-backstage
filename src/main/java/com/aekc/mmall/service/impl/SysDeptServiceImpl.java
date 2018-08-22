package com.aekc.mmall.service.impl;

import com.aekc.mmall.common.BeanValidator;
import com.aekc.mmall.common.RequestHolder;
import com.aekc.mmall.dao.SysDeptMapper;
import com.aekc.mmall.dao.SysUserMapper;
import com.aekc.mmall.exception.DeptException;
import com.aekc.mmall.model.SysDept;
import com.aekc.mmall.param.DeptParam;
import com.aekc.mmall.service.SysDeptService;
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
public class SysDeptServiceImpl implements SysDeptService {

    @Autowired
    private SysDeptMapper sysDeptMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysLogService sysLogService;

    private boolean checkExist(Integer parentId, String deptName, Integer deptId) {
        return sysDeptMapper.countByNameAndParentId(parentId, deptName, deptId) > 0;
    }

    private String getLevel(Integer deptId) {
        SysDept dept = sysDeptMapper.selectByPrimaryKey(deptId);
        if(dept == null) {
            return null;
        }
        return dept.getLevel();
    }

    @Override
    public void delete(Integer deptId) {
        SysDept dept = sysDeptMapper.selectByPrimaryKey(deptId);
        if(dept == null) {
            throw new DeptException("待删除的部门不存在, 无法删除");
        }
        if(sysDeptMapper.countByParentId(dept.getId()) > 0) {
            throw new DeptException("当前部门下面有子部门，无法删除");
        }
        if(sysUserMapper.countByDeptId(dept.getId()) > 0) {
            throw new DeptException("当前部门下面有用户，无法删除");
        }
        sysDeptMapper.deleteByPrimaryKey(deptId);
    }

    @Override
    public void save(DeptParam param) {
        BeanValidator.check(param);
        if(checkExist(param.getParentId(), param.getName(), param.getId())) {
            throw new DeptException("在同一层级下存在相同名称的部门");
        }
        SysDept dept = new SysDept();
        BeanUtils.copyProperties(param, dept);
        String deptLevel = getLevel(param.getParentId());
        dept.setLevel(LevelUtil.calculateLevel(deptLevel, param.getParentId()));
        dept.setOperator(RequestHolder.getCurrentUser().getUsername());
        dept.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        dept.setOperateTime(new Date());
        //insertSelective()只插入有值值
        sysDeptMapper.insertSelective(dept);
        sysLogService.saveDeptLog(null, dept);
    }

    @Override
    public void update(DeptParam param) {
        BeanValidator.check(param);
        if(checkExist(param.getParentId(), param.getName(), param.getId())) {
            throw new DeptException("在同一层级下存在相同名称的部门");
        }
        SysDept before = sysDeptMapper.selectByPrimaryKey(param.getId());
        if(before == null) {
            throw new DeptException("待更新的部门不存在");
        }
        SysDept after = new SysDept();
        BeanUtils.copyProperties(param, after);
        String deptLevel = getLevel(param.getParentId());
        after.setLevel(LevelUtil.calculateLevel(deptLevel, param.getParentId()));
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        sysLogService.saveDeptLog(before, after);

    }

    @Transactional(rollbackFor = Exception.class)
    protected void updateWithChild(SysDept before, SysDept after) {
        String newLevelPrefix = after.getLevel();
        String oldLevelPrefix = before.getLevel();
        if(!after.getLevel().equals(before.getLevel())) {
            List<SysDept> deptList = sysDeptMapper.getChildDeptListByLevel(before.getLevel());
            if(CollectionUtils.isNotEmpty(deptList)) {
                for(SysDept dept : deptList) {
                    String level = dept.getLevel();
                    if(level.indexOf(oldLevelPrefix) == 0) {
                        level = newLevelPrefix + level.substring(oldLevelPrefix.length());
                        dept.setLevel(level);
                    }
                }
                sysDeptMapper.batchUpdateLevel(deptList);
            }
        }
        sysDeptMapper.updateByPrimaryKey(after);
    }
}
