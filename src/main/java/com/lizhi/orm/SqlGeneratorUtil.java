package com.lizhi.orm;

import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMapping;

import java.util.regex.Pattern;

/**
 * @author lizhixiong
 * @time 2019 - 05 - 10 - 21:34
 */
public class SqlGeneratorUtil {
    public static final String RESULTMAPNAME_REG = "\'.+\'";

    public static boolean isIdFiled(ResultMapping resultMapping) {
        return resultMapping.getFlags() != null && !resultMapping.getFlags().isEmpty() && resultMapping.getFlags().get(0) instanceof ResultFlag;
    }

    public static String checkAndConvertResultMapName(String resultMapName) {
        if (!Pattern.matches(RESULTMAPNAME_REG, resultMapName)) {
            throw new IllegalArgumentException("resultmapid :" + resultMapName + "  illegal,must matches start with \"'\" and end with \"'\" ");
        }
        return resultMapName.substring(resultMapName.indexOf("'") + 1, resultMapName.lastIndexOf("'"));
    }
}
