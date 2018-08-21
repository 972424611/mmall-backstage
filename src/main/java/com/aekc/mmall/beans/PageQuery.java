package com.aekc.mmall.beans;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class PageQuery {

    @Min(value = 1, message = "当前页码不合法")
    private int pageNo = 1;

    @Min(value = 1, message = "每页展示的数量不合法")
    private int pageSize = 10;

    private int offset;
}
