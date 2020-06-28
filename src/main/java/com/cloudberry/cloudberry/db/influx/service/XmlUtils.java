package com.cloudberry.cloudberry.db.influx.service;


import org.apache.commons.lang3.StringUtils;

public class XmlUtils {
    static String getTagName(String raw) {
        return StringUtils.substringBetween(raw, "<", ">");
    }
    static String getClosingTagName(String raw) {
        return StringUtils.substringBetween(raw, "</", ">");
    }
}
