package com.app.jssl.sockettest.eventbus;

/**
 * Author: ls
 * Time:   2019/3/29 15:43
 * Desc:   This is ClientEvent：
 */
public class ClientEvent {
    private String time;
    private String message;

    public ClientEvent(String time, String message) {
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
