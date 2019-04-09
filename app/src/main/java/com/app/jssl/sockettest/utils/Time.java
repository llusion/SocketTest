package com.app.jssl.sockettest.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author: ls
 * Time:   2019/4/8 16:20
 * Desc:   This is Time：
 */
public class Time {
    public static String now() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
