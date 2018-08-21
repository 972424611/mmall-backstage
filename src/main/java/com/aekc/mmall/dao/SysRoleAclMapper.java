package com.aekc.mmall.dao;

import com.aekc.mmall.model.SysRoleAcl;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysRoleAclMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysRoleAcl record);

    int insertSelective(SysRoleAcl record);

    SysRoleAcl selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRoleAcl record);

    int updateByPrimaryKey(SysRoleAcl record);

    //--------下面是自己添加的---------

    List<Integer> selectAclIdListByRoleIdList(@Param("roleIdList") List<Integer> roleIdList);

    void deleteByRoleId(int roleId);

    void batchInsert(@Param("roleAclList") List<SysRoleAcl> roleAclList);

    List<Integer> selectRoleIdListByAclId(int aclId);
}