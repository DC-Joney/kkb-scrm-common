package com.kkb.common.util;


import com.kkb.common.core.exception.KkbBusinessException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * cron 构建器 （秒 分 时 日 月 周 年） （粗略处理）
 *
 * @author sy
 * @date 2021-09-22 06:46
 * @since
 */
public class CronUtils {

    private final static String HMS_SPLIT = ":";
    private final static String YMD_SPLIT = "-";
    private final static int TIME_SPLIT_LEN = 3;
    private final static String TIME_PATTERN_19 = "yyyy-MM-dd HH:mm:ss";
    private final static DateTimeFormatter FORMATTER_19 = DateTimeFormatter.ofPattern(TIME_PATTERN_19);

    public static String atOnceTime(String ymd, String hms) {
        // todo 检查参数格式是否正确
        return new Builder(ymd, hms).build();
    }

    /**
     * 执行一次，如果时间超过当前时间了，加一天
     *
     * @param timeStr 2021-10-10 11:12:13
     * @return
     */
    public static String atOnce(String timeStr) {
        if (null == timeStr) {
            return null;
        }
        LocalDateTime parse = LocalDateTime.parse(timeStr, FORMATTER_19);
        return new Builder(parse).build();
    }

    /**
     * 执行一次，如果时间超过当前时间了，加一天
     *
     * @param timeStr 11:12:13
     * @return
     */
    public static String atOnceTime(String timeStr) {
        String[] split = timeStr.split(HMS_SPLIT);
        LocalDateTime now = LocalDateTime.now();
        if (split.length == TIME_SPLIT_LEN) {
            String hour = split[0];
            String minute = split[1];
            String second = split[2];
            LocalTime localTime = LocalTime.of(Integer.parseInt(hour),
                    Integer.parseInt(minute),
                    Integer.parseInt(second));
            if (localTime.isBefore(LocalTime.now())) {
                now = now.plusDays(1);
            }
        } else {
            throw KkbBusinessException.of(CommonError.PARAMETER_ERROR);
        }
        return new Builder(timeStr, now).build();
    }

    /**
     * 每日 固定时间 循环
     *
     * @param timeStr
     * @return
     */
    public static String dayTimeCycle(String timeStr) {
        return new Builder(timeStr).build();
    }

    /**
     * 每周 固定时间 循环（每周某几天）
     *
     * @param timeStr
     * @param weeks
     * @return
     */
    public static String weekTimeCycle(String timeStr, List<Integer> weeks) {
        return new Builder(timeStr)
                .inWeeks(weeks)
                .build();
    }

    /**
     * 每月 固定时间 循环（每月某几天）
     *
     * @param timeStr
     * @param days
     * @return
     */
    public static String monthTimeCycle(String timeStr, List<Integer> days) {
        return new Builder(timeStr)
                .inDays(days)
                .build();
    }

    private static class Builder {
        private final String[] result = {
                "*", " ",
                "*", " ",
                "*", " ",
                "*", " ",
                "*", " ",
                "?", " ",
                "*"};

        public Builder(String timeStr) {
            String[] split = timeStr.split(HMS_SPLIT);
            if (split.length == TIME_SPLIT_LEN) {
                result[0] = split[2];
                result[2] = split[1];
                result[4] = split[0];
            }
        }

        public Builder(LocalDateTime date) {
            int year = date.getYear();
            int month = date.getMonthValue();
            int dayOfMonth = date.getDayOfMonth();
            int hour = date.getHour();
            int minute = date.getMinute();
            int second = date.getSecond();
            result[0] = String.valueOf(second);
            result[2] = String.valueOf(minute);
            result[4] = String.valueOf(hour);
            result[6] = String.valueOf(dayOfMonth);
            result[8] = String.valueOf(month);
            result[12] = String.valueOf(year);
        }

        public Builder(String timeStr, LocalDateTime date) {
            String[] split = timeStr.split(HMS_SPLIT);
            if (split.length == TIME_SPLIT_LEN) {
                result[0] = split[2];
                result[2] = split[1];
                result[4] = split[0];
            }
            int year = date.getYear();
            int month = date.getMonthValue();
            int dayOfMonth = date.getDayOfMonth();
            result[6] = String.valueOf(dayOfMonth);
            result[8] = String.valueOf(month);
            result[12] = String.valueOf(year);

        }

        public Builder(String ymd, String hms) {
            String[] hmsSplit = hms.split(HMS_SPLIT);
            if (hmsSplit.length == TIME_SPLIT_LEN) {
                result[0] = hmsSplit[2];
                result[2] = hmsSplit[1];
                result[4] = hmsSplit[0];
            }
            String[] ymdSplit = ymd.split(YMD_SPLIT);
            if (ymdSplit.length == TIME_SPLIT_LEN) {
                result[6] = ymdSplit[2];
                result[8] = ymdSplit[1];
                result[12] = ymdSplit[0];
            }
        }

        public Builder inDays(List<Integer> values) {
            if (in(values, 6, "*")) {
                // week 清除
                result[10] = "?";
            }
            return this;
        }

        public Builder inWeeks(List<Integer> values) {
            if (in(values, 10, "?", true)) {
                // 日、月清除
                result[8] = "*";
                result[6] = "?";
            }
            return this;
        }

        private boolean in(List<Integer> values, int index, String defaultValue) {
            return in(values, index, defaultValue, false);
        }

        private boolean in(List<Integer> values, int index, String defaultValue, boolean isWeek) {
            if (null != values && values.size() > 0) {
                StringBuilder builder = new StringBuilder();
                for (Integer value : values) {
                    if (isWeek) {
                        String name = CronWeeks.getName(value);
                        if (null == name) {
                            result[index] = defaultValue;
                            return false;
                        }
                        builder.append(name);
                    } else {
                        builder.append(value);
                    }
                    builder.append(",");
                }
                builder.delete(builder.length() - 1, builder.length());
                result[index] = builder.toString();
                return true;
            } else {
                result[index] = defaultValue;
            }
            return false;
        }

        public String build() {
            StringBuilder stringBuilder = new StringBuilder();
            for (String str : result) {
                stringBuilder.append(str);
            }
            return stringBuilder.toString();
        }
    }

    private enum CronWeeks {

        MON(1),
        TUE(2),
        WED(3),
        THU(4),
        FRI(5),
        SAT(6),
        SUN(7);

        private int value;

        private static Map<Integer, CronWeeks> cache = new HashMap<>();

        static {
            for (CronWeeks week : CronWeeks.values()) {
                cache.put(week.value, week);
            }
        }

        CronWeeks(int value) {
            this.value = value;
        }

        public static String getName(int value) {
            CronWeeks weeks = cache.get(value);
            if (null == weeks) {
                return null;
            }
            return weeks.name();
        }
    }

    public static void main(String[] args) {
        System.out.println(CronUtils.atOnce("2021-12-12 15:33:55"));
        System.out.println(CronUtils.atOnceTime("12:33:55"));
    }


}
