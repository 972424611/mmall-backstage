package com.aekc.mmall.model;

import lombok.Data;

import java.util.Date;

@Data
public abstract class SysBase {

    private Integer id;

    private String operator;

    private Date operateTime;

    private String operateIp;
}
