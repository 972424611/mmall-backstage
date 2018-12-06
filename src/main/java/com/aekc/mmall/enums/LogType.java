package com.aekc.mmall.enums;

import lombok.Getter;

@Getter
public enum LogType {

    TYPE_DEPT(1),

    TYPE_USER(2),

    TYPE_ACL_MODULE(3),

    TYPE_ACL(4),

    TYPE_ROLE(5),

    TYPE_ROLE_ACL(6),

    TYPE_ROLE_USER(7);

    private int type;

    LogType(int type) {
        this.type = type;
    }

    public static LogType getLogType(int type) {
        LogType[] logTypes = LogType.values();
        for (LogType logType : logTypes) {
            if (logType.getType() == type) {
                return logType;
            }
        }
        return null;
    }
}
