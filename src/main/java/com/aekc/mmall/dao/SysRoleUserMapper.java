package com.aekc.mmall.dao;

import com.aekc.mmall.model.SysRoleUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysRoleUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysRoleUser record);

    int insertSelective(SysRoleUser record);

    SysRoleUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRoleUser record);

    int updateByPrimaryKey(SysRoleUser record);

    List<Integer> selectUserIdListByRoleId(int roleId);

    //--------下面是自己添加的---------

    void deleteByRoleId(int roleId);

    void batchInsert(@Param("roleUserList") List<SysRoleUser> roleUserList);

    List<Integer> selectRoleIdListByUserId(int userId);

    List<Integer> selectUserIdListByRoleIdList(@Param("roleIdList") List<Integer> roleIdList);
}