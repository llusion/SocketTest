package com.app.jssl.sockettest.base;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.app.jssl.sockettest.utils.SocketUtils;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Author: ls
 * Time:   2019/3/21 11:43
 * Desc:   This is BaseActivity：
 */
public class BaseActivity extends AppCompatActivity implements NetWorkChangReceiver.NetChangeListener {
    private NetWorkChangReceiver netWorkChangReceiver;
    private ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("socket").daemon(true).build();
    public ExecutorService threadPools = new ThreadPoolExecutor(5, 20, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
            threadFactory, new ThreadPoolExecutor.AbortPolicy());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        netWorkChangReceiver = new NetWorkChangReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkChangReceiver, filter);
        netWorkChangReceiver.setStatusMonitor(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(netWorkChangReceiver);
    }

    @Override
    public void onNetChange(boolean netStatus) {
        if (!netStatus) {
            Toast.makeText(this, "断网了", Toast.LENGTH_SHORT).show();
            SocketUtils.release();
        }
    }
}
