package com.app.jssl.sockettest.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.app.jssl.sockettest.eventbus.LoginEvent;
import com.app.jssl.sockettest.eventbus.SocketEvent;
import com.app.jssl.sockettest.utils.SocketUtils;
import com.app.jssl.sockettest.utils.Time;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    private ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("client").daemon(true).build();
    public ExecutorService threadPools = new ThreadPoolExecutor(5, 20, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
            threadFactory, new ThreadPoolExecutor.AbortPolicy());

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
        initService();
        return super.onStartCommand(intent, flags, startId);
    }

    private void initService() {
        connectServer();
    }

    /**
     * 启动接收线程
     */
    private void receive() {
        while (true) {
            try {
                SocketUtils.getSocket().sendUrgentData(0);
                byte[] buffer = new byte[1024];
                int length = 0;
                while (((length = SocketUtils.inputStream.read(buffer)) != -1)) {
                    if (length > 0) {
                        String message = new String(Arrays.copyOf(buffer, length)).trim();
                        dealAcceptServer(message);
                    }
                }
            } catch (NullPointerException e) {
                EventBus.getDefault().post(new LoginEvent(Time.now(), false, "正在连接服务器...", "启动服务"));
            } catch (SocketException e) {
                EventBus.getDefault().post(new LoginEvent(Time.now(), false, "服务断开", "启动服务"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void dealAcceptServer(String message) {
        try {
            JSONObject jsonObject = new JSONObject(message);
            if ("login".equals(jsonObject.get("type"))) {
                EventBus.getDefault().post(new LoginEvent(Time.now(), message.contains("true"), message, "登录"));
            }
            if ("beat".equals(jsonObject.get("type"))) {
                EventBus.getDefault().post(new SocketEvent(Time.now(), "服务器回应心跳：" + message));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void connectServer() {
        threadPools.execute(() -> {
            SocketUtils.getSocket();
            if (SocketUtils.getSocket() != null) {
                receive();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SocketUtils.release();
    }
}
