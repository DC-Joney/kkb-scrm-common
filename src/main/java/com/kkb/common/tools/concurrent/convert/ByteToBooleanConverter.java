package com.kkb.common.tools.concurrent.convert;

import org.springframework.core.convert.converter.Converter;

/**
 * TODO
 *
 * @author zhangyang
 * @date 2020-10-14
 */
public class ByteToBooleanConverter implements Converter<Byte, Boolean> {

    private static final Byte ONE = (byte) 1;
    private static final Byte ZERO = (byte) 0;

    @Override
    public Boolean convert(Byte source) {

        if (ONE.equals(source)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }
}
