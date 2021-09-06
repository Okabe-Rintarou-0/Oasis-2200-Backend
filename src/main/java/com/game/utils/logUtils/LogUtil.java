package com.game.utils.logUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogUtil {
    public static boolean isLinux() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    public static void print(String logText) {
        if (isLinux()) {
            //log.info("linux");
            log.info(logText);
        } else if (isWindows()) {
            //System.out.println("windows");
            System.out.println(logText);
        } else {
            System.out.println("unknown system!");
        }
    }
    //日志信息从高到低分为四级

    public static void error(String logText) {
        if (isLinux()) {
            log.error(logText);
        } else if (isWindows()) {
            System.out.println("ERROR: " + logText);
        }
    }

    public static void warn(String logText) {
        if (isLinux()) {
            log.warn(logText);
        } else if (isWindows()) {
            System.out.println("WARN: " + logText);
        }
    }

    public static void info(String logText) {
        if (isLinux()) {
            log.info(logText);
        } else if (isWindows()) {
            System.out.println("INFO: " + logText);
        }
    }

    public static void debug(String logText) {
        if (isLinux()) {
            log.debug(logText);
        } else if (isWindows()) {
            System.out.println("DEBUG: " + logText);
        }
    }

    public static void print(String format, Object... logObject) {
        print(String.format(format, logObject));
    }

    public static void error(Object logObject) {
        error(logObject.toString());
    }

    public static void warn(Object logObject) {
        warn(logObject.toString());
    }

    public static void info(Object logObject) {
        info(logObject.toString());
    }

    public static void debug(Object logObject) {
        debug(logObject.toString());
    }
}
