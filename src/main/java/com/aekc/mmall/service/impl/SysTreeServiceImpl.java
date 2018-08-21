package com.aekc.mmall.service.impl;

import com.aekc.mmall.dao.SysAclMapper;
import com.aekc.mmall.dao.SysAclModuleMapper;
import com.aekc.mmall.dao.SysDeptMapper;
import com.aekc.mmall.dto.AclDto;
import com.aekc.mmall.dto.AclModuleLevelDto;
import com.aekc.mmall.dto.DeptLevelDto;
import com.aekc.mmall.model.SysAcl;
import com.aekc.mmall.model.SysAclModule;
import com.aekc.mmall.model.SysDept;
import com.aekc.mmall.service.SysCoreService;
import com.aekc.mmall.service.SysTreeService;
import com.aekc.mmall.utils.LevelUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysTreeServiceImpl implements SysTreeService {

    @Autowired
    private SysDeptMapper sysDeptMapper;

    @Autowired
    private SysAclModuleMapper sysAclModuleMapper;

    @Autowired
    private SysCoreService sysCoreService;

    @Autowired
    private SysAclMapper sysAclMapper;

    @Override
    public List<AclModuleLevelDto> userAclTree(int userId) {
        List<SysAcl> userAclList = sysCoreService.getUserAclList(userId);
        List<AclDto> aclDtoList = Lists.newArrayList();
        for(SysAcl acl : userAclList) {
            AclDto dto = AclDto.adapt(acl);
            dto.setHasAcl(true);
            dto.setChecked(true);
            aclDtoList.add(dto);
        }
        return aclListToTree(aclDtoList);
    }

//-------------------------------------------------------------------------------------------

    @Override
    public List<AclModuleLevelDto> roleTree(Integer roleId) {
        //1. 当前用户已分配的权限点
        List<SysAcl> userAclList = sysCoreService.getCurrentUserAclList();
        //2. 当前角色分配的权限点
        List<SysAcl> roleAclList = sysCoreService.getRoleAclList(roleId);
        //3. 当前系统的所有权限点
        List<SysAcl> allAclList = sysAclMapper.selectAllAcl();

        //java8新特性 取出SysAcl中的所有id放入set集合中
        Set<Integer> userAclIdSet = userAclList.stream().map(SysAcl::getId).collect(Collectors.toSet());
        Set<Integer> roleAclIdSet = roleAclList.stream().map(SysAcl::getId).collect(Collectors.toSet());

        List<AclDto> aclDtoList = Lists.newArrayList();
        for(SysAcl acl : allAclList) {
            AclDto dto = AclDto.adapt(acl);
            if(userAclIdSet.contains(acl.getId())) {
                // 如果权限勾选出现问题 就把所有的dto的hasAcl设置为true
                dto.setHasAcl(true);
            }
            if(roleAclIdSet.contains(acl.getId())) {
                dto.setChecked(true);
            }
            aclDtoList.add(dto);
        }
        return aclListToTree(aclDtoList);
    }

    private List<AclModuleLevelDto> aclListToTree(List<AclDto> aclDtoList) {
        if(CollectionUtils.isEmpty(aclDtoList)) {
            return Lists.newArrayList();
        }
        List<AclModuleLevelDto> aclModuleLevelList = aclModuleTree();
        Multimap<Integer, AclDto> moduleIdAclMap = ArrayListMultimap.create();
        for(AclDto acl : aclDtoList) {
            if(acl.getStatus() == 1) {
                moduleIdAclMap.put(acl.getAclModuleId(), acl);
            }
        }
        bindAclsWithOrder(aclModuleLevelList, moduleIdAclMap);
        return aclModuleLevelList;
    }

    private void bindAclsWithOrder(List<AclModuleLevelDto> aclModuleLevelList, Multimap<Integer, AclDto> moduleIdAclMap) {
        if(CollectionUtils.isEmpty(aclModuleLevelList)) {
            return;
        }
        for(AclModuleLevelDto dto : aclModuleLevelList) {
            List<AclDto> aclDtoList = (List<AclDto>) moduleIdAclMap.get(dto.getId());
            if(CollectionUtils.isNotEmpty(aclDtoList)) {
                aclDtoList.sort(aclSeqComparator);
                dto.setAclList(aclDtoList);
            }
            bindAclsWithOrder(dto.getAclModuleList(), moduleIdAclMap);
        }
    }

    private Comparator<AclDto> aclSeqComparator = new Comparator<AclDto>() {
        @Override
        public int compare(AclDto o1, AclDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };

//-------------------------------------------------------------------------------------------

    @Override
    public List<AclModuleLevelDto> aclModuleTree() {
        List<SysAclModule> aclModuleList = sysAclModuleMapper.selectAllAclModule();
        List<AclModuleLevelDto> dtoList = Lists.newArrayList();
        for(SysAclModule aclModule : aclModuleList) {
            dtoList.add(AclModuleLevelDto.adapt(aclModule));
        }
        return aclModuleListToTree(dtoList);
    }

    private List<AclModuleLevelDto> aclModuleListToTree(List<AclModuleLevelDto> dtoList) {
        if(CollectionUtils.isEmpty(dtoList)) {
            return Lists.newArrayList();
        }
        Multimap<String, AclModuleLevelDto> levelAclModuleMap = ArrayListMultimap.create();
        // 保存根节点即level为0
        List<AclModuleLevelDto> rootList = Lists.newArrayList();
        for(AclModuleLevelDto dto : dtoList) {
            levelAclModuleMap.put(dto.getLevel(), dto);
            if(LevelUtil.ROOT.equals(dto.getLevel())) {
                rootList.add(dto);
            }
        }
        rootList.sort(aclModuleSeqComparator);
        transformAclModuleTree(rootList, LevelUtil.ROOT, levelAclModuleMap);
        return rootList;
    }

    private void transformAclModuleTree(List<AclModuleLevelDto> dtoList, String level, Multimap<String, AclModuleLevelDto> levelAclModuleMap) {
        for(int i = 0; i < dtoList.size(); i++) {
            AclModuleLevelDto aclModuleLevelDto = dtoList.get(i);
            String nextLevel = LevelUtil.calculateLevel(level, aclModuleLevelDto.getId());
            List<AclModuleLevelDto> tempDeptList = (List<AclModuleLevelDto>) levelAclModuleMap.get(nextLevel);
            if(CollectionUtils.isNotEmpty(tempDeptList)) {
                tempDeptList.sort(aclModuleSeqComparator);
                aclModuleLevelDto.setAclModuleList(tempDeptList);
                transformAclModuleTree(tempDeptList, nextLevel, levelAclModuleMap);
            }
        }
    }

    private Comparator<AclModuleLevelDto> aclModuleSeqComparator = new Comparator<AclModuleLevelDto>() {
        @Override
        public int compare(AclModuleLevelDto o1, AclModuleLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };

//-------------------------------------------------------------------------------------------

    @Override
    public List<DeptLevelDto> deptTree() {
        List<SysDept> deptList = sysDeptMapper.selectAllDept();
        List<DeptLevelDto> dtoList = Lists.newArrayList();
        for(SysDept dept : deptList) {
            DeptLevelDto dto = DeptLevelDto.adapt(dept);
            dtoList.add(dto);
        }
        return deptListToTree(dtoList);
    }

    private List<DeptLevelDto> deptListToTree(List<DeptLevelDto> deptLevelDtoList) {
        if(CollectionUtils.isEmpty(deptLevelDtoList)) {
            return Lists.newArrayList();
        }
        // level -> [dept1, dept2] 如果key相同会自动把value变为Collection
        Multimap<String, DeptLevelDto> levelDeptMap = ArrayListMultimap.create();
        // 保存根节点即level为0
        List<DeptLevelDto> rootList = Lists.newArrayList();
        for(DeptLevelDto dto : deptLevelDtoList) {
            levelDeptMap.put(dto.getLevel(), dto);
            if(LevelUtil.ROOT.equals(dto.getLevel())) {
                rootList.add(dto);
            }
        }
        // 按照seq从小到大排序
        rootList.sort(new Comparator<DeptLevelDto>() {
            @Override
            public int compare(DeptLevelDto o1, DeptLevelDto o2) {
                return o1.getSeq() - o2.getSeq();
            }
        });
        //递归生成树
        transformDeptTree(rootList, LevelUtil.ROOT, levelDeptMap);
        return rootList;
    }

    private void transformDeptTree(List<DeptLevelDto> deptLevelDtoList, String level, Multimap<String, DeptLevelDto> levelDeptMap) {
        for (DeptLevelDto deptLevelDto : deptLevelDtoList) {
            //遍历该层的每个元素
            //处理当前层级的数据
            String nextLevel = LevelUtil.calculateLevel(level, deptLevelDto.getId());
            //处理下一层
            List<DeptLevelDto> tempDeptList = (List<DeptLevelDto>) levelDeptMap.get(nextLevel);
            if (CollectionUtils.isNotEmpty(tempDeptList)) {
                //排序
                tempDeptList.sort(deptSeqComparator);
                //设置下一层部门
                deptLevelDto.setDeptList(tempDeptList);
                //进入到下一层处理
                transformDeptTree(tempDeptList, nextLevel, levelDeptMap);
            }
        }
    }

    private Comparator<DeptLevelDto> deptSeqComparator = new Comparator<DeptLevelDto>() {
        @Override
        public int compare(DeptLevelDto o1, DeptLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };
}
