package com.kkb.common.tools.concurrent.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Enum工具类
 *
 * @author zhangyang
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UniversalEnumUtils {

    private static final String CSV_EXTENSION = "csv";

    /**
     *
     * @param fileName excel文件名称
     */
    public static boolean isValidateExcel(String fileName){
        return Optional.of(FilenameUtils.getExtension(fileName))
                .filter(StringUtils::hasText)
                .map(name -> EnumUtils.isValidEnum(ExcelTypeEnum.class,name.toUpperCase()))
                .orElse(false);
    }

    /**
     * @param fileName csv文件名称
     */
    public static boolean isCsvFile(String fileName) {
        return Optional.of(FilenameUtils.getExtension(fileName))
                .filter(StringUtils::hasText)
                .map(name -> name.equalsIgnoreCase(CSV_EXTENSION))
                .orElse(false);
    }


    @Getter
    @AllArgsConstructor
    public enum ExcelTypeEnum {

        /**
         * XLS
         */
        XLS("xls"),

        /**
         * XLSX
         */
        XLSX("xlsx");

        private String type;

    }
}
