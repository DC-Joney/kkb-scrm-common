package com.kkb.common.tools.concurrent.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * TODO
 *
 * @author zhangyang
 * @date 2020-09-11
 */
public final class CharactorToBooleanConverter implements Converter<Character, Boolean> {

    private static final Set<String> trueValues = new HashSet<String>(4);

    private static final Set<String> falseValues = new HashSet<String>(4);

    static {
        trueValues.add("true");
        trueValues.add("on");
        trueValues.add("yes");
        trueValues.add("1");

        falseValues.add("false");
        falseValues.add("off");
        falseValues.add("no");
        falseValues.add("0");
    }

    @Override
    public Boolean convert(Character source) {
        if (source != null) {
            String value = source.toString();
            if (StringUtils.hasText(value)) {
                value = value.trim().toLowerCase();

                if (trueValues.contains(value)) {
                    return Boolean.TRUE;
                } else if (falseValues.contains(value)) {
                    return Boolean.FALSE;
                } else {
                    throw new IllegalArgumentException("Invalid boolean value '" + source + "'");
                }
            }
        }
        return false;
    }
}
