package com.app.jssl.sockettest.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.app.jssl.sockettest.base.BaseActivity;
import com.app.jssl.sockettest.eventbus.SocketEvent;
import com.app.jssl.sockettest.utils.Constant;
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
        sendHeartBeat();
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
                        if (!Constant.valid) {
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("type", "beat");
                            String socketData = jsonObject.toString();
                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(SocketUtils.outputStream));
                            writer.write(socketData);
                            writer.flush();
                        } catch (IOException e) {
                            EventBus.getDefault().post(new SocketEvent(Time.now(), "reconnect", "reconnect"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            EventBus.getDefault().post(new SocketEvent(Time.now(), "服务器异常", "remoteException"));
                        }
                        mHandler.postDelayed(this, 10 * 1000);
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
        if (mHandler != null) {
            mHandler.removeCallbacks(runnable);
        }
    }
}
