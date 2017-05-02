package com.yunfei.wh.control;

import com.prj.sdk.util.DateUtil;
import com.yunfei.wh.R;

public class WeatherInfoControl {

    public static int getWeatherInfoBg(String dayInfo, String nightInfo) {
        int weatherRes = R.drawable.ic_sunny_day_bg;
        if (DateUtil.getDayOrNight()) {// 夜晚
            if (nightInfo.contains("雷")) {
                weatherRes = R.drawable.ic_thunder_bg;
            } else if (nightInfo.contains("雨")) {
                weatherRes = R.drawable.ic_rain_bg;
            } else if (nightInfo.contains("雪")) {
                weatherRes = R.drawable.ic_snow_bg;
            } else if (nightInfo.contains("云")) {
                weatherRes = R.drawable.ic_cloudy_bg;
            } else if (nightInfo.contains("霾")) {
                weatherRes = R.drawable.ic_overcast_bg;
            } else {// 晴
                weatherRes = R.drawable.ic_sunny_night_bg;
            }
        } else {// 白天
            if (dayInfo.contains("雷")) {
                weatherRes = R.drawable.ic_thunder_bg;
            } else if (dayInfo.contains("雨")) {
                weatherRes = R.drawable.ic_rain_bg;
            } else if (dayInfo.contains("雪")) {
                weatherRes = R.drawable.ic_snow_bg;
            } else if (dayInfo.contains("云")) {
                weatherRes = R.drawable.ic_cloudy_bg;
            } else if (dayInfo.contains("霾")) {
                weatherRes = R.drawable.ic_overcast_bg;
            } else {// 晴
                weatherRes = R.drawable.ic_sunny_day_bg;
            }
        }

        return weatherRes;
    }

    public static int getWeatherResForNight(String nightInfo) {
        int weatherRes = R.drawable.ic_sunny_night;

        if (nightInfo.contains("云")) {
            weatherRes = R.drawable.ic_cloudy_night;
        } else if (nightInfo.contains("雪")) {
            weatherRes = R.drawable.ic_snow_night;
        } else if (nightInfo.contains("雨")) {
            weatherRes = R.drawable.ic_rain_night;
        } else if (nightInfo.contains("雾")) {
            weatherRes = R.drawable.ic_foggy;
        } else if (nightInfo.contains("霾")) {
            weatherRes = R.drawable.ic_haze_night;
        } else {
            weatherRes = R.drawable.ic_sunny_night;
        }

        return weatherRes;
    }

    public static int getWeatherResForDay(String dayInfo) {
        int weatherRes = R.drawable.ic_sunny_day;

        if (dayInfo.contains("暴雪")) {
            weatherRes = R.drawable.ic_snow_storm;
        } else if (dayInfo.contains("暴雨")) {
            weatherRes = R.drawable.ic_rain_storm;
        } else if (dayInfo.contains("大雪")) {
            weatherRes = R.drawable.ic_snow_heavy;
        } else if (dayInfo.contains("大雨")) {
            weatherRes = R.drawable.ic_rain_heavy;
        } else if (dayInfo.contains("雾")) {
            weatherRes = R.drawable.ic_foggy;
        } else if (dayInfo.contains("霾")) {
            weatherRes = R.drawable.ic_haze_day;
        } else if (dayInfo.contains("雨夹冰雹")) {
            weatherRes = R.drawable.ic_rain_hail;
        } else if (dayInfo.contains("雨夹雪")) {
            weatherRes = R.drawable.ic_rain_snow;
        } else if (dayInfo.contains("阵雨")) {
            weatherRes = R.drawable.ic_rain_shower;
        } else if (dayInfo.contains("中雪")) {
            weatherRes = R.drawable.ic_snow_moderate;
        } else if (dayInfo.contains("中雨")) {
            weatherRes = R.drawable.ic_rain_moderate;
        } else if (dayInfo.contains("多云")) {
            weatherRes = R.drawable.ic_cloudy_day;
        } else if (dayInfo.contains("雷阵雨")) {
            weatherRes = R.drawable.ic_rain_thunder;
        } else if (dayInfo.contains("少云")) {
            weatherRes = R.drawable.ic_cloudy_partly;
        } else if (dayInfo.contains("阴")) {
            weatherRes = R.drawable.ic_overcast;
        } else if (dayInfo.contains("小雪")) {
            weatherRes = R.drawable.ic_snow_small;
        } else if (dayInfo.contains("小雨")) {
            weatherRes = R.drawable.ic_rain_small;
        } else {
            weatherRes = R.drawable.ic_sunny_day;
        }

        return weatherRes;
    }
}
