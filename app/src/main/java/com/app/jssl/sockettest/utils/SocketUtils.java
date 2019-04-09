package com.app.jssl.sockettest.utils;

import android.content.Context;

import com.app.jssl.sockettest.eventbus.LoginEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Author: ls
 * Time:   2019/2/25 14:53
 * Desc:   This is SocketUtils：
 */
public class SocketUtils {

    public static Socket socket = null;
    public static Context activity;
    public static OutputStream outputStream;
    public static InputStream inputStream;

    private SocketUtils(Context activity) {
        SocketUtils.activity = activity;
    }

    /**
     * 单例获取socket对象
     *
     * @return socket
     */
    public static Socket getSocket() {
        if (socket == null) {
            synchronized (Socket.class) {
                if (socket == null) {
                    try {
                        socket = new Socket("127.0.0.1", 9001);
                        socket.setKeepAlive(true);
                        socket.setTcpNoDelay(false);
                        outputStream = socket.getOutputStream();
                        inputStream = socket.getInputStream();
                        EventBus.getDefault().post(new LoginEvent(Time.now(), true, "socket连接成功", "连接"));
                    } catch (UnknownHostException e) {
                        EventBus.getDefault().post(new LoginEvent(Time.now(), false, "socket连接失败", "连接"));
                    } catch (IOException e) {
                        EventBus.getDefault().post(new LoginEvent(Time.now(), false, "socket连接失败", "连接"));
                    } catch (NullPointerException e) {
                        EventBus.getDefault().post(new LoginEvent(Time.now(), false, "socket连接失败", "连接"));
                    }
                }
            }
        }
        return socket;
    }

    /**
     * 释放socket资源
     */
    public static void release() {
        try {
            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
            }
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
