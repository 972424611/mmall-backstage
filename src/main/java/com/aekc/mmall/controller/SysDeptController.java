package com.aekc.mmall.controller;

import com.aekc.mmall.common.JsonData;
import com.aekc.mmall.dto.DeptLevelDto;
import com.aekc.mmall.param.DeptParam;
import com.aekc.mmall.service.SysDeptService;
import com.aekc.mmall.service.SysTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "/sys/dept")
public class SysDeptController {

    @Autowired
    private SysDeptService sysDeptService;

    @Autowired
    private SysTreeService sysTreeService;

    @ResponseBody
    @RequestMapping(value = "/delete")
    public JsonData deleteDept(HttpServletRequest request) {
        String deptId = request.getParameter("id");
        sysDeptService.delete(Integer.valueOf(deptId));
        return JsonData.success();
    }

    @ResponseBody
    @RequestMapping(value = "/save")
    public JsonData saveDept(DeptParam param) {
        sysDeptService.save(param);
        return JsonData.success();
    }

    @ResponseBody
    @RequestMapping(value = "/tree")
    public JsonData tree() {
        List<DeptLevelDto> dtoList = sysTreeService.deptTree();
        return JsonData.success(dtoList);
    }

    @ResponseBody
    @RequestMapping(value = "/update")
    public JsonData updateDept(DeptParam param) {
        sysDeptService.update(param);
        return JsonData.success();
    }
}
