package com.app.jssl.sockettest.eventbus;

/**
 * Author: ls
 * Time:   2019/4/1 15:48
 * Desc:   This is LoginEntity：
 */
public class LoginEntity {
    private boolean result;
    private String message;

    public LoginEntity(boolean result, String message) {
        this.result = result;
        this.message = message;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
