package com.kkb.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

    private final static long MICROSECONDS_TO_NANO = 1000;
    private final static long MILLISECONDS_TO_NANO = MICROSECONDS_TO_NANO * 1000;
    private final static long SECONDS_TO_NANO = MILLISECONDS_TO_NANO * 1000;
    private final static long MINUTES_TO_NANO = SECONDS_TO_NANO * 60;
    private final static long HOURS_TO_NANO = MINUTES_TO_NANO * 60;
    private final static long DAYS_TO_NANO = HOURS_TO_NANO * 24;

    public final static String PATTERN_19 = "yyyy-MM-dd HH:mm:ss";
    public final static String PATTERN_10 = "yyyy-MM-dd";
    public final static String PATTERN_8 = "HH:mm:ss";
    public final static String PATTERN_MM_DOT_DD = "MM.dd";

    public static int getCurrentTime() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    /**
     * 获取到当天结束还有多少毫秒
     *
     * @return
     */
    public static Long getEndTime() {
        Calendar curDate = Calendar.getInstance();
        Calendar nextDayDate = new GregorianCalendar(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH), curDate.get(Calendar.DATE) + 1, 0, 0, 0);
        return (nextDayDate.getTimeInMillis() - curDate.getTimeInMillis()) / 1000;
    }

    /**
     * 获取当我00:00
     *
     * @return
     */
    public static int getRemainingSeconds() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return (24 * 60 * 60) - (hour * 60 * 60);
    }

    //time 格式为 00:08:01
    public static int HHmmsstoTime(String time) {
        String[] split = time.split(":");
        int hh = Integer.parseInt(split[0]);
        int mm = Integer.parseInt(split[1]);
        int ss = Integer.parseInt(split[2]);
        return hh * 3600 + mm * 60 + ss;
    }

    /**
     * 毫秒转换为时间
     *
     * @param time
     */
    public static String second2Time(Integer time) {
        if (time == null || time == 0) {
            return "0分钟";
        }
        if (time < 60) {
            return "1分钟";
        }
        int hour = time / 3600;
        int minute = time % 3600 / 60;
        if (hour == 0 && minute != 0) {
            return String.format("%s%s", minute, "分钟");
        } else if (hour != 0 && minute == 0) {
            return String.format("%s%s", hour, "小时");
        } else {
            return String.format("%s%s%s%s", hour, "小时", minute, "分钟");
        }
    }

    /**
     * 将秒数转换为日时分秒，
     * 7783 格式为 02:09:43
     *
     * @param second
     * @return
     */
    public static String secondToTime(Long second) {
        if (second == null) {
            return null;
        }
        long hours = second / 3600;             //转换小时
        second = second % 3600;                 //剩余秒数
        long minutes = second / 60;             //转换分钟
        second = second % 60;                   //剩余秒数
        return String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", second);
    }

    //时间戳转日期
    public static String getTimestamp2DateStr(String seconds, String format) {
        if (seconds == null || seconds.isEmpty()) {
            return null;
        }
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds) * 1000));
    }

    //时间戳转日期
    public static String getTimestamp2DateStr(Date date, String format) {
        if (date == null) {
            return null;
        }
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static String getFormatyyyyMMdd(Date date) {
        return getTimestamp2DateStr(date, "yyyy-MM-dd");
    }


    //将时间转换为时间戳
    public static Integer dateToStamp(String times, String format) {
        if (times == null || times.isEmpty()) {
            return null;
        }
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = simpleDateFormat.parse(times);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Math.toIntExact(date.getTime() / 1000);
    }

    public static Date getFormatDate(String date) {
        return getformatDate(date, null);
    }

    public static Date getformatDate(String date, String format) {
        if (date == null || date.isEmpty()) {
            return null;
        }
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将秒单位改成日期格式
     *
     * @param seconds
     * @return
     */
    public static String getDateFromSeconds(Long seconds) {
        Date date = new Date(seconds * 1000);
        return formatDateByPattern(date, "yyyy-MM-dd HH:mm:ss");
    }


    public static String getformatDate(Date date, String format) {
        if (date == null) {
            return null;
        }
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }


    //时间戳转日期
    public static Date getTimestamp2Date(String seconds) {
        if (seconds == null || seconds.isEmpty()) {
            return null;
        }
        return new Date(Long.valueOf(seconds) * 1000);
    }

    /***
     *
     * @param date
     * @param format : e.g:yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String formatDateByPattern(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String formatTimeStr = null;
        if (date != null) {
            formatTimeStr = sdf.format(date);
        }
        return formatTimeStr;
    }

    public static String format(Date date, String format) {
        return formatDateByPattern(date, format);
    }

    public static String format(Date date) {
        return formatDateByPattern(date, PATTERN_19);
    }

    public static String formatNow() {
        return formatDateByPattern(new Date(), PATTERN_19);
    }

    public static String formatDefaultNow(Date date) {
        if (null == date) {
            date = new Date();
        }
        return formatDateByPattern(date, PATTERN_19);
    }


    /***
     * convert Date to cron ,eg.  "0 07 10 15 1 ? 2016"
     * @param date  : 时间点
     * @return
     */
    public static String getCron(Date date) {
        String dateFormat = "ss mm HH dd MM ? yyyy";
        return formatDateByPattern(date, dateFormat);
    }

    public static String getCron(String seconds) {
        Date date = getTimestamp2Date(seconds);
        return getCron(date);
    }


    public static Long getUnixTime(String date) {
        return getformatDate(date, null).getTime() / 1000;
    }

    public static Long getUnixTime(Date date) {
        return (date.getTime() / 1000);
    }


    /**
     * 设置日期时间-精确到秒00:00:00
     *
     * @param date
     * @return
     */
    public static Date getStartTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 设置日期时间-精确到秒23:59:59
     *
     * @param date
     * @return
     */
    public static Date getEndTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * 昨天
     *
     * @return
     */
    public static Date getYesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar.getTime();
    }

    public static Date getToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 0);
        return calendar.getTime();
    }

    public static Date getAmountBefore(Date date, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, amount);
        return calendar.getTime();
    }

    /**
     * 获取几天后零点
     *
     * @param seconds
     * @param days
     * @return
     */
    public static Integer getStartPlusDays(Integer seconds, int days) {
        return Math.toIntExact(getStartTime(TimeUtils.getAmountBefore(new Date(Long.valueOf(seconds) * 1000), days)).getTime() / 1000);
    }

    /**
     * 获取今天的 23:59:59 的时间戳 （秒级别的）
     *
     * @return
     */
    public static Integer getTodayLastTime() {
        LocalDateTime todayLastTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return Math.toIntExact(todayLastTime.toEpochSecond(ZoneOffset.of("+8")));
    }

    /**
     * 获取几天胡的 23:59:59 的时间戳 （秒级别的）
     *
     * @return
     */
    public static Integer getAfterDaysLastTime(Integer days) {
        LocalDateTime todayLastTime = LocalDateTime.of(LocalDate.now().plusDays(days), LocalTime.MAX);
        return Math.toIntExact(todayLastTime.toEpochSecond(ZoneOffset.of("+8")));
    }

    /**
     * 获取几天后23:59:59
     *
     * @param seconds
     * @param days
     * @return
     */
    public static Integer getEndPlusDays(Integer seconds, int days) {
        return Math.toIntExact(getEndTime(TimeUtils.getAmountBefore(new Date(Long.valueOf(seconds) * 1000), days - 1)).getTime() / 1000);
    }

    public static Date getMinuteBefore(int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, amount);
        return calendar.getTime();
    }

    /**
     * 秒转毫秒
     *
     * @param second
     * @return
     */
    public static Integer second2Millisecond(String second) {
        if (second == null || second.length() == 0) {
            return null;
        }
        Double ms = Double.valueOf(second) * 1000;
        return ms.intValue();
    }

    /**
     * 当前时间是 12:25:12， 先获取5分钟前的时间即分钟数-5为 12:20:12
     *
     * @param amount 分钟
     * @return
     */
    public static String getCurrentAmountTime(Integer amount, String format) {
        Calendar beforeTime = Calendar.getInstance();
        beforeTime.add(Calendar.MINUTE, -(amount == null ? 5 : amount));
        Date beforeD = beforeTime.getTime();
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        return new SimpleDateFormat(format).format(beforeD);
    }


    public static long getNowTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return calendar.getTimeInMillis();
    }

    /**
     * 获取当天凌晨12点整点时间-秒
     *
     * @return
     */
    public static Long getTwelveOclockTime() {
        //当天零点
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        //获取秒数
        Long second = todayStart.toEpochSecond(ZoneOffset.of("+8"));
        return second;
    }

    private static void appendTime(StringBuilder sb, int time, boolean isSecond) {
        if (time == 0) {
            sb.append("00");
        } else {
            if (time < 10) {
                sb.append("0" + time);
            } else {
                sb.append(time);
            }
        }
        if (!isSecond) {
            sb.append(":");
        }
    }

    public static String getHourMinuteSecondStr(Integer time) {
        int h = time / 3600;
        int m = (time % 3600) / 60;
        int s = (time % 3600) % 60;
        StringBuilder sb = new StringBuilder();
        appendTime(sb, h, false);
        appendTime(sb, m, false);
        appendTime(sb, s, true);
        return sb.toString();
    }

    public static Date parseDate(String ymd) {
        return TimeUtils.getformatDate(ymd, PATTERN_10);
    }

    public static Date parseTime(String hms) {
        return TimeUtils.getformatDate(hms, PATTERN_8);
    }

    public static Date parse(String ymd, String hms) {
        String realTime = ymd + " " + hms;
        return TimeUtils.getformatDate(realTime, PATTERN_19);
    }

    public static Date parse(Date ymd, String hms) {
        Date date = new Date();
        date.setTime(ymd.getTime());
        Date time = TimeUtils.getformatDate(hms, PATTERN_8);
        date.setHours(time.getHours());
        date.setMinutes(time.getMinutes());
        date.setSeconds(time.getSeconds());
        return date;
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        return toLocalDateTime(date, ZoneId.systemDefault());
    }

    public static LocalDateTime toLocalDateTime(Date date, ZoneId zoneId) {
        Instant instant = date.toInstant();
        return instant.atZone(zoneId).toLocalDateTime();
    }

    public static boolean isBefore(LocalDateTime before, LocalDateTime after, TimeUnit timeUnit, int time) {
        if (null == before || null == after || null == timeUnit) {
            throw new IllegalArgumentException("before、after、timeUnit should not null.");
        }
        if (time < 0) {
            throw new IllegalArgumentException("time should greater than 0.");
        }
        if (before.isAfter(after)) {
            return false;
        }
        int beforeNano = before.getNano();
        int afterNano = after.getNano();
        long afterNum = after.toEpochSecond(ZoneOffset.UTC) * SECONDS_TO_NANO + afterNano;
        long beforeNum = before.toEpochSecond(ZoneOffset.UTC) * SECONDS_TO_NANO + beforeNano;
        double diff = afterNum - beforeNum;
        switch (timeUnit) {
            case DAYS:
                return diff / DAYS_TO_NANO >= time;
            case HOURS:
                return diff / HOURS_TO_NANO >= time;
            case MINUTES:
                return diff / MINUTES_TO_NANO >= time;
            case SECONDS:
                return diff / SECONDS_TO_NANO >= time;
            case MILLISECONDS:
                return diff / MILLISECONDS_TO_NANO >= time;
            case MICROSECONDS:
                return diff / MICROSECONDS_TO_NANO >= time;
            case NANOSECONDS:
                return diff >= time;
            default:
                break;
        }
        return true;
    }

    public static boolean isBefore(Date before, Date after, TimeUnit timeUnit, int time) {
        if (null == before || null == after || null == timeUnit) {
            throw new IllegalArgumentException("before、after、timeUnit should not null.");
        }
        if (time < 0) {
            throw new IllegalArgumentException("time should greater than 0.");
        }
        LocalDateTime beforeLocal = toLocalDateTime(before);
        LocalDateTime afterLocal = toLocalDateTime(after);
        return isBefore(beforeLocal, afterLocal, timeUnit, time);
    }

    public static Date plusMinutes(Date date, int minutes) {
        if (null == date) {
            date = new Date();
        }
        return new Date(date.getTime() + minutes * 60 * 1000);
    }

    public static Date plusMinutes(int minutes) {
        return plusMinutes(new Date(), minutes);
    }

    public static Date plusDays(Date date, int days) {
        if (null == date) {
            date = new Date();
        }
        return new Date(date.getTime() + days * 24 * 60 * 60 * 1000);
    }

    public static Date plusDays(int days) {
        return plusDays(new Date(), days);
    }

    public static void main(String[] args) {
        System.out.println(new Date());
        System.out.println(plusDays(new Date(), -10));
        System.out.println(getStartTime(new Date()));
        System.out.println(getEndTime(new Date()));
    }

}

