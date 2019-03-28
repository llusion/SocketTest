package com.app.jssl.sockettest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

/**
 * 监听网络状态变化
 * Created by Travis on 2017/10/11.
 */

public class NetWorkChangReceiver extends BroadcastReceiver {
    private NetChangeListener netChangeListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            netChangeListener.onNetChange(NetConnectUtils.hasNet(context));
        }
    }

    /**
     * 网络状态类型改变的监听接口
     */
    public interface NetChangeListener {
        void onNetChange(boolean netStatus);
    }

    /**
     * 设置网络状态监听接口
     */
    public void setStatusMonitor(NetChangeListener netChangeListener) {
        this.netChangeListener = netChangeListener;
    }
}
