package com.aekc.mmall.param;

import lombok.Data;

@Data
public class SearchLogParam {

    private Integer type;

    private String beforeSeg;

    private String afterSeg;

    private String operator;

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    private String fromTime;

    private String toTime;

}
