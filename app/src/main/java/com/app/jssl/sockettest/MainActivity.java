package com.app.jssl.sockettest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;

public class MainActivity extends BaseActivity {
    private TextView send, init, receive, reconnect, log;
    private Handler mHandler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init = findViewById(R.id.init);
        reconnect = findViewById(R.id.reconnect);
        send = findViewById(R.id.send);
        receive = findViewById(R.id.receive);
        log = findViewById(R.id.log);
        EventBus.getDefault().register(this);

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
                                    jsonObject.put("UserId", "7");
                                    jsonObject.put("MAC", "EC:D0:9F:D2:24:C1");
                                    String socketData = jsonObject.toString();
                                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(SocketUtils.outputStream));
                                    writer.write(socketData);
                                    writer.flush();
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
                            String message = new String(Arrays.copyOf(buffer, length), "gb2312").trim();
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void Event(MessageEvent messageEvent) {
        log.setText(messageEvent.getMessage());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
