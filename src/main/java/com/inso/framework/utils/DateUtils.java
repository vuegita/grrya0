package com.inso.framework.utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateUtils {

    public static final String TYPE_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String TYPE_YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";
    public static final String TYPE_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String TYPE_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    
    
    public static final String TYPE_YYYYMMDD = "yyyyMMdd";
    public static final String TYPE_YYYY_MM = "yyyy-MM";
    public static final String TYPE_YYYY_MM_DD = "yyyy-MM-dd";

    public static final String Year = "Year";
    public static final String Month = "Month";
    public static final String Day = "Day";

    private static final Map<String, SimpleDateFormat> maps = new HashMap<String, SimpleDateFormat>();

    public static String convertString(Date date) {
        return convertString(TYPE_YYYYMMDDHHMMSS, date);
    }

    public static String convertString(Date date, String parttern) {
        return convertString(parttern, date);
    }

    public static String convertString(String parttern, Date date) {
        return convertString(parttern, new DateTime(date));
    }

    public static String convertString(String parttern, DateTime dateTime) {
        if (StringUtils.isEmpty(parttern) || dateTime == null) {
            return null;
        }
        DateTimeFormatter fmt = DateTimeFormat.forPattern(parttern);
        return fmt.print(dateTime);
    }

    public static Date convertDate(String parttern, String dateString) {
        if (StringUtils.isEmpty(parttern) || StringUtils.isEmpty(dateString)) {
            return null;
        }
        DateTimeFormatter fmt = DateTimeFormat.forPattern(parttern);
        DateTime dateTime = fmt.parseDateTime(dateString);
        return dateTime.toDate();
    }

	public static Date getEndOfDay(Date date) {
		LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());;
		LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
		return Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
	}

    public static Date getStartOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }


    public static Date getNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, +1);//+1今天的时间加一天
        date = calendar.getTime();
        return date;
    }


    public static Date getMinute(Date date, int addMinute) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, addMinute);
        return cal.getTime();
    }

    public static Date getPreDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);//+1今天的时间加一天
        date = calendar.getTime();
        return date;
    }

    public static Date getPreDay(Date date,int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -n);
        date = calendar.getTime();
        return date;
    }

    public static String getHHMM(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);//小时
        int minute = cal.get(Calendar.MINUTE);//分
        String time = hour + ":" + minute;
        return time;
    }

    //获取给定日期，给定时间的时间戳
    public static Long getTime(Date date, String times) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);//获取年份
        int month = cal.get(Calendar.MONTH) + 1;//获取月份
        int day = cal.get(Calendar.DATE);//获取日
        Long aLong = new DateTime().withYear(year).withMonthOfYear(month).withDayOfMonth(day).withHourOfDay(Integer.parseInt(times.split(":")[0])).
                withMinuteOfHour(Integer.parseInt(times.split(":")[1])).withSecondOfMinute(0).getMillis() / 1000 * 1000;
        return aLong;
    }
    
    public static int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);//获取年份
        return year;
    }
    
    public static int getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH) + 1;//获取年份
        return month;
    }

    //获取当前时间戳
    public static Long getNowTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);
        return date.getTime();
    }

    public static Date getTimeSecond(Date date, int second) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, second);
        return cal.getTime();
    }

    //获取时间戳
    public static long getMill(int hour,int minute,int second){
        return new DateTime().withHourOfDay(hour).withMinuteOfHour(minute).withSecondOfMinute(second).getMillis()/1000*1000;
    }

    /**
     * 两个日期相差天数(date1比date2小)
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDays(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);
        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2)   //同一年
        {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0)    //闰年
                {
                    timeDistance += 366;
                } else    //不是闰年
                {
                    timeDistance += 365;
                }
            }
            return timeDistance + (day2 - day1);
        } else    //不同年
        {
            return day2 - day1;
        }
    }

    //获取当前星期(1:周日; 2:周一 ;3;周二;4;周三;5;周四;6;周五;7;周六)
    public static int getWeekNo() {
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        int weekday = c.get(Calendar.DAY_OF_WEEK);
        return weekday;
    }

    //根据参数获取当前星期(1:周日; 2:周一 ;3;周二;4;周三;5;周四;6;周五;7;周六)
    public static int getWeekNo(Date dte) {
        Calendar c = Calendar.getInstance();
        c.setTime(dte);
        int weekday = c.get(Calendar.DAY_OF_WEEK);
        return weekday;
    }

    //获取当前时间多加n天
    public static String getOpenDay(int i) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, i);
        return sf.format(c.getTime());
    }

    //转为时间戳类型
    public static Long getOpenTime(String openTime) {
        Long aLong = 0L;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date dateStart = format.parse(openTime);
            aLong = (Long) (dateStart.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aLong;
    }

    //判断当前时间是否在指定时间内
    public static boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }
    public static Date getWeekStartDate(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        Date date = cal.getTime();
        return date;
    }

    public static Date addSecond(Date date, int addSecond){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, addSecond);
        return cal.getTime();
    }

    public static int getHH(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        return hour;
    }
    //获取当前时间减n天
    public static String getCleanDayDetails(int times) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -times);
        return sf.format(c.getTime());
    }
    
    public static String getBeginTimeOfDay(String yyyyMMdd) {
        return yyyyMMdd + " 00:00:00";
    }
    public static String getBeginTimeOfDayTwo(String yyyyMMdd) {
        return yyyyMMdd + "000000";
    }

    public static String getEndTimeOfDay(String yyyyMMdd) {
        return yyyyMMdd + " 23:59:59";
    }

    public static String getEndTimeOfDayTwo(String yyyyMMdd) {
        return yyyyMMdd + "235959";
    }


    public static DateTime getDateTimeByYyyyMMddHHmmss(String yyyyMMddHHmmss) {
        try {
            String[] tempArray = yyyyMMddHHmmss.split(" ");
            String[] yyyyMMdd = tempArray[0].split("-");
            String[] HHmmss = tempArray[1].split(":");

            return new DateTime(Integer.parseInt(yyyyMMdd[0])
                    ,Integer.parseInt(yyyyMMdd[1])
                    ,Integer.parseInt(yyyyMMdd[2])
                    ,Integer.parseInt(HHmmss[0])
                    ,Integer.parseInt(HHmmss[1])
                    ,Integer.parseInt(HHmmss[2]));
        }catch (Exception e){

        }
        return null;
    }

    public static DateTime getDateTimeByHHmm(String HHmm) {
        DateTime now = DateTime.now();
        String yyyyMMdd = now.toString(TYPE_YYYY_MM_DD);
        return getDateTimeByYyyyMMddHHmmss(yyyyMMdd + " " + HHmm + ":00");
    }

    public static void main(String[] args) {
        String start = "2019-06-03";
        int startIssue = 2019062;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            long from = df.parse(start).getTime();
            long to = getWeekStartDate().getTime();
            long i = (to - from) / (1000 * 3600 * 24 * 7);
            System.out.println("相差周数为："+(to-from)/(1000*3600*24*7));
            int endIssue = startIssue + (int) i * 3;
            System.out.println("qqqqqqqqqqqqqqqqqqqqqq：" + endIssue);
        } catch (Exception e) {
            e.printStackTrace();
        }
       System.out.println("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqq"+getCleanDayDetails(60));
    }
    
    

}
