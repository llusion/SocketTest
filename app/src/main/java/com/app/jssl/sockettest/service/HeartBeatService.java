package com.app.jssl.sockettest.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.app.jssl.sockettest.eventbus.ClientEvent;
import com.app.jssl.sockettest.utils.SocketUtils;
import com.app.jssl.sockettest.utils.Time;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Author: ls
 * Time:   2019/4/11 9:02
 * Desc:   This is HeartBeatService：
 */
public class HeartBeatService extends Service {
    private ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("beat").daemon(true).build();
    public ExecutorService threadPools = new ThreadPoolExecutor(5, 20, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
            threadFactory, new ThreadPoolExecutor.AbortPolicy());
    private Handler mHandler;
    private Runnable runnable;

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
        return super.onStartCommand(intent, flags, startId);
    }

    private void sendHeartBeat() {
        threadPools.execute(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                mHandler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("type", "beat");
                            String socketData = jsonObject.toString();
                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(SocketUtils.outputStream));
                            writer.write(socketData);
                            writer.flush();
                            writer.close();
                        } catch (IOException e) {
                            //todo 重连
                            EventBus.getDefault().post(new ClientEvent(Time.now(), "当前连接已断开...正在重连..."));
                            SocketUtils.release();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            EventBus.getDefault().post(new ClientEvent(Time.now(), "服务器正在初始化..."));
                        }
                        mHandler.postDelayed(this, 1000);
                    }
                };
                mHandler.post(runnable);
                Looper.loop();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
