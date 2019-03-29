package com.app.jssl.sockettest.eventbus;

/**
 * Author: ls
 * Time:   2019/3/29 15:43
 * Desc:   This is InfoEntity：
 */
public class InfoEntity {
    private String time;
    private String message;

    public InfoEntity(String time, String message) {
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
