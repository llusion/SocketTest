package com.app.jssl.sockettest.server;

import com.app.jssl.sockettest.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author: ls
 * Time:   2019/3/29 11:50
 * Desc:   This is MySocketServer：
 */
public class MySocketServer {
    private boolean isEnable;
    private final WebConfig webConfig;//配置信息类
    private final ExecutorService threadPool;//线程池
    private ServerSocket socket;

    public MySocketServer(WebConfig webConfig) {
        this.webConfig = webConfig;
        threadPool = Executors.newCachedThreadPool();
    }

    /**
     * 开启server
     */
    public void startServerAsync() {
        isEnable = true;
        new Thread(() -> doProcSync()).start();
    }

    /**
     * 关闭server
     */
    public void stopServerAsync() throws IOException {
        if (!isEnable) {
            return;
        }
        isEnable = true;
        socket.close();
        socket = null;
    }

    private void doProcSync() {
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(webConfig.getPort());
            socket = new ServerSocket();
            socket.bind(socketAddress);
            EventBus.getDefault().postSticky(new MessageEvent("socket server is bind" + socket.toString()));
            while (isEnable) {
                final Socket remotePeer = socket.accept();
                threadPool.submit(() -> onAcceptRemotePeer(remotePeer));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onAcceptRemotePeer(Socket remotePeer) {
        try {
            remotePeer.getOutputStream().write("connected successful".getBytes());
            // 从Socket当中得到InputStream对象
            InputStream inputStream = remotePeer.getInputStream();
            byte buffer[] = new byte[1024 * 4];
            int temp = 0;
            // 从InputStream当中读取客户端所发送的数据
            while ((temp = inputStream.read(buffer)) != -1) {
                remotePeer.getOutputStream().write(buffer, 0, temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}