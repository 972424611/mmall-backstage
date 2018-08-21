package com.aekc.mmall.utils;

import com.google.common.base.Splitter;

import java.util.List;
import java.util.stream.Collectors;

public class StringUtil {

    /**
     *  1,2,3,,, 4,  --->   1 2 3 4
     */
    public static List<Integer> splitToListInt(String str) {
        List<String> strList = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(str);
        return strList.stream().map(strItem -> Integer.parseInt(strItem)).collect(Collectors.toList());
    }
}
