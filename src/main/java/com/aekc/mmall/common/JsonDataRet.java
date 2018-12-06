package com.aekc.mmall.common;

import lombok.Data;

@Data
public class JsonDataRet extends JsonData {

    /**
     * 登录后自动跳转到登录前的页面
     */
    private String retPage;

    public JsonDataRet(boolean ret) {
        setRet(ret);
    }

    public static JsonDataRet success(Object object, String retPage) {
        JsonDataRet jsonDataRet = new JsonDataRet(true);
        jsonDataRet.setData(object);
        jsonDataRet.setRetPage(retPage);
        return jsonDataRet;
    }
}
