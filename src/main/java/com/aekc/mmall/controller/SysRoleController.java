package com.aekc.mmall.controller;

import com.aekc.mmall.common.JsonData;
import com.aekc.mmall.dto.AclModuleLevelDto;
import com.aekc.mmall.model.SysRole;
import com.aekc.mmall.model.SysUser;
import com.aekc.mmall.param.RoleParam;
import com.aekc.mmall.service.*;
import com.aekc.mmall.utils.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/sys/role")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysTreeService sysTreeService;

    @Autowired
    private SysRoleAclService sysRoleAclService;

    @Autowired
    private SysRoleUserService sysRoleUserService;

    @Autowired
    private SysUserService sysUserService;

    @ResponseBody
    @RequestMapping("/save")
    public JsonData saveRole(RoleParam param) {
        sysRoleService.save(param);
        return JsonData.success();
    }

    @ResponseBody
    @RequestMapping("/update")
    public JsonData updateRole(RoleParam param) {
        sysRoleService.update(param);
        return JsonData.success();
    }

    @ResponseBody
    @RequestMapping("/list")
    public JsonData list() {
        List<SysRole> list = sysRoleService.getAll();
        return JsonData.success(list);
    }

    @ResponseBody
    @RequestMapping("/roleTree")
    public JsonData roleTree(@RequestParam("roleId") Integer roleId) {
        List<AclModuleLevelDto> aclModuleLevelDtoList = sysTreeService.roleTree(roleId);
        return JsonData.success(aclModuleLevelDtoList);
    }

    @ResponseBody
    @RequestMapping("/changeAcls")
    public JsonData changeAcls(@RequestParam("roleId") int roleId,
                               @RequestParam(value = "aclIds", required = false, defaultValue = "") String aclIds) {
        List<Integer> aclIdList = StringUtil.splitToListInt(aclIds);
        sysRoleAclService.changeRoleAcls(roleId, aclIdList);
        return JsonData.success();
    }

    @ResponseBody
    @RequestMapping("/changeUsers")
    public JsonData changeUsers(@RequestParam("roleId") int roleId,
                                @RequestParam(value = "userIds", required = false, defaultValue = "") String userIds) {
        List<Integer> userIdList = StringUtil.splitToListInt(userIds);
        sysRoleUserService.changeRoleUsers(roleId, userIdList);
        return JsonData.success();
    }

    @ResponseBody
    @RequestMapping("/users")
    public JsonData users(@RequestParam("roleId") int roleId) {
        List<SysUser> selectedUserList = sysRoleUserService.getListByRoleId(roleId);
        List<SysUser> allUserList = sysUserService.getAll();
        List<SysUser> unselectedUserList = Lists.newArrayList();

        Set<Integer> selectedUserIdSet = selectedUserList.stream().map(SysUser::getId).collect(Collectors.toSet());
        for (SysUser sysUser : allUserList) {
            // 大数据不要用到List的contains方法, 用set效率会高很多
            if (sysUser.getStatus() == 1 && !selectedUserIdSet.contains(sysUser.getId())) {
                unselectedUserList.add(sysUser);
            }
        }
        Map<String, List<SysUser>> map = Maps.newHashMap();
        map.put("selected", selectedUserList);
        map.put("unselected", unselectedUserList);
        return JsonData.success(map);
    }
}
