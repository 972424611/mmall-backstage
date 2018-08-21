package com.aekc.mmall.dao;

import com.aekc.mmall.beans.PageQuery;
import com.aekc.mmall.model.SysAcl;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysAclMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysAcl record);

    int insertSelective(SysAcl record);

    SysAcl selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysAcl record);

    int updateByPrimaryKey(SysAcl record);

    //--------下面是自己添加的---------

    int countByNameAndAclModuleId(@Param("aclModuleId") int aclModuleId, @Param("name") String name, @Param("id") Integer id);

    int countByAclModuleId(int aclModuleId);

    List<SysAcl> selectPageByAclModuleId(@Param("aclModuleId") int aclModuleId, @Param("pageQuery") PageQuery pageQuery);

    List<SysAcl> selectByIdList(List<Integer> aclIdList);

    List<SysAcl> selectAllAcl();

    List<SysAcl> selectByUrl(String url);
}