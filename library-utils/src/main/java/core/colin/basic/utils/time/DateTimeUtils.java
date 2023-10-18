package core.colin.basic.utils.time;

import android.text.TextUtils;

import com.blankj.utilcode.util.StringUtils;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author PengLin
 * @desc
 * @date 2017/8/9
 */

public class DateTimeUtils {

    /**
     * 时间格式
     */
    public final static String FORMAT_PATTERN_1 = "yyyy年MM月dd日 HH:mm";
    public final static String FORMAT_PATTERN_2 = "yyyy-MM-dd HH:mm";
    public final static String FORMAT_PATTERN_3 = "yyyy年MM月dd日";

    public final static DateTimeFormatter DT_FORMAT_PATTERN_1 = DateTimeFormat.forPattern(FORMAT_PATTERN_1);
    public final static DateTimeFormatter DT_FORMAT_PATTERN_2 = DateTimeFormat.forPattern(FORMAT_PATTERN_2);
    public final static DateTimeFormatter DT_FORMAT_PATTERN_3 = DateTimeFormat.forPattern(FORMAT_PATTERN_3);

    public static String getFormatHMS(long time) {
        time = time / 1000;
//        int s = (int) (time % 60);
//        int m = (int) (time / 60);

        int hour = (int) (time / 3600);
        int min = (int) (time % 3600 / 60);
        int second = (int) (time % 60);
        return String.format(Locale.CHINA, "%02d:%02d:%02d", hour, min, second);
    }

    /**
     * @param taskTime
     * @return
     */
    public static long getDailyTaskTime(long taskTime) {
        DateTime dateTime = new DateTime(taskTime);
        DateTimeFormatter pattern = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime resultTime = pattern.parseDateTime(DateTime.now().toString("yyyy-MM-dd")
                + " " + dateTime.toString("HH:mm:ss"));
        return resultTime.getMillis();
    }


    public static String dateToStr(Date dateDate, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    public static Date strToDate(String strDate, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }


    public static Calendar strToCalendar(String dateStr, String dateFormat) {
        Date fbTime = strToDate(dateStr, dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fbTime);
        return calendar;
    }

    /**
     * @param dayOneTime The first day. yyyy-MM-dd HH:mm:ss
     * @param dayTwoTime The second day. yyyy-MM-dd HH:mm:ss
     */
    public static boolean isSameMonth(String dayOneTime, String dayTwoTime) {
        if (StringUtils.isTrimEmpty(dayOneTime) || StringUtils.isTrimEmpty(dayTwoTime)) {
            return false;
        }
        DateTime dayOne = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime(dayOneTime);
        DateTime dayTwo = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime(dayTwoTime);
        return dayOne.getYear() == dayTwo.getYear() && dayOne.getMonthOfYear() == dayTwo.getMonthOfYear();
    }

    /**
     * @param dayOne The first day.
     * @param dayTwo The second day.
     */
    public static boolean isSameYear(Calendar dayOne, Calendar dayTwo) {
        return dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR);
    }

    /**
     * @param dayOne The first day.
     * @param dayTwo The second day.
     */
    public static boolean isSameMonth(Calendar dayOne, Calendar dayTwo) {
        return isSameYear(dayOne, dayTwo) && dayOne.get(Calendar.MONTH) == dayTwo.get(Calendar.MONTH);
    }


    public static String getWeek(Calendar calendar) {
        String str = null;
        String dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) + "";
        if ("1".equals(dayOfWeek)) {
            str = "星期日";
        } else if ("2".equals(dayOfWeek)) {
            str = "星期一";
        } else if ("3".equals(dayOfWeek)) {
            str = "星期二";
        } else if ("4".equals(dayOfWeek)) {
            str = "星期三";
        } else if ("5".equals(dayOfWeek)) {
            str = "星期四";
        } else if ("6".equals(dayOfWeek)) {
            str = "星期五";
        } else if ("7".equals(dayOfWeek)) {
            str = "星期六";
        }
        return str;
    }

    /*
     * 传入的日期转换成星期几
     */
    public static String dateToWeekday(Date changeDate) {
        String[] weekdays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        if (changeDate == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(changeDate);
        int numOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        return weekdays[numOfWeek];
    }


//    /**
//     * 对毫秒值补0处理
//     * 2017-09-19T10:00:45.37 为格式不完整的情况，补0-->2017-09-19T10:00:45.037
//     *
//     * @param time
//     * @return
//     */
//    public static String amendTime(String time) {
//        if (!TextUtils.isEmpty(time)) {
//            //包括毫秒，则去判断补0
//            if (time.lastIndexOf(".") != -1) {
//                String prer = time.substring(time.lastIndexOf(".") + 1);
//                if (prer.length() == 2) {
//                    prer = "0" + prer;
//                    time = time.substring(0, time.lastIndexOf(".") + 1) + prer;
//                } else if (prer.length() == 1) {
//                    prer = "00" + prer;
//                    time = time.substring(0, time.lastIndexOf(".") + 1) + prer;
//                }
//            } else {
//                //不包括毫秒
//                time = time + ".000";
//            }
//
//        }
//        return time;
//    }

    /**
     * @param dateTime 日期 yyyy-MM-dd HH:mm:ss
     * @param pattern  目标日期格式
     */
    public static String formatYMdHmsTime(String dateTime, String pattern) {
        if (!TextUtils.isEmpty(dateTime)) {
            try {
                DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                DateTimeFormatter format2 = DateTimeFormat.forPattern(pattern);
                return format.parseDateTime(dateTime).toString(format2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @param dateTime 日期 yyyy-MM-dd
     * @param pattern  目标日期格式
     */
    public static String formatYMdTime(String dateTime, String pattern) {
        if (!TextUtils.isEmpty(dateTime)) {
            try {
                DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd");
                DateTimeFormatter format2 = DateTimeFormat.forPattern(pattern);
                return format.parseDateTime(dateTime).toString(format2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * @param dateTime    日期
     * @param datePattern 该日期格式
     * @param pattern     目标日期格式
     */
    public static String formatTime(String dateTime, String datePattern, String pattern) {
        if (TextUtils.isEmpty(dateTime)) {
            return "";
        }
        try {
            DateTimeFormatter format = DateTimeFormat.forPattern(datePattern);
            DateTimeFormatter format2 = DateTimeFormat.forPattern(pattern);
            return format.parseDateTime(dateTime).toString(format2);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * @param millseconds
     */
    public static String formatMillsecondsTime(String millseconds, String pattern) {
        try {
            if (millseconds != null && !millseconds.trim().isEmpty()) {
                DateTime dateTime = new DateTime(Long.parseLong(millseconds));
                return dateTime.toString(DateTimeFormat.forPattern(pattern));
            }
        } catch (Exception e) {

        }
        return null;
    }


    /**
     * @param millseconds
     */
    public static String formatMillsecondsTime(long millseconds, String pattern) {
        try {
            DateTime dateTime = new DateTime(millseconds);
            return dateTime.toString(DateTimeFormat.forPattern(pattern));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param dateTime
     * @param pattern
     * @return
     */
    public static DateTime getDateTime(String dateTime, String pattern) {
        try {
            if (!TextUtils.isEmpty(dateTime)) {
                DateTimeFormatter format = DateTimeFormat.forPattern(pattern);
                return DateTime.parse(dateTime, format);
            }
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * @param millseconds
     * @return
     */
    public static DateTime getDateTime(long millseconds) {
        try {
            if (millseconds <= 0) {
                return new DateTime(millseconds);
            }
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * @param dateTime
     * @param pattern
     * @return millseconds
     */
    public static long getTimeMillis(String dateTime, String pattern) {
        try {
            if (!TextUtils.isEmpty(dateTime)) {
                DateTimeFormatter format = DateTimeFormat.forPattern(pattern);
                return DateTime.parse(dateTime, format).getMillis();
            }
        } catch (Exception e) {

        }
        return 0;
    }


    /**
     * 日期是否是今天
     *
     * @param dateTime
     */
    public static boolean isToday(String dateTime, String pattern) {
        if (!TextUtils.isEmpty(dateTime)) {
            try {
                DateTimeFormatter format = DateTimeFormat.forPattern(pattern);
                DateTime time = DateTime.parse(dateTime, format);
                DateTime nowTime = DateTime.now();
                DateTime minTime = nowTime.withTimeAtStartOfDay();
                DateTime maxTime = nowTime.millisOfDay().withMaximumValue();
//                MTLogger.d("nowTime:" + nowTime);
//                MTLogger.d("minTime:" + minTime);
//                MTLogger.d("maxTime:" + maxTime);
                //计算特定日期是否在该区间内
                Interval i = new Interval(minTime, maxTime);
                return i.contains(time);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 日期是否是今天
     *
     * @param millseconds
     */
    public static boolean isToday(long millseconds) {
        try {
            DateTime time = new DateTime(millseconds);
            DateTime nowTime = DateTime.now();
            DateTime minTime = nowTime.withTimeAtStartOfDay();
            DateTime maxTime = nowTime.millisOfDay().withMaximumValue();
//                MTLogger.d("nowTime:" + nowTime);
//                MTLogger.d("minTime:" + minTime);
//                MTLogger.d("maxTime:" + maxTime);
            //计算特定日期是否在该区间内
            Interval i = new Interval(minTime, maxTime);
            return i.contains(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 日期是否是明天
     *
     * @param dateTime
     */
    public static boolean isTomorrowDay(String dateTime, String pattern) {
        if (!TextUtils.isEmpty(dateTime)) {
            try {
                DateTimeFormatter format = DateTimeFormat.forPattern(pattern);
                DateTime time = DateTime.parse(dateTime, format);
                DateTime tomTime = DateTime.now().plusDays(1);
                DateTime minTime = tomTime.withTimeAtStartOfDay();
                DateTime maxTime = tomTime.millisOfDay().withMaximumValue();
//                MTLogger.d("tomTime:" + tomTime);
//                MTLogger.d("minTime:" + minTime);
//                MTLogger.d("maxTime:" + maxTime);
                //计算特定日期是否在该区间内
                Interval i = new Interval(minTime, maxTime);
                return i.contains(time);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * 日期是否是昨天
     *
     * @param dateTime
     */
    public static boolean isYesterday(String dateTime, String pattern) {
        if (!TextUtils.isEmpty(dateTime)) {
            try {
                DateTimeFormatter format = DateTimeFormat.forPattern(pattern);
                DateTime time = DateTime.parse(dateTime, format);
                DateTime yesTime = DateTime.now().minusDays(1);
                DateTime minTime = yesTime.withTimeAtStartOfDay();
                DateTime maxTime = yesTime.millisOfDay().withMaximumValue();
//                MTLogger.d("yesTime:" + yesTime);
//                MTLogger.d("minTime:" + minTime);
//                MTLogger.d("maxTime:" + maxTime);
                //计算特定日期是否在该区间内
                Interval i = new Interval(minTime, maxTime);
                return i.contains(time);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 日期是否是昨天
     *
     * @param millseconds
     */
    public static boolean isYesterday(long millseconds) {
        if (millseconds > 0) {
            try {
                DateTime time = new DateTime(millseconds);
                DateTime yesTime = DateTime.now().minusDays(1);
                DateTime minTime = yesTime.withTimeAtStartOfDay();
                DateTime maxTime = yesTime.millisOfDay().withMaximumValue();
//                MTLogger.d("yesTime:" + yesTime);
//                MTLogger.d("minTime:" + minTime);
//                MTLogger.d("maxTime:" + maxTime);
                //计算特定日期是否在该区间内
                Interval i = new Interval(minTime, maxTime);
                return i.contains(time);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 是否是当前以后
     */
    public static boolean isAfterTomorrowDay(String dateTime, String pattern) {
        if (!TextUtils.isEmpty(dateTime)) {
            try {
                DateTimeFormatter format = DateTimeFormat.forPattern(pattern);
                DateTime time = DateTime.parse(dateTime, format);

                DateTime afterTomorrowTime = DateTime.now();
//                DateTime afterTomorrowTime = DateTime.now().plusDays(2);
//                DateTime minTime = afterTomorrowTime.withTimeAtStartOfDay();
                DateTime maxTime = afterTomorrowTime.millisOfDay().withMaximumValue();
//                MTLogger.d("afterTomorrowTime:" + afterTomorrowTime);
//                MTLogger.d("minTime:" + minTime);
//                MTLogger.d("maxTime:" + maxTime);

                return time.isAfter(maxTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * 是否是以后
     */
    public static boolean isAfterTomorrowDay2(String dateTime, String pattern) {
        if (!TextUtils.isEmpty(dateTime)) {
            try {
                DateTimeFormatter format = DateTimeFormat.forPattern(pattern);
                DateTime time = DateTime.parse(dateTime, format);

                DateTime afterTomorrowTime = DateTime.now().plusDays(1);
//                DateTime afterTomorrowTime = DateTime.now().plusDays(2);
//                DateTime minTime = afterTomorrowTime.withTimeAtStartOfDay();
                DateTime maxTime = afterTomorrowTime.millisOfDay().withMaximumValue();
//                MTLogger.d("afterTomorrowTime:" + afterTomorrowTime);
//                MTLogger.d("minTime:" + minTime);
//                MTLogger.d("maxTime:" + maxTime);

                return time.isAfter(maxTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * @param nowDate   要比较的时间
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return true在时间段内，false不在时间段内
     * @throws Exception
     */
    public static boolean hourMinuteBetween(String nowDate, String startDate, String endDate) throws Exception {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Date now = format.parse(nowDate);
        Date start = format.parse(startDate);
        Date end = format.parse(endDate);

        long nowTime = now.getTime();
        long startTime = start.getTime();
        long endTime = end.getTime();

        return nowTime >= startTime && nowTime <= endTime;
    }


    public static int compare_date(String DATE1, String DATE2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                System.out.println("dt1 在dt2前");
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                System.out.println("dt1在dt2后");
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }


    /**
     * 是否是昨天以前
     *
     * @param dateTime
     */
    public static boolean isBeforeYesterday(String dateTime, String pattern) {
        if (!TextUtils.isEmpty(dateTime)) {
            try {
                DateTimeFormatter format = DateTimeFormat.forPattern(pattern);
                DateTime time = DateTime.parse(dateTime, format);
                DateTime beforeYesTime = DateTime.now().minusDays(2);
                DateTime minTime = beforeYesTime.withTimeAtStartOfDay();
//                DateTime maxTime = beforeYesTime.millisOfDay().withMaximumValue();
//                MTLogger.d("beforeYesTime:" + beforeYesTime);
//                MTLogger.d("minTime:" + minTime);
//                MTLogger.d("maxTime:" + maxTime);

                return time.isBefore(minTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 两个日期是否为同一天
     *
     * @param dateTime1
     * @param dateTime2
     */
    public static boolean isEqualsDay(String dateTime1, String dateTime2, String pattern) {
        if (!TextUtils.isEmpty(dateTime1) && !TextUtils.isEmpty(dateTime2)) {
            try {
                DateTimeFormatter format = DateTimeFormat.forPattern(pattern);
                DateTime time1 = DateTime.parse(dateTime1, format);
                DateTime time2 = DateTime.parse(dateTime2, format);
//                MTLogger.d("time1:" + time1.withTimeAtStartOfDay());
//                MTLogger.d("time2:" + time2.withTimeAtStartOfDay());
                return time1.withTimeAtStartOfDay().equals(time2.withTimeAtStartOfDay());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 两个日期是否为同一天
     *
     * @param dateTime1
     * @param dateTime2
     */
    public static boolean isEqualsDay(long dateTime1, long dateTime2) {
        if (dateTime1 > 0 && dateTime2 > 0) {
            try {
                DateTime time1 = new DateTime(dateTime1);
                DateTime time2 = new DateTime(dateTime2);
                return time1.withTimeAtStartOfDay().equals(time2.withTimeAtStartOfDay());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * 是否是今年
     *
     * @param time
     */
    public static boolean isNowYear(long time) {
        try {
            DateTime time1 = new DateTime(time);
            return time1.getYear() == DateTime.now().getYear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 计算岁数
     *
     * @param millseconds
     */
    public static int getAgeFromBirth(long millseconds) {
        if (millseconds >= 0) {
            try {
                DateTime dateTime1 = new DateTime(millseconds);
                Period p = new Period(dateTime1, DateTime.now(), PeriodType.yearMonthDay());
                return p.getYears();
            } catch (Exception e) {
                return -1;
            }
        }
        return -1;
    }

    /**
     * 计算岁数
     *
     * @param millseconds
     */
    public static int getAgeFromBirthMillseconds(String millseconds) {
        if (millseconds != null && !millseconds.trim().isEmpty()) {
            return getAgeFromBirth(Long.parseLong(millseconds));
        }
        return -1;
    }

    /**
     * 计算岁数
     *
     * @param dateTime 日期格式 yyyy-MM-dd
     */
    public static int getAgeFromBirth(String dateTime) {
        if (!TextUtils.isEmpty(dateTime)) {
            try {
                DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd");
                DateTime dateTime1 = DateTime.parse(dateTime, format);
                return getAgeFromBirth(dateTime1.getMillis());
            } catch (Exception e) {
                return -1;
            }
        }
        return -1;
    }

    /**
     * 日期是否是属于某一个时间区间
     *
     * @param dateTime yyyy-MM-dd HH:mm:ss
     * @param minTime  yyyy-MM-dd HH:mm:ss
     * @param maxTime  yyyy-MM-dd HH:mm:ss
     */
    public static boolean isInterval(DateTime dateTime, DateTime minTime, DateTime maxTime) {
        if (dateTime != null) {
            try {
                //计算特定日期是否在该区间内
                Interval i = new Interval(minTime, maxTime);
                return i.contains(dateTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 2- 7 days ago
     *
     * @param millseconds yyyy-MM-dd HH:mm:ss
     */
    public static boolean isIntervalWeek(long millseconds) {
        if (millseconds > 0) {
            try {
                DateTime dateTime = new DateTime(millseconds);
                DateTime startTime = DateTime.now().minusDays(7).withTimeAtStartOfDay();
                DateTime endTime = DateTime.now().minusDays(2).millisOfDay().withMaximumValue();
                //计算特定日期是否在该区间内
                Interval i = new Interval(startTime, endTime);
                return i.contains(dateTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * @param time
     * @return
     */
    public static String assignTime(long time) {
        try {
            DateTime dateTime = new DateTime(time);
            DateTime now = DateTime.now();
//            MTLogger.e("year-->" + now.getYear());
//            MTLogger.e("month-->" + now.getMonthOfYear());
//            MTLogger.e("day-->" + now.getDayOfMonth());
            //是否是今年
            if (dateTime.getYear() != now.getYear()) {
                return dateTime.toString("yyyy-MM-dd HH:mm");
            } else {
                //是否是今天
//                if (dateTime.getMonthOfYear() == now.getMonthOfYear()
//                        && dateTime.getDayOfMonth() == now.getDayOfMonth()) {
//                    return dateTime.toString("HH:mm");
//                } else {
                return dateTime.toString("MM-dd HH:mm");
//                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param time
     * @return
     */
    public static String assignTime2(long time) {
        try {
            DateTime dateTime = new DateTime(time);
            DateTime now = DateTime.now();
//            MTLogger.e("year-->" + now.getYear());
//            MTLogger.e("month-->" + now.getMonthOfYear());
//            MTLogger.e("day-->" + now.getDayOfMonth());
            //是否是今年
            if (dateTime.getYear() != now.getYear()) {
                return dateTime.toString("yyyy-MM-dd HH:mm");
            } else {
                //是否是今天
                if (dateTime.getMonthOfYear() == now.getMonthOfYear()
                        && dateTime.getDayOfMonth() == now.getDayOfMonth()) {
                    return dateTime.toString("HH:mm");
                } else {
                    return dateTime.toString("MM-dd HH:mm");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param time
     * @return
     */
    public static String assignDate(long time) {
        try {
            DateTime dateTime = new DateTime(time);
            DateTime now = DateTime.now();
            //是否是今年
            if (dateTime.getYear() != now.getYear()) {
                return dateTime.toString("yyyy-MM-dd");
            } else {
                return dateTime.toString("MM-dd");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 时间是今天， 显示 HH:mm
     *
     * @return
     */
    public static String needDisplayTime(long time) {
        if (time <= 0) {
            return null;
        }
        try {
            if (isToday(time)) { //今天
                return DateTimeUtils.formatMillsecondsTime(time, "HH:mm");
            } else if (isYesterday(time)) { //昨天
                return "昨天" + DateTimeUtils.formatMillsecondsTime(time, "HH:mm");
            } else if (isIntervalWeek(time)) { // 2 ~ 7days ago
                return DateTimeUtils.formatMillsecondsTime(time, "EEEE HH:mm");
            } else if (isNowYear(time)) {
                return DateTimeUtils.formatMillsecondsTime(time, "MM月dd日 HH:mm");
            } else {
                return DateTimeUtils.formatMillsecondsTime(time, "yyyy年MM月dd日 HH:mm");
            }
        } catch (Exception e) {
            return null;
        }
    }

    //获取日期的前一天
    public static DateTime getDate() {
        Calendar ca = Calendar.getInstance();
        ca.setTime(new Date());
        ca.add(Calendar.DATE, -1);
        DateTime dateTime = new DateTime(ca.getTimeInMillis());
        return dateTime;
    }


    private final static long minute = 60 * 1000;// 1分钟

    /**
     * 时间是今天， 显示 HH:mm
     *
     * @return
     */
    public static String dsplayTime(long time) {
        if (time <= 0) {
            return null;
        }
        try {
            if (isToday(time)) { //今天
//                //是否是半小时之内
//                long diff = new Date().getTime() - time;
//                long r = 0;
//                if (diff > minute) {
//                    r = (diff / minute);
//                    if (r == 0) {
//                        return "刚刚";
//                    } else if (r <= 30) {
//                        return r + "分钟前";
//                    } else {
//                        return DateTimeUtils.formatMillsecondsTime(time, "HH:mm");
//                    }
//                } else {
                return "今天 " + DateTimeUtils.formatMillsecondsTime(time, "yyyy-MM-dd HH:mm");
//                }

            } else if (isYesterday(time)) { //昨天
                return "昨天 " + DateTimeUtils.formatMillsecondsTime(time, "yyyy-MM-dd HH:mm");
//                return "昨天" + DateTimeUtils.formatMillsecondsTime(time, "HH:mm");
            } else {
                return DateTimeUtils.formatMillsecondsTime(time, "yyyy-MM-dd HH:mm");
            }
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 传入今天日期
     *
     * @param date
     * @return 返回本日所在周的开始日期
     */
    public static String getFirstDayOfWeek(String date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat ff = new SimpleDateFormat("yyyy-MM-dd");
        String startTime = "";
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
//        c.setTime(date);
        try {
            c.setTime(df.parse(date));
            c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday
            startTime = ff.format(c.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return startTime;
    }

    // 获取当前时间所在周的结束日期
    public static String getLastDayOfWeek(String date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat ff = new SimpleDateFormat("yyyy-MM-dd");
        String endTime = "";

        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        try {
            c.setTime(df.parse(date));
            c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6); // Sunday
            endTime = ff.format(c.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return endTime;
    }


    /**
     * 时间是今天， 显示今天 HH:mm
     *
     * @return
     */
    public static String needDisplayDayTime(long time) {
        if (time <= 0) {
            return null;
        }
        try {
            if (isToday(time)) { //今天
                return "昨天" + DateTimeUtils.formatMillsecondsTime(time, "HH:mm");
            } else if (isYesterday(time)) { //昨天
                return "昨天" + DateTimeUtils.formatMillsecondsTime(time, "HH:mm");
            } else {
                return DateTimeUtils.formatMillsecondsTime(time, "yyyy年MM月dd日 HH:mm");
            }
//            else if (isIntervalWeek(time)) { // 2 ~ 7days ago
//                return DateTimeUtils.formatMillsecondsTime(time, "EEEE HH:mm");
//            } else if (isNowYear(time)) {
//                return DateTimeUtils.formatMillsecondsTime(time, "MM月dd日 HH:mm");
//            }
        } catch (Exception e) {
            return null;
        }
    }


    public static String getFirstDay(String yearMonth) {
        String lastDay = "";
        SimpleDateFormat ff = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cale = Calendar.getInstance();
        try {
            cale.setTime(ff.parse(yearMonth));
//            cale.add(Calendar.MONTH);
            cale.set(Calendar.DAY_OF_MONTH, 1);//第一天
            lastDay = dateFormater.format(cale.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return lastDay;
    }


    public static String getLastDay(String yearMonth) {
        String lastDay = "";
        SimpleDateFormat ff = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cale = Calendar.getInstance();
        try {
            cale.setTime(ff.parse(yearMonth));
            cale.add(Calendar.MONTH, +1);
            cale.set(Calendar.DAY_OF_MONTH, 0);//最后一天
            lastDay = dateFormater.format(cale.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return lastDay;
    }


    public static DateTime getLastDay() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, +1);
        cale.set(Calendar.DAY_OF_MONTH, 0);//设置为1号,当前日期既为本月第一天
        String lastDay = format.format(cale.getTime());
        return getDateTime(lastDay, "yyyy-MM-dd");
    }

    public static DateTime getBefoDay() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //获取前月的前两月一天
        Calendar cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, -2);
        cale.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        String lastDay = format.format(cale.getTime());
        return getDateTime(lastDay, "yyyy-MM-dd");
    }

    //年 计算
    public static int getYearOfDay(int year) {
        //一年多少天  计算
        int days;//某年(year)的天数
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {//闰年的判断规则
            days = 366;
        } else {
            days = 365;
        }
        return days;
    }

    //月计算
    public static int getMonthOfDay(DateTime dateTime) {
        int days;
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, dateTime.getYear());
        c.set(Calendar.MONTH, dateTime.getMonthOfYear() - 1);
        days = c.getActualMaximum(Calendar.DAY_OF_MONTH);
//        System.out.println("天数：" + c.getActualMaximum(Calendar.DAY_OF_MONTH));
//        System.out.println("周数：" + c.getActualMaximum(Calendar.WEEK_OF_MONTH));
        return days;
    }

    //季度计算
    public static int getQuarterOfDay(DateTime dateTime, int quarterPosition) {
        int days = 0;
//一年分四个季度，每个季度是三个月，第一季度就是一二三月，第一季度是90天，闰年是91天。第二季度是91天。第三季度是92天。第四季度是92天。
        if (quarterPosition == 0) {//第一季度
            if (dateTime.getYear() % 4 == 0 && dateTime.getYear() % 100 != 0 || dateTime.getYear() % 400 == 0) {//闰年的判断规则
                days = 91;
            } else {
                days = 90;
            }
        } else if (quarterPosition == 1) {//第二季度
            days = 91;
        } else if (quarterPosition == 2) {//第三季度
            days = 92;
        } else if (quarterPosition == 3) {//第四季度
            days = 92;
        }
        return days;
    }

    public static Calendar daysOfCalendar(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(str);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 判断两个日期跨了几天，两个日期相差的天数
     *
     * @param dayOne
     * @param dayTwo
     * @return
     */
    public static int daysOfOne(Calendar dayOne, Calendar dayTwo) {
        //先判断两个日期是否为同一年
        if (dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR)) {
            int day1 = dayOne.get(Calendar.DAY_OF_YEAR);
            int day2 = dayTwo.get(Calendar.DAY_OF_YEAR);
            return day2 - day1 + 1;
        } else { //跨年
            //先判断前面的日期到该年最后一天的天数
            Calendar lastCalendar = Calendar.getInstance();
            lastCalendar.set(Calendar.YEAR, dayOne.get(Calendar.YEAR));
            lastCalendar.set(Calendar.MONTH, 11);
            lastCalendar.set(Calendar.DAY_OF_MONTH, 31);
            lastCalendar.set(Calendar.HOUR_OF_DAY, 23);
            lastCalendar.set(Calendar.MINUTE, 59);
            lastCalendar.set(Calendar.SECOND, 59);
            int day1 = lastCalendar.get(Calendar.DAY_OF_YEAR) - dayOne.get(Calendar.DAY_OF_YEAR) + 1;
//            Log.d("##", "day1:" + day1);
            //判断后面的日期到该年第一天的天数
            Calendar firstCalendar = (Calendar) lastCalendar.clone();
            firstCalendar.set(Calendar.YEAR, dayTwo.get(Calendar.YEAR));
            firstCalendar.set(Calendar.MONTH, 0);
            firstCalendar.set(Calendar.DAY_OF_MONTH, 1);
            firstCalendar.set(Calendar.HOUR_OF_DAY, 0);
            firstCalendar.set(Calendar.MINUTE, 0);
            lastCalendar.set(Calendar.SECOND, 0);
            int day2 = dayTwo.get(Calendar.DAY_OF_YEAR) - firstCalendar.get(Calendar.DAY_OF_YEAR) + 1;
//            Log.d("##", "day2:" + day2);
            //中间相差几年
            int dd = 0;
            if ((dayTwo.get(Calendar.YEAR) - dayOne.get(Calendar.YEAR)) > 1) {
                dd = daysOfTwo(lastCalendar, firstCalendar); //相差的天数
            }
//            Log.d("##", "dd:" + dd);
            return day1 + day2 + dd;
        }

    }

    /**
     * 判断两个日期相差多少天，在时间差上判断
     *
     * @param dayOne
     * @param dayTwo
     * @return
     */
    public static int daysOfTwo(Calendar dayOne, Calendar dayTwo) {
        if (null == dayOne || null == dayTwo) {
            return -1;
        }
        long intervalMilli = dayTwo.getTimeInMillis() - dayOne.getTimeInMillis();
        return (int) (intervalMilli / (24 * 60 * 60 * 1000));

    }

    public static String getDatePoor(long endTime, long startTime) {
        if (endTime < startTime) {
            return null;
        }
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endTime - startTime;
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        long sec = diff % nd % nh % nm / ns;
        if (day > 0) {
            return day + "天" + hour + "时" + min + "分";
        } else if (hour > 0) {
            return hour + "时" + min + "分" + sec + "秒";
        } else {
            return min + "分" + sec + "秒";
        }
    }

    /**
     * 当前时间所在一周的周一和周日时间
     *
     * @return
     */
    public static DateTime getWeekDate() {
        Map<String, String> map = new HashMap();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
        if (dayWeek == 1) {
            dayWeek = 8;
        }

        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - dayWeek);// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        Date mondayDate = cal.getTime();
        String weekBegin = sdf.format(mondayDate);
        DateTime dateTime = getDateTime(weekBegin, "yyyy-MM-dd");
        System.out.println("所在周星期一的日期：" + weekBegin);

        return dateTime;
    }

    //日期 后几天时间
    public static String getOldDate(int distanceDay) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) + distanceDay);
        Date endDate = null;
        try {
            endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //后 传正数   前几天传负数
//        LogUtil.d("前7天==" + dft.format(endDate));
        return dft.format(endDate);
    }


    //日期 后几天时间
    public static String getOldDate(DateTime day, int distanceDay) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(day.getYear(), day.getMonthOfYear() - 1, day.getDayOfMonth() + distanceDay);
//        date.set(Calendar.DATE, date.get(Calendar.DATE) + distanceDay);
        Date endDate = null;
        try {
            endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dft.format(endDate);
    }

    //两时间计算
    public static int getTwoTimeOfDay(DateTime startTime, DateTime endTime) {
        int daysBetween = 0;
        try {
            daysBetween = daysBetween(startTime.toString("yyyy-MM-dd 00:00:00"), endTime.toString("yyyy-MM-dd 00:00:00"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return daysBetween;
    }

    public static int daysBetween(String smdate, String bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smdate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bdate));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    //获取前一天
    public static String getDay(String time, String type) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        int day = calendar.get(Calendar.DATE);
        //                      此处修改为+1则是获取后一天
        if (type.equals("reduce")) {
            calendar.set(Calendar.DATE, day - 1);
        } else if (type.equals("add")) {
            calendar.set(Calendar.DATE, day + 1);
        }


        String lastDay = sdf.format(calendar.getTime());
        return lastDay;
    }


    //获取后一年
    public static String getNextYear(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        // 此处修改为+1则是获取后一天
        calendar.set(Calendar.YEAR, year + 1);
        return sdf.format(calendar.getTime());
    }

    public static String getWeek(String pTime) {

        String Week = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {

            c.setTime(format.parse(pTime));

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            Week += "星期天";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 2) {
            Week += "星期一";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 3) {
            Week += "星期二";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 4) {
            Week += "星期三";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 5) {
            Week += "星期四";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 6) {
            Week += "星期五";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 7) {
            Week += "星期六";
        }
        return Week;
    }

    /**
     * 把传入的时间，处理分钟近整数点
     *
     * @return
     */
    public static long roundTimeInMillis(long timeInMillis) {
        DateTime dateTime;
        if (timeInMillis > 0) {
            dateTime = new DateTime(timeInMillis);
        } else {
            dateTime = DateTime.now();
        }
        int minutes = dateTime.getMinuteOfHour();
        if (minutes <= 15) {
            dateTime = dateTime.withMinuteOfHour(0);
        } else if (minutes < 45) {
            dateTime = dateTime.withMinuteOfHour(30);
        } else {
            dateTime = dateTime.withMinuteOfHour(0).plusHours(1);
        }
        return dateTime.getMillis();
    }

    //月 的第几周
    public static Integer dateToWeek(String today) {
//        String today = "2013-01-14";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        int WEEK_OF_MONTH = 0;
        try {
            date = format.parse(today);
            Calendar calendar = Calendar.getInstance(Locale.CHINA);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            calendar.setTime(date);
            WEEK_OF_MONTH = calendar.get(Calendar.WEEK_OF_MONTH);//当月的 DAY_OF_WEEK_IN_MONTH


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return WEEK_OF_MONTH;

    }

    //某月第几周的星期一
    public static String getWeekDateByWeekInMonth(int year, int month, int weekCount) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.WEEK_OF_MONTH, weekCount);//DAY_OF_WEEK_IN_MONTH
        cal.setFirstDayOfWeek(Calendar.MONDAY);
//        return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH);
        return DateTimeUtils.formatMillsecondsTime(cal.getTime().getTime(), "yyyy-MM-dd");

    }

    // 获取本周开始时间
    public static Date getBeginDayOfWeek(Date date) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayofweek == 1) {
            dayofweek += 7;
        }
        cal.add(Calendar.DATE, 2 - dayofweek);
        return cal.getTime();
    }

    public static Date gmtToDate(String stringGMTDate) {
        //如果stringDate中没有‘,’号，格式转换中也不要加，保持一致
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        try {
            //格林威治时间字符串转为Date格式 - 此Date是北京时间 GMT+8
            Date date = sdf.parse(stringGMTDate);
            return date;
        } catch (Exception e) {
            return null;
        }
    }

    public static long gmtToDateTime(String stringGMTDate) {
        //如果stringDate中没有‘,’号，格式转换中也不要加，保持一致
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        try {
            //格林威治时间字符串转为Date格式 - 此Date是北京时间 GMT+8
            Date date = sdf.parse(stringGMTDate);
            return date.getTime();
        } catch (Exception e) {
            return -1;
        }
    }


}
