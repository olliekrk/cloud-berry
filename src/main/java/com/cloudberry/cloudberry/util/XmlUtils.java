package com.cloudberry.cloudberry.util;


import org.apache.commons.lang3.StringUtils;

public class XmlUtils {

    public static final String XML_OPEN_OPEN_SIGN = "<";
    public static final String XML_CLOSE_SIGN = ">";
    public static final String XML_OPEN_CLOSE_SIGN = "</";

    public static String getTagName(String raw) {
        return StringUtils.substringBetween(raw, XML_OPEN_OPEN_SIGN, XML_CLOSE_SIGN);
    }

    public static String getClosingTagName(String raw) {
        return StringUtils.substringBetween(raw, XML_OPEN_CLOSE_SIGN, XML_CLOSE_SIGN);
    }
}
