package com.kkb.common.util;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 项目名称：kkb-scrm-hacker-data-server
 * 类名称：DateUtils
 * 类描述：日期常用工具类方法
 * 创建人：YuanGL
 * 创建时间：2021年4月26日11:27:34
 * version 1.0
 */
public class DateUtils {

    public static final String PATTERN_STANDARD = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final String PATTERN_YYYYMMDD = "yyyyMMdd";
    public static final String PATTERN_yyyyMMddHHmmss = "yyyyMMddHHmmss";
    public static final String PATTERN_yyyyMMddHHmm = "yyyyMMddHHmm";

    public static Date nowWithoutMicrosecond (){
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date string2Date(String strDate, String pattern) {
        if (StringUtils.isEmpty(strDate)) {
            throw new RuntimeException("String Date is null");
        }
        if (StringUtils.isEmpty(pattern)) {
            pattern = PATTERN_DATE;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date;
        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return date;
    }


    public static Date string2Date(String strDate) {
        if (StringUtils.isEmpty(strDate)) {
            throw new RuntimeException("String Date is null");
        }
        SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_STANDARD);
        Date date;
        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return date;
    }

    public static String date2String(Date date, String pattern) {
        Assert.notNull(date);
        if (StringUtils.isEmpty(pattern)) {
            pattern = PATTERN_STANDARD;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static Date getStartTimeOfThisDay() {
        return getStartTimeOfThisDay(new Date());
    }

    public static Date getStartTimeOfThisDay(Date date) {
        Assert.notNull(date);
        String strDateTime = date2String(date, PATTERN_DATE) + " 00:00:00";
        return string2Date(strDateTime, PATTERN_STANDARD);
    }

    public static Date getEndTimeOfThisDay() {
        return getEndTimeOfThisDay(new Date());
    }

    public static Date getEndTimeOfThisDay(Date date) {
        Assert.notNull(date);
        String strDateTime = date2String(date, PATTERN_DATE) + " 23:59:59";
        return string2Date(strDateTime, PATTERN_STANDARD);
    }

    public static Date getAddDay(Date date, Integer day) {
        Assert.notNull(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, day);
        return calendar.getTime();
    }

    public static String getDate(String pattern){
        Assert.notNull(pattern);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(Calendar.getInstance().getTime());
    }

    /**
     * 根据日期获取当天是周几
     * @param datetime 日期
     * @return 周几
     */
    public static String dateToWeek(String datetime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        Date date;
        try {
            date = sdf.parse(datetime);
            cal.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        return weekDays[w];
    }

    // 获取本周开始时间
    public static Date getBeginDayOfWeek() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayofweek == 1) {
            dayofweek += 7;
        }
        cal.add(Calendar.DATE, 2 - dayofweek);
        return getStartTimeOfThisDay(cal.getTime());
    }

    // 获取本周结束时间
    public static Date getEndDayOfWeek(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(getBeginDayOfWeek());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        Date weekEndSta = cal.getTime();
        return getEndTimeOfThisDay(weekEndSta);
    }

    public static void main(String[] args) {
        System.out.println(DateUtils.date2String(DateUtils.getEndDayOfWeek(),null));
        System.out.println(DateUtils.date2String(new Date(),DateUtils.PATTERN_STANDARD));
    }
}
