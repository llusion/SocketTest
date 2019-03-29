package com.app.jssl.sockettest.utils;

import com.app.jssl.sockettest.eventbus.InfoEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author: ls
 * Time:   2019/3/29 15:57
 * Desc:   This is Constant：
 */
public class Constant {

    public static List<InfoEntity> info = new ArrayList();
    public static String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

}
