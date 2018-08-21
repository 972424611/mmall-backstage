package com.aekc.mmall.dto;

import com.aekc.mmall.model.SysDept;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class DeptLevelDto extends SysDept {

    private List<DeptLevelDto> deptList = new ArrayList<>();

    public static DeptLevelDto adapt(SysDept dept) {
        DeptLevelDto deptLevelDto = new DeptLevelDto();
        BeanUtils.copyProperties(dept, deptLevelDto);
        return deptLevelDto;
    }
}
