package com.app.jssl.sockettest.eventbus;

/**
 * Author: ls
 * Time:   2019/4/3 11:15
 * Desc:   This is ServerEvent：
 */
public class ServerEvent {
    private String time;
    private String message;

    public ServerEvent(String time, String message) {
        this.time = time;
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
