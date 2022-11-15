package com.kkb.common.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@UtilityClass
public class PageUtils {

    public <T> Page<T> applyPage(List<T> records, int page, int size) {
        List<T> pageData = records.stream()
                .skip((long) (page - 1) * size)
                .limit(size)
                .collect(Collectors.toList());
        return createPage(pageData, size, page, records.size());
    }

    public <T> Page<T> createPage(List<T> records, int size, int pageNum, int total) {
        Page<T> page = new Page<>(pageNum, size, total);
        page.setRecords(records);
        return page;
    }

    public static void main(String[] args) {
        List<Integer> collect = IntStream.rangeClosed(1, 100).boxed().collect(Collectors.toList());
        Page<Integer> objectPage = PageUtils.applyPage(collect, 3,16);
        System.out.println(objectPage);


    }


}
