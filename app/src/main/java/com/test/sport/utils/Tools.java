package com.test.sport.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

// TODO:工具类 
public class Tools {

    /**
     * 转本地时区
     */
    public static String getTime(String time, String timeZoneId) {
        // 定义输入时间的格式
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

        // 解析输入时间字符串为ZonedDateTime对象
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(time, inputFormatter);

        // 转换为中国时间（东八区）
        ZoneId zoneId = ZoneId.of(timeZoneId);
        ZonedDateTime localTime = zonedDateTime.withZoneSameInstant(zoneId);

        // 定义输出时间的格式
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        // 格式化输出时间字符串
        return localTime.format(outputFormatter);
    }


    /**
     * 获取比赛本地日期
     */
    public static String getLocalDate(String time) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(time, inputFormatter);
            ZoneId chinaZoneId = ZoneId.of("Asia/Shanghai");
            ZonedDateTime chinaTime = zonedDateTime.withZoneSameInstant(chinaZoneId);
            return chinaTime.format(outputFormatter);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 修改状态栏颜色
     */
    public static void setStatusBarColor(Activity activity, int colorId) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(activity.getResources().getColor(colorId));
    }

    /*
     * 全透状态栏
     */

    public static void setStatusBarColor(Activity activity) {

        //21表示5.0
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    /**
     * 自定义格式化时间
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String customFormat(Date date, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.CHINA);
        return simpleDateFormat.format(date);
    }

    /**
     * 字符串转时间戳
     *
     * @param time
     * @return
     */
    public static long strToLong(String time, String pattern) {
        SimpleDateFormat sdr = new SimpleDateFormat(pattern, Locale.CHINA);
        Date date;
        long l = 0;
        try {
            date = sdr.parse(time);
            l = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return l;
    }

    /**
     * 日期字符串转为另一种格式日期字符串
     *
     * @param time
     * @return
     * @throws ParseException
     */
    public static String StringToDate(String time, String oldPattern, String newPattern) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat(oldPattern, Locale.CHINA);
        Date date;
        date = format.parse(time);
        SimpleDateFormat format1 = new SimpleDateFormat(newPattern, Locale.CHINA);
        String s = format1.format(date);
        return s;
    }


    public static String readAssetFile(Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            return new String(buffer, "UTF-8");
        } catch (IOException e) {
            Log.e("Tools", "读取assets文件失败: " + fileName, e);
            return null;
        }
    }

    //判断热门赛事
    // 足球热门赛事判断
    public static boolean isPopularFootballEvent(String competition) {
        // 定义精确的热门赛事名称列表
        final String[] POPULAR_FOOTBALL_EVENTS = {
                "Premier League",    // 英超
                "UEFA Champions League",     // 欧冠
                "LaLiga Santander",         // 西甲
                "Bundesliga",               // 德甲
                "Serie A",                  // 意甲
                "Ligue 1",                  // 法甲
                "FIFA World Cup",           // 世界杯
                "UEFA European Championship",// 欧洲杯
                "Copa América"              // 美洲杯
        };

        // 精确匹配完整赛事名称
        for (String event : POPULAR_FOOTBALL_EVENTS) {
            if (competition.equals(event)) {
                return true;
            }
        }

        // 特殊情况处理（如世界杯相关赛事）
        if (competition.startsWith("FIFA World Cup") &&
                !competition.toLowerCase().contains("qualification") &&
                !competition.toLowerCase().contains("u20") &&
                !competition.toLowerCase().contains("u21") &&
                !competition.toLowerCase().contains("u23")) {
            return true;
        }

        return false;
    }


    // 篮球热门赛事判断
    public static boolean isPopularBasketballEvent(String competition) {
        final String[] POPULAR_BASKETBALL_EVENTS = {
                "NBA",                          // NBA
                "NBA Regular Season",           // NBA常规赛
                "NBA Playoffs",                 // NBA季后赛
                "NBA Finals",                   // NBA总决赛
                "CBA",                          // CBA
                "EuroLeague",                   // 欧洲联赛
                "FIBA Basketball World Cup",    // 世界杯
                "NCAA Division I"               // NCAA一级联赛
        };

        for (String event : POPULAR_BASKETBALL_EVENTS) {
            if (competition.equals(event)) {
                return true;
            }
        }
        return false;
    }

    // 冰球热门赛事判断
    public static boolean isPopularHockeyEvent(String competition) {
        final String[] POPULAR_HOCKEY_EVENTS = {
                "NHL",                          // NHL
                "NHL Regular Season",           // NHL常规赛
                "NHL Playoffs",                 // NHL季后赛
                "NHL Stanley Cup",              // NHL斯坦利杯
                "KHL",                          // KHL
                "IIHF World Championship",      // 世界锦标赛
                "Champions Hockey League"        // 欧洲冠军联赛
        };

        for (String event : POPULAR_HOCKEY_EVENTS) {
            if (competition.equals(event)) {
                return true;
            }
        }
        return false;
    }

    // 网球热门赛事判断
    public static boolean isPopularTennisEvent(String competition) {
        final String[] POPULAR_TENNIS_EVENTS = {
                "Australian Open",              // 澳网
                "French Open",                  // 法网
                "Wimbledon",                    // 温网
                "US Open",                      // 美网
                "ATP Finals",                   // ATP年终总决赛
                "WTA Finals",                   // WTA年终总决赛
                "Davis Cup",                    // 戴维斯杯
                "ATP Masters 1000"              // ATP大师赛
        };

        for (String event : POPULAR_TENNIS_EVENTS) {
            if (competition.equals(event)) {
                return true;
            }
        }

        // 特殊情况处理：大师赛系列
        if (competition.contains("Masters 1000") &&
                !competition.toLowerCase().contains("qualification")) {
            return true;
        }

        return false;
    }

    //计算前一天
    public static String toPreviousDate(String date) throws ParseException {
        String previousDate;
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(parser.parse(date));
        cal.add(java.util.Calendar.DAY_OF_MONTH, -1);
        previousDate = parser.format(cal.getTime());
        return previousDate;
    }

}
