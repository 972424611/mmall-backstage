package com.aekc.mmall.dto;

import lombok.Data;

import java.util.Date;

@Data
public class SearchLogDto {

    private Integer type;

    private String beforeSeg;

    private String afterSeg;

    private String operator;

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    private Date fromTime;

    private Date toTime;
}
