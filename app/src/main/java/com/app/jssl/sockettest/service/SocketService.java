package com.app.jssl.sockettest.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.app.jssl.sockettest.eventbus.LoginEntity;

import org.greenrobot.eventbus.EventBus;

/**
 * Author: ls
 * Time:   2019/4/1 15:09
 * Desc:   This is SocketService：
 * <p>
 * socket在实际应用时:
 * <p>
 * 1.持续发送心跳来维护连接
 * <p>
 * 2.开启循环读取线程一直读取服务器的数据
 */
public class SocketService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        EventBus.getDefault().postSticky(new LoginEntity(true, "登录成功！"));
        return super.onStartCommand(intent, flags, startId);
    }
}
