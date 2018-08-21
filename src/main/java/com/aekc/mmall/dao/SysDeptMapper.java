package com.aekc.mmall.dao;

import com.aekc.mmall.model.SysDept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysDeptMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysDept record);

    int insertSelective(SysDept record);

    SysDept selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysDept record);

    int updateByPrimaryKey(SysDept record);

    //--------下面是自己添加的---------

    int countByNameAndParentId(@Param("parentId") Integer parentId, @Param("name") String name, @Param("id") Integer id);

    int countByParentId(Integer id);

    List<SysDept> getChildDeptListByLevel(String level);

    void batchUpdateLevel(List<SysDept> deptList);

    List<SysDept> selectAllDept();
}