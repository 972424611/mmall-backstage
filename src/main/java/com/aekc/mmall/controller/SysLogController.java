package com.aekc.mmall.controller;

import com.aekc.mmall.beans.PageQuery;
import com.aekc.mmall.common.JsonData;
import com.aekc.mmall.param.SearchLogParam;
import com.aekc.mmall.service.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/sys/log")
public class SysLogController {

    @Autowired
    private SysLogService sysLogService;

    @ResponseBody
    @RequestMapping(value = "/recover")
    public JsonData recover(@RequestParam("id") Integer id) {
        sysLogService.recover(id);
        return JsonData.success();
    }

    @ResponseBody
    @RequestMapping(value = "/page")
    public JsonData searchPage(SearchLogParam param, PageQuery page) {
        return JsonData.success(sysLogService.searchPageList(param, page));
    }
}
