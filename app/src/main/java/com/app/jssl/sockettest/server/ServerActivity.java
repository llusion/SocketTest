package com.app.jssl.sockettest.server;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.app.jssl.sockettest.client.ClientActivity;
import com.app.jssl.sockettest.eventbus.MessageEvent;
import com.app.jssl.sockettest.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

/**
 * Author: ls
 * Time:   2019/3/29 11:58
 * Desc:   This is ServerActivity：
 */
public class ServerActivity extends AppCompatActivity {
    private Button start, reply, stop;
    private TextView log, client;
    private MySocketServer mySocketServer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        start = findViewById(R.id.start);
        reply = findViewById(R.id.reply);
        stop = findViewById(R.id.stop);
        client = findViewById(R.id.client);
        log = findViewById(R.id.log);
        EventBus.getDefault().register(this);
        start.setOnClickListener(v -> startServer());
        stop.setOnClickListener(v -> {
            try {
                mySocketServer.stopServerAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        client.setOnClickListener(v -> startActivity(new Intent(ServerActivity.this, ClientActivity.class)));
        reply.setOnClickListener(v -> {

        });
    }

    private void startServer() {
        WebConfig webConfig = new WebConfig();
        webConfig.setPort(9001);
        webConfig.setMaxParallels(10);
        mySocketServer = new MySocketServer(webConfig);
        mySocketServer.startServerAsync();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void Event(MessageEvent messageEvent) {
        log.setText(messageEvent.getInfo().get(0).getTime() + "\n" + messageEvent.getInfo().get(0).getMessage());
    }
}
