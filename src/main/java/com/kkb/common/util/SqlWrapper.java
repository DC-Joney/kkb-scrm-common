package com.kkb.common.util;

import com.google.common.base.Predicates;
import io.vavr.API;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.is;


/**
 * @author zhangyang
 */
@Slf4j
public class SqlWrapper {

    private static final String LIKE_STR = "%";

    public static String likeLeft(String column, boolean left) {
        return wrapLike(column, left, false);
    }

    public static String likeRight(String column, boolean right) {
        return wrapLike(column, false, right);
    }

    public static String wrapFullLike(String column) {
        return wrapLike(column, true, true);
    }

    public static String wrapFullLike(Number column) {
        return wrapLike(String.valueOf(column), true, true);
    }

    public static String wrapLike(String column, boolean left, boolean right) {

        if (StringUtils.isEmpty(column)) {
            log.warn("Wrap column to like search is empty，The column str is {}", column);
            throw new NullPointerException("The column value must not be null");
        }


        if (left)
            column = LIKE_STR.concat(column);

        if (right)
            column = column.concat(LIKE_STR);

        return column;
    }

}
