package com.game.utils.timeUtils;

import com.game.utils.logUtils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {

    //获取时间  返回毫秒级时间
    public static String getTime() {
        //LogUtil.print("getTime...util...");
        Calendar calendar = Calendar.getInstance();
        long date = calendar.getTime().getTime();            //获取毫秒时间
        return Long.toString(date);
    }

    //比较时间间隔
    public static boolean cmpTime(String time) {
        //LogUtil.print("cmpTime...util...");
        long tempTime = Long.parseLong(time);
        //LogUtil.print("tempTime"+tempTime);

        //再获取现在的时间
        Calendar calendar = Calendar.getInstance();
        long date = calendar.getTime().getTime();            //获取毫秒时间
        //LogUtil.print("date"+date);

        if(date - tempTime > 600000 ) {   //10分钟内不能重复获取
            LogUtil.print("have sent VCode in 10 mins");
            return false;
        } else {
            return true;
        }
    }
}