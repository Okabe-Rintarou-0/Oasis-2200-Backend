package com.game.utils.triggerUtils;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Date;

//TriggerUtil: 创建定时任务的辅助工具;

@Component
public class TriggerUtil {
    //创建秒级别的trigger(基于cron)
    public CronTrigger createSecondLevelCronTrigger(int offset, int interval) {
        String cron = String.format("%d/%d * * * * *", offset, interval);
        return new CronTrigger(cron);
    }

    //创建毫秒级别的trigger
    public Trigger createMicroSecLevelTrigger(int interval) {
        return new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                Date lastTime = triggerContext.lastScheduledExecutionTime();
                if (lastTime == null) {
                    return new Date();
                } else {
                    return new Date(lastTime.getTime() + interval);
                }
            }
        };
    }
}
