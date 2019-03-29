package com.app.jssl.sockettest;

/**
 * Author: ls
 * Time:   2019/3/29 10:39
 * Desc:   This is MessageEvent：
 */
public class MessageEvent {
    private String message;

    public MessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
