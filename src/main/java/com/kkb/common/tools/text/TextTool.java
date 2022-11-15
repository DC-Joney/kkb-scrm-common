/*
 * Copyright
 */
package com.kkb.common.tools.text;

/**
 * 类描述
 *
 * @author ztkool
 * @since 1.0.0
 */
public class TextTool {

    public static String format(String pattern, Object... args) {
        TextMeta meta = TextFormatter.format(pattern, args);
        return meta.getMessage();
    }
}
