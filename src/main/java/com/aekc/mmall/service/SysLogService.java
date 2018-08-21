package com.aekc.mmall.service;

import com.aekc.mmall.beans.PageQuery;
import com.aekc.mmall.beans.PageResult;
import com.aekc.mmall.model.*;
import com.aekc.mmall.param.SearchLogParam;

public interface SysLogService {

    void recover(int id);

    PageResult<SysLogWithBLOBs> searchPageList(SearchLogParam param, PageQuery page);


}
