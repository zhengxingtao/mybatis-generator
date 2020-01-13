package com.src.common.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by src on 2018/7/3.
 */
public class DateUtil {


    /**
     * 时间 格式：yyyy-MM-dd hh:mm:ss
     * @return String
     */
    public static String format$yyyy_MM_dd$HH_mm_ss(Date date) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }
    public static String format$yyyyMMddHHmmss(Date date) {
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.format(date);
    }
    public static String format$yyyy_MM_dd(Date date) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }
    public static String formatHH_mm_ss(Date date) {
        DateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(date);
    }
    public static String formatDateAccordingToFormatStr(Date date, String formatStr){
        DateFormat format = new SimpleDateFormat(formatStr);
        return format.format(date);
    }

    /**
     * @describe: 获取某个月的月末时间
     * @Date:2018/7/3 下午12:58
     * @Author:src
     * @param changMonth 0 当前月份
     *                   -1 上个月
     *                   1 下个月
     *                   数字加减以此类推
     * @return 时间精确到 23：59：59秒
     */
    public static long getMonthLastDayTime(int changMonth){
        Calendar c= Calendar.getInstance();
        c.add(Calendar.MONTH, changMonth);
        int lastMonthMaxDay=c.getActualMaximum(Calendar.DAY_OF_MONTH);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), lastMonthMaxDay, 23, 59, 59);
        //按格式输出
        return c.getTimeInMillis();
    }
    /**
     * @describe: 获取某个月的月初时间
     * @Date:2018/7/3 下午12:58
     * @Author:src
     * @param changMonth 0 当前月份
     *                   -1 上个月
     *                   1 下个月
     *                   数字加减以此类推
     * @return 时间精确到 00：00：00
     */
    public static long getMonthFirstDayTime(int changMonth){
        Calendar c= Calendar.getInstance();
        c.add(Calendar.MONTH, changMonth);
        int firstMonthMaxDay=c.getActualMinimum(Calendar.DAY_OF_MONTH);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), firstMonthMaxDay, 00, 00, 00);
        //按格式输出
        return c.getTimeInMillis();
    }

}
