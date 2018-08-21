package com.aekc.mmall.controller;

import com.aekc.mmall.beans.PageQuery;
import com.aekc.mmall.beans.PageResult;
import com.aekc.mmall.common.JsonData;
import com.aekc.mmall.model.SysRole;
import com.aekc.mmall.param.AclParam;
import com.aekc.mmall.service.SysAclService;
import com.aekc.mmall.service.SysRoleService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/sys/acl")
public class SysAclController {

    @Autowired
    private SysAclService sysAclService;

    @Autowired
    private SysRoleService sysRoleService;

    @ResponseBody
    @RequestMapping(value = "/save")
    public JsonData saveAcl(AclParam param) {
        sysAclService.save(param);
        return JsonData.success();
    }

    @ResponseBody
    @RequestMapping(value = "/update")
    public JsonData updateAcl(AclParam param) {
        sysAclService.update(param);
        return JsonData.success();
    }

    @ResponseBody
    @RequestMapping(value = "/page")
    public JsonData list(@RequestParam(value = "aclModuleId") Integer aclModuleId, PageQuery pageQuery) {
        PageResult pageResult = sysAclService.getPageByAclModuleId(aclModuleId, pageQuery);
        return JsonData.success(pageResult);
    }

    @ResponseBody
    @RequestMapping(value = "/acls")
    public JsonData acls(@RequestParam(value = "aclId") Integer aclId) {
        Map<String, Object> map = Maps.newHashMap();
        List<SysRole> roleList = sysRoleService.getRoleListByAclId(aclId);
        map.put("roles", roleList);
        map.put("users", sysRoleService.getUserListByRoleList(roleList));
        return JsonData.success(map);
    }
}
