package com.app.jssl.sockettest;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Author: ls
 * Time:   2019/2/25 14:53
 * Desc:   This is SocketUtils：
 */
public class SocketUtils {

    public static volatile Socket socket = null;
    public static Context activity;
    public static OutputStream outputStream;
    public static InputStream inputStream;

    private SocketUtils(Context activity) {
        SocketUtils.activity = activity;
    }

    /**
     * 单例获取socket对象
     *
     * @return
     */
    public static Socket getSocket() {
        if (socket == null) {
            synchronized (Socket.class) {
                if (socket == null) {
                    try {
                        socket = new Socket("192.168.0.15", 19021);
                        socket.setKeepAlive(true);
                        socket.setTcpNoDelay(true);
                        outputStream = socket.getOutputStream();
                        inputStream = socket.getInputStream();
                    } catch (Exception e) {
                        e.printStackTrace();
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
