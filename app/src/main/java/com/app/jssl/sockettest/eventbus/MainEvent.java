package com.app.jssl.sockettest.eventbus;

/**
 * Author: ls
 * Time:   2019/4/18 16:12
 * Desc:   This is MainEvent：
 */
public class MainEvent {
    private String time;
    private String message;

    public MainEvent(String time, String message) {
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
