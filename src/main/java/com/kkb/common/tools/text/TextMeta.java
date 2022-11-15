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
final class TextMeta {

    private String pattern;
    private Object[] args;
    private String message;

    TextMeta(String pattern, Object[] args) {
        this.pattern = pattern;
        this.args = args;
    }

    TextMeta message(String message) {
        this.message = message;
        return this;
    }

    public String getPattern() {
        return pattern;
    }

    public Object[] getArgs() {
        return args;
    }

    public String getMessage() {
        return message;
    }

}
