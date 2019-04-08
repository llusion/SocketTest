package com.app.jssl.sockettest.server;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.app.jssl.sockettest.R;
import com.app.jssl.sockettest.client.ClientActivity;
import com.app.jssl.sockettest.eventbus.ServerEvent;
import com.app.jssl.sockettest.service.ServerService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.app.jssl.sockettest.utils.Constant.serverLog;

/**
 * Author: ls
 * Time:   2019/3/29 11:58
 * Desc:   This is ServerActivity：
 */
public class ServerActivity extends AppCompatActivity implements View.OnClickListener {
    private Button start, stop;
    private TextView log, client;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);
        client = findViewById(R.id.client);
        log = findViewById(R.id.log);
        log.setText(serverLog);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        client.setOnClickListener(this);
        if (EventBus.getDefault().isRegistered(ServerActivity.this)) return;
        EventBus.getDefault().register(ServerActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                startServer();
                start.setClickable(false);
                break;
            case R.id.stop:
                MySocketServer.getInstance().stopServer();
                break;
            case R.id.client:
                startActivity(new Intent(ServerActivity.this, ClientActivity.class));
//                finish();
                break;
        }
    }

    private void startServer() {
        Intent intent = new Intent(ServerActivity.this, ServerService.class);
        startService(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(ServerEvent serverEvent) {
        serverLog.append(serverEvent.getTime() + "\n" + serverEvent.getMessage() + "\n");
        log.setText(serverLog);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(ServerActivity.this)) {
            EventBus.getDefault().unregister(ServerActivity.this);
        }
    }
}
