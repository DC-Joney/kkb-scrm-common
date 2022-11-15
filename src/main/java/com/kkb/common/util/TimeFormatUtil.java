package com.kkb.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author duanxiangchao on 2021/10/14
 */
public class TimeFormatUtil {

    public static final Integer MINUTE = 1000 * 60;

    public static final Integer HOUR = 1000 * 60 * 60;

    public static final Integer DAY = 1000 * 60 * 60 * 24;

    private static final String MINUTE_STRING_FORMAT_1 = "%s分%s秒";

    private static final String MINUTE_STRING_FORMAT_2 = "%s:%s";

    private static final String MINUTE_STRING_FORMAT_3 = "%s分钟%s秒";

    private static final SimpleDateFormat SDF0 = new SimpleDateFormat("yyyy-MM-dd");

    private static final SimpleDateFormat SDF1 = new SimpleDateFormat("yyyyMMdd");

    private static final SimpleDateFormat SDF2 = new SimpleDateFormat("MM月dd日");

    private static final SimpleDateFormat SDF3 = new SimpleDateFormat("yyyy年MM月dd日");

    private static final SimpleDateFormat SDF4 = new SimpleDateFormat("yyyy年MM月dd日HH:mm");

    private static final SimpleDateFormat SDF5 = new SimpleDateFormat("MM.dd HH:mm");

    private static final SimpleDateFormat SDF6 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private static final SimpleDateFormat SDF7 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final SimpleDateFormat SDF8 = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    private static final SimpleDateFormat SDF9 = new SimpleDateFormat("MM.dd");






    public static final String getMinuteString1(Integer second){
        Integer minute = second/60;
        second = second%60;
        return String.format(MINUTE_STRING_FORMAT_1, minute, second);
    }

    public static final String getMinuteString2(Integer second){
        Integer minute = second/60;
        second = second%60;
        return String.format(MINUTE_STRING_FORMAT_2, minute, second);
    }

    public static Date getDate(Long time){
        if(time.toString().length() == 10){
            time = time * 1000;
        }
        return new Date(time);
    }

    public static String format0(Date date) {
        return SDF0.format(date);
    }

    public static String format1(Date date) {
        return SDF1.format(date);
    }

    public static String format2(Date date) {
        return SDF2.format(date);
    }

    public static String format3(Date date) {
        return SDF3.format(date);
    }

    public static String format4(Date date) {
        return SDF4.format(date);
    }

    public static String format5(Date date) {
        return SDF5.format(date);
    }

    public static String format6(Date date) {
        return SDF6.format(date);
    }

    public static String format7(Date date) {
        return SDF7.format(date);
    }

    public static String format8(Date date) {
        return SDF8.format(date);
    }

    public static String format9(Date date) {
        return SDF9.format(date);
    }




    public static Date parse0(String date) {
        try {
            return SDF0.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date parse1(String date) {
        try {
            return SDF1.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(getMinuteString1(100));
        System.out.println(getMinuteString1(100));
        System.out.println(System.currentTimeMillis());
        System.out.println(new Date(System.currentTimeMillis()));
    }
    public static Integer getCurrentYear(){
        return Calendar.getInstance().get(Calendar.YEAR);
    }

}
