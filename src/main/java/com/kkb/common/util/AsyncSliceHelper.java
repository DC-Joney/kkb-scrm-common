/*
 * description
 */
package com.kkb.common.util;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分片处理
 *
 * @author sy
 * @date 2021/9/16 1:11 下午
 * @since
 */
public final class AsyncSliceHelper {

    /**
     * 分片处理
     *
     * @param source
     * @param size
     * @param function
     * @param <S>
     * @param <R>
     * @return
     */
    public static <S, R> List<R> slice(List<S> source, int size, Function<List<S>, List<R>> function) {
        if (null == source) {
            return null;
        }
        Optional<List<R>> optional = Lists.partition(source, size)
                .parallelStream()
                .map(function)
                .reduce((slice1, slice2) -> {
                    slice1.addAll(slice2);
                    return slice1;
                });
        return optional.orElse(null);
    }

    public static <S, R> List<R> syncSlice(List<S> source, int size, Function<List<S>, List<R>> function) {
        if (null == source) {
            return null;
        }
        Optional<List<R>> optional = Lists.partition(source, size)
                .stream()
                .map(function)
                .reduce((slice1, slice2) -> {
                    slice1.addAll(slice2);
                    return slice1;
                });
        return optional.orElse(null);
    }

    public static void main(String[] args) {
        List<Integer> test = new ArrayList<>();
        for (int i = 0; i < 893; i++) {
            test.add(i);
        }
        List<String> result = AsyncSliceHelper.slice(test, 100, (slice) -> {
            return slice.stream()
                    .map(num -> "abc-" + num)
                    .collect(Collectors.toList());
        });
        Set<String> aaa = new HashSet<>();
        aaa.addAll(result);
        System.out.println(aaa.size());
        aaa.forEach(System.out::println);
    }
}
