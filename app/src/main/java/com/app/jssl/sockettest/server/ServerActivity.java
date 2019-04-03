package com.app.jssl.sockettest.server;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.app.jssl.sockettest.R;
import com.app.jssl.sockettest.client.ClientActivity;
import com.app.jssl.sockettest.eventbus.InfoEntity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Author: ls
 * Time:   2019/3/29 11:58
 * Desc:   This is ServerActivity：
 */
public class ServerActivity extends AppCompatActivity implements View.OnClickListener {
    private Button start, reply, stop, disConn;
    private TextView log, client;
    private MySocketServer mySocketServer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        start = findViewById(R.id.start);
        reply = findViewById(R.id.reply);
        disConn = findViewById(R.id.disconnect);
        stop = findViewById(R.id.stop);
        client = findViewById(R.id.client);
        log = findViewById(R.id.log);
        EventBus.getDefault().register(this);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        client.setOnClickListener(this);
        reply.setOnClickListener(this);
        disConn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mySocketServer == null) {
            Toast.makeText(this, "请先开启服务", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (v.getId()) {
            case R.id.start:
                startServer();
                break;
            case R.id.stop:
                mySocketServer.stopServer();
                break;
            case R.id.client:
                startActivity(new Intent(ServerActivity.this, ClientActivity.class));
                break;
            case R.id.reply:
                mySocketServer.reply();
                break;
            case R.id.disconnect:
                mySocketServer.disconn();
                break;
        }
    }

    private void startServer() {
        WebConfig webConfig = new WebConfig();
        webConfig.setPort(9001);
        webConfig.setMaxParallels(10);
        mySocketServer = new MySocketServer(webConfig);
        mySocketServer.startServer();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void Event(InfoEntity infoEntity) {
        log.setText(infoEntity.getTime() + "\n" + infoEntity.getMessage());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
