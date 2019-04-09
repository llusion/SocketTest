package com.app.jssl.sockettest.eventbus;

/**
 * Author: ls
 * Time:   2019/4/1 15:48
 * Desc:   This is LoginEvent：
 */
public class LoginEvent {
    private String time;
    private boolean result;
    private String message;
    private String type;

    public LoginEvent(String time, boolean result, String message, String type) {
        this.time = time;
        this.result = result;
        this.message = message;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
