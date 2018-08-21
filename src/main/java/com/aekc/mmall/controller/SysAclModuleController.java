package com.aekc.mmall.controller;

import com.aekc.mmall.common.JsonData;
import com.aekc.mmall.dto.AclModuleLevelDto;
import com.aekc.mmall.param.AclModuleParam;
import com.aekc.mmall.service.SysAclModuleService;
import com.aekc.mmall.service.SysTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/sys/aclModule")
public class SysAclModuleController {

    @Autowired
    private SysAclModuleService sysAclModuleService;

    @Autowired
    private SysTreeService sysTreeService;

    @ResponseBody
    @RequestMapping(value = "/save")
    public JsonData saveAclModule(AclModuleParam param) {
        sysAclModuleService.save(param);
        return JsonData.success();
    }

    @ResponseBody
    @RequestMapping(value = "/update")
    public JsonData updateAclModule(AclModuleParam param) {
        sysAclModuleService.update(param);
        return JsonData.success();
    }

    @ResponseBody
    @RequestMapping(value = "/tree")
    public JsonData tree() {
        List<AclModuleLevelDto> dtoList = sysTreeService.aclModuleTree();
        return JsonData.success(dtoList);
    }

    @ResponseBody
    @RequestMapping(value = "/delete")
    public JsonData deleteAclModule(@RequestParam(value = "id") Integer aclModuleId) {
        sysAclModuleService.delete(aclModuleId);
        return JsonData.success();
    }
}
