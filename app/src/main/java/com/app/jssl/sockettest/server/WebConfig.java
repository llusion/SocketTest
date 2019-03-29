package com.app.jssl.sockettest.server;

/**
 * Author: ls
 * Time:   2019/3/29 11:51
 * Desc:   This is WebConfig：
 */
public class WebConfig {

    private int port;//端口
    private int maxParallels;//最大监听数

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMaxParallels() {
        return maxParallels;
    }

    public void setMaxParallels(int maxParallels) {
        this.maxParallels = maxParallels;
    }
}
