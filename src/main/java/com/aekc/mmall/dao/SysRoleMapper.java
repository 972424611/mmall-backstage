package com.aekc.mmall.dao;

import com.aekc.mmall.model.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysRoleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysRole record);

    int insertSelective(SysRole record);

    SysRole selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRole record);

    int updateByPrimaryKey(SysRole record);

    //--------下面是自己添加的---------

    int countByName(@Param("name") String name, @Param("id") Integer id);

    List<SysRole> selectAllRole();

    List<SysRole> selectByIdList(@Param("roleIdList") List<Integer> roleIdList);
}