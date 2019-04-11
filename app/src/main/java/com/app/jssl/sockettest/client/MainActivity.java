package com.app.jssl.sockettest.client;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.widget.TextView;

import com.app.jssl.sockettest.R;
import com.app.jssl.sockettest.base.BaseActivity;
import com.app.jssl.sockettest.eventbus.SocketEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.app.jssl.sockettest.utils.Constant.socketLog;

/**
 * Author: ls
 * Time:   2019/4/11 9:15
 * Desc:   This is MainActivity：
 */
public class MainActivity extends BaseActivity {
    private TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        info = findViewById(R.id.info);
        EventBus.getDefault().register(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(SocketEvent entity) {
        socketLog.append(entity.getTime() + "\n" + entity.getMessage() + "\n");
        info.setText(socketLog);
    }
}
