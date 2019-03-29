package com.app.jssl.sockettest.eventbus;

import java.util.List;

/**
 * Author: ls
 * Time:   2019/3/29 10:39
 * Desc:   This is MessageEvent：
 */
public class MessageEvent {
    private List<InfoEntity> info;

    public MessageEvent(List<InfoEntity> info) {
        this.info = info;
    }

    public List<InfoEntity> getInfo() {
        return info;
    }

    public void setInfo(List<InfoEntity> info) {
        this.info = info;
    }
}
