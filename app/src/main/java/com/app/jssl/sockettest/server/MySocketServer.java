package com.app.jssl.sockettest.server;

import com.app.jssl.sockettest.eventbus.LoginEvent;
import com.app.jssl.sockettest.eventbus.ServerEvent;
import com.app.jssl.sockettest.utils.Time;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
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
    private Socket clientSocket;

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
            socketAddress = new InetSocketAddress(InetAddress.getLocalHost(), 9001);
            socket = new ServerSocket();
            socket.bind(socketAddress);
            EventBus.getDefault().post(new LoginEvent(Time.now(), true, "端口开启：" + socket.getLocalPort(), "启动服务"));
            //堵塞
            acceptData();
        } catch (IOException e) {
            if (socket.isClosed()) {
                EventBus.getDefault().post(new LoginEvent(Time.now(), false, "服务端关闭", "启动服务"));
            }
            if (socket.isBound()) {
                EventBus.getDefault().post(new LoginEvent(Time.now(), false, "服务端口已经开启", "启动服务"));
            }
        }
    }

    /**
     * 等待客户端连接，并接收数据
     */
    private void acceptData() {
        threadPools.execute(() -> {
            while (isEnable) {
                try {
                    clientSocket = socket.accept();
                    clientSocket.setTcpNoDelay(false);
                    onAcceptRemote(clientSocket);
                } catch (IOException e) {
                    EventBus.getDefault().post(new ServerEvent(Time.now(), "服务器断开，不再接收数据"));
                }
            }
        });
    }

    private void onAcceptRemote(Socket remote) {
        try {
            // 从Socket当中得到InputStream对象
            InputStream inputStream = remote.getInputStream();
            byte buffer[] = new byte[1024];
            int temp = 0;
            String message = "";
            // 从InputStream当中读取客户端所发送的数据
            while ((temp = inputStream.read(buffer)) != -1) {
                message = new String(Arrays.copyOf(buffer, temp)).trim();
                EventBus.getDefault().post(new ServerEvent(Time.now(), "收到客户端的消息：" + message));
                dealAcceptRemote(remote, message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理客户端数据
     *
     * @param remote
     * @param message
     * @throws JSONException
     * @throws IOException
     */
    private void dealAcceptRemote(Socket remote, String message) throws JSONException, IOException {
        JSONObject jsonObject = new JSONObject(message);
        JSONObject response = new JSONObject();
        //todo 定协议 接收的数据格式和响应的数据格式
        if (jsonObject.get("type").equals("login")) {
            if (jsonObject.get("name").equals("admin") && jsonObject.get("password").equals("123")) {
                response.put("result", "true");
                response.put("message", "登录成功");
            } else {
                response.put("result", "false");
                response.put("message", "账号或密码不对");
            }
        }
        if (jsonObject.get("type").equals("beat")) {
            response.put("result", "true");
        }
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(remote.getOutputStream()));
        writer.write(response.toString());
        writer.flush();
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
            if (clientSocket != null) {
                clientSocket.close();
                clientSocket = null;
            } else {
                EventBus.getDefault().post(new ServerEvent(Time.now(), "连接已关闭，不再接收数据"));
            }
            socket.close();
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().post(new ServerEvent(Time.now(), "服务端关闭"));
    }
}