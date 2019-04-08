package com.app.jssl.sockettest.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.app.jssl.sockettest.eventbus.ClientEvent;
import com.app.jssl.sockettest.eventbus.LoginEvent;
import com.app.jssl.sockettest.utils.SocketUtils;
import com.app.jssl.sockettest.utils.Time;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
        initService();
        return super.onStartCommand(intent, flags, startId);
    }

    private void initService() {
        startServer();
        sendHeartBeat();
        receive();
    }

    /**
     * 启动接收线程
     */
    private void receive() {
        threadPools.execute(() -> {
            while (true) {
                try {
//                    SocketUtils.getSocket().sendUrgentData(0);
                    byte[] buffer = new byte[1024 * 2];
                    int length = 0;
                    while (((length = SocketUtils.inputStream.read(buffer)) != -1)) {
                        if (length > 0) {
                            String message = new String(Arrays.copyOf(buffer, length)).trim();
                            EventBus.getDefault().post(new LoginEvent(Time.now(), message));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
                            jsonObject.put("type", "connect");
                            String socketData = jsonObject.toString();
                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(SocketUtils.outputStream));
                            writer.write(socketData);
                            writer.flush();
                        } catch (IOException e) {
                            //todo 重连
                            EventBus.getDefault().post(new ClientEvent(Time.now()
                                    , "当前连接已断开...正在重连..."));
                            SocketUtils.release();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            EventBus.getDefault().post(new ClientEvent(Time.now()
                                    , "服务器关闭，请开启"));
                            SocketUtils.release();
                            mHandler.removeCallbacks(runnable);
                        }
                        mHandler.postDelayed(this, 1000);
                    }
                };
                mHandler.post(runnable);
                Looper.loop();
            }
        });
    }

    private void startServer() {
        threadPools.execute(() -> SocketUtils.getSocket());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnable);
        SocketUtils.release();
    }
}
