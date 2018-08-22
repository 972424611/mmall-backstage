package com.aekc.mmall.dao;

import com.aekc.mmall.model.SysAclModule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysAclModuleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysAclModule record);

    int insertSelective(SysAclModule record);

    SysAclModule selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysAclModule record);

    int updateByPrimaryKey(SysAclModule record);

    //--------下面是自己添加的---------

    int countByNameAndParentId(@Param("parentId") Integer parentId, @Param("name") String aclModuleName, @Param("id") Integer aclModuleId);

    List<SysAclModule> selectChildAclModuleListByLevel(String level);

    void batchUpdateLevel(@Param("aclModuleList") List<SysAclModule> aclModuleList);

    int countByParentId(Integer id);

    List<SysAclModule> selectAllAclModule();
}