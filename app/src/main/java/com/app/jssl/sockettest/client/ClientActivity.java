package com.app.jssl.sockettest.client;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import com.app.jssl.sockettest.R;
import com.app.jssl.sockettest.base.BaseActivity;
import com.app.jssl.sockettest.eventbus.ClientEvent;
import com.app.jssl.sockettest.server.ServerActivity;
import com.app.jssl.sockettest.utils.Constant;
import com.app.jssl.sockettest.utils.SocketUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static com.app.jssl.sockettest.utils.Constant.clientLog;

public class ClientActivity extends BaseActivity {
    private TextView send, init, receive, reconnect, log, server;
    private Handler mHandler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        init = findViewById(R.id.init);
        reconnect = findViewById(R.id.reconnect);
        send = findViewById(R.id.send);
        server = findViewById(R.id.server);
        receive = findViewById(R.id.receive);
        log = findViewById(R.id.log);
        log.setText(clientLog);
        EventBus.getDefault().register(this);

        //打开服务器端
        server.setOnClickListener(v -> startActivity(new Intent(ClientActivity.this, ServerActivity.class)));
        //socket连接
        init.setOnClickListener(v -> initSocket());

        //socket重连
        reconnect.setOnClickListener(v -> {

        });

        //心跳
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                threadPools.execute(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        mHandler = new Handler();
                        runnable = new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("UserId", "1");
                                    String socketData = jsonObject.toString();
                                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(SocketUtils.outputStream));
                                    writer.write(socketData);
                                    writer.flush();
                                    EventBus.getDefault().post(new ClientEvent(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                                            , "心跳发送成功"));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                mHandler.postDelayed(this, 60 * 1000);
                            }
                        };
                        mHandler.post(runnable);
                        Looper.loop();
                    }
                });
            }
        });

        //接收
        receive.setOnClickListener(v -> threadPools.execute(() -> {
            try {
                while (true) {
                    SocketUtils.getSocket().sendUrgentData(0);
                    byte[] buffer = new byte[1024 * 2];
                    int length = 0;
                    while (((length = SocketUtils.inputStream.read(buffer)) != -1)) {
                        if (length > 0) {
                            String message = new String(Arrays.copyOf(buffer, length)).trim();
                            EventBus.getDefault().post(new ClientEvent(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                                    , "---接收线程----" + "\n" + "收到服务器消息！" + message));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    private void initSocket() {
        threadPools.execute(() -> SocketUtils.getSocket());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(ClientEvent info) {
        clientLog.append(info.getTime() + "\n" + info.getMessage() + "\n");
        log.setText(clientLog.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
