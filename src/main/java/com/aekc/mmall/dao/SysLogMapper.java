package com.aekc.mmall.dao;

import com.aekc.mmall.beans.PageQuery;
import com.aekc.mmall.dto.SearchLogDto;
import com.aekc.mmall.model.SysLog;
import com.aekc.mmall.model.SysLogWithBLOBs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysLogWithBLOBs record);

    int insertSelective(SysLogWithBLOBs record);

    SysLogWithBLOBs selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysLogWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(SysLogWithBLOBs record);

    int updateByPrimaryKey(SysLog record);

    //--------下面是自己添加的---------

    int countBySearchDto(SearchLogDto dto);

    List<SysLogWithBLOBs> getPageListBySearchDto(@Param("dto") SearchLogDto dto, @Param("page") PageQuery page);
}