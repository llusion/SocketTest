package com.app.jssl.sockettest.service;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.app.jssl.sockettest.base.BaseActivity;
import com.app.jssl.sockettest.eventbus.SocketEvent;
import com.app.jssl.sockettest.utils.SocketUtils;
import com.app.jssl.sockettest.utils.Time;

import org.greenrobot.eventbus.EventBus;

/**
 * Author: ls
 * Time:   2019/4/18 9:32
 * Desc:   This is ReconnectUtils：
 */
public class ReconnectUtils {

    public Context context;
    public LoadingFragment loading;
    private FragmentManager mFragmentManager;
    private boolean enable = true;
    private long beginTime;
    public volatile static ReconnectUtils mInstance;

    public ReconnectUtils(Context mContext, FragmentManager fm) {
        context = mContext;
        mFragmentManager = fm;
    }

    public static ReconnectUtils getInstance(Context mContext, FragmentManager fm) {
        if (mInstance == null) {
            synchronized (ReconnectUtils.class) {
                if (mInstance == null) {
                    mInstance = new ReconnectUtils(mContext, fm);
                }
                return mInstance;
            }
        }
        return mInstance;
    }

    public void reconnect() {
        //1.显示加载框
        Bundle bundle = new Bundle();
        bundle.putString("flag", "正在重连..");
        if (loading != null) return;
        loading = LoadingFragment.newInstance(bundle);
        commitAllowingStateLoss(loading, "loading");
        beginTime = System.currentTimeMillis();
        //2.释放资源
        SocketUtils.release();
        //3.连接策略
        ((BaseActivity) context).threadPools.execute(() -> {
            while (enable) {
                if (SocketUtils.isConnect()) {
                    enable = false;
                    dismissLoadingFragment();
                } else {
                    enable = true;
                    //重连超过2分钟，就无法连接
                    if (System.currentTimeMillis() - beginTime > 10 * 1000) {
                        enable = false;
                        //停止重连
                        dismissLoadingFragment();
                        EventBus.getDefault().post(new SocketEvent(Time.now(), "reconnect", "netError"));
                    } else {
                        SocketUtils.getSocket();
                        dismissLoadingFragment();
                    }
                }
            }
        });
    }

    /**
     * 避免dialogFragment.show出现的Can not perform this action after onSaveInstanceState错误
     *
     * @param flag
     */
    private void commitAllowingStateLoss(DialogFragment dialogFragment, String flag) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.add(dialogFragment, flag);
        ft.commitAllowingStateLoss();
    }

    /**
     * 取消加载对话框
     */
    public void dismissLoadingFragment() {
        if (loading != null) {
            loading.dismissAllowingStateLoss();
            loading = null;
        }
    }
}
