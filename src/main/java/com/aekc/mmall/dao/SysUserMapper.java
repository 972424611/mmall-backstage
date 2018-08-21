package com.aekc.mmall.dao;

import com.aekc.mmall.beans.PageQuery;
import com.aekc.mmall.model.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserMapper {
    
    int deleteByPrimaryKey(Integer id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);

    //--------下面是自己添加的---------

    int countByMail(@Param("mail") String mail, @Param("id") Integer id);

    int countByTelephone(@Param("telephone") String telephone, @Param("id") Integer id);

    SysUser selectByKeyword(@Param("keyword") String keyword);

    int countByDeptId(@Param("deptId") int deptId);

    List<SysUser> selectPageByDeptId(@Param("deptId") int deptId, @Param("pageQuery") PageQuery pageQuery);

    List<SysUser> selectAllUser();

    List<SysUser> selectByIdList(@Param("userIdList") List<Integer> userIdList);
}