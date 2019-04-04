package com.app.jssl.sockettest.server;

import com.app.jssl.sockettest.eventbus.ClientEvent;
import com.app.jssl.sockettest.eventbus.ServerEvent;
import com.app.jssl.sockettest.utils.Constant;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Author: ls
 * Time:   2019/3/29 11:50
 * Desc:   This is MySocketServer：
 */
public class MySocketServer {
    private volatile static MySocketServer server;
    private ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("server").daemon(true).build();
    public ExecutorService threadPools = new ThreadPoolExecutor(5, 20, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
            threadFactory, new ThreadPoolExecutor.AbortPolicy());
    private boolean isEnable;
    private ServerSocket socket;
    private InetSocketAddress socketAddress;

    public static MySocketServer getInstance() {
        if (server == null) {
            synchronized (MySocketServer.class) {
                if (server == null) {
                    server = new MySocketServer();
                }
            }
        }
        return server;
    }

    /**
     * 开启server
     */
    public void startServer() {
        isEnable = true;
        threadPools.execute(() -> startPort());
    }

    /**
     * 一一对应
     */
    private void startPort() {
        try {
            socketAddress = new InetSocketAddress(9001);
            socket = new ServerSocket();
            socket.bind(socketAddress);
            EventBus.getDefault().post(new ServerEvent(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                    , "端口开启：" + socket.getLocalPort()));
            acceptData();
        } catch (IOException e) {
            if (socket.isClosed()) {
                EventBus.getDefault().post(new ServerEvent(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                        , "服务端关闭"));
            }
            if (socket.isBound()) {
                EventBus.getDefault().post(new ServerEvent(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                        , "服务端口已经开启，请勿重复点击"));
            }
        }
    }

    private void acceptData() throws IOException {
        while (isEnable) {
            final Socket remote = socket.accept();
            threadPools.submit(() -> onAcceptRemote(remote));
        }
    }

    private void onAcceptRemote(Socket remote) {
        try {
            // 从Socket当中得到InputStream对象
            InputStream inputStream = remote.getInputStream();
            byte buffer[] = new byte[1024 * 4];
            int temp = 0;
            // 从InputStream当中读取客户端所发送的数据
            while ((temp = inputStream.read(buffer)) != -1) {
                String message = new String(Arrays.copyOf(buffer, temp)).trim();
                EventBus.getDefault().post(new ServerEvent(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                        "收到客户端的消息：" + message));
                JSONObject jsonObject = new JSONObject(message);
                //todo 定协议 接收的数据格式和响应的数据格式
                remote.getOutputStream().write(buffer, 0, temp);
                EventBus.getDefault().post(new ClientEvent(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                        "收到服务器的响应消息：" + message));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void reply() {
        try {
            Socket remote = socket.accept();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("server", "服务器主动发送消息");
            remote.getOutputStream().write(new Byte(jsonObject.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭server
     */
    public void stopServer() {
        if (!isEnable) {
            return;
        }
        try {
            isEnable = false;
            socket.close();
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().post(new ServerEvent(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                "服务端关闭"));
    }
}