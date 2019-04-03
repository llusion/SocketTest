package com.app.jssl.sockettest.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.app.jssl.sockettest.server.MySocketServer;
import com.app.jssl.sockettest.server.WebConfig;

/**
 * Author: ls
 * Time:   2019/4/3 11:18
 * Desc:   This is ServerService：
 */
public class ServerService extends Service {
    private MySocketServer mySocketServer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        WebConfig webConfig = new WebConfig();
        webConfig.setPort(9001);
        webConfig.setMaxParallels(10);
        mySocketServer = new MySocketServer(webConfig);
        mySocketServer.startServer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
