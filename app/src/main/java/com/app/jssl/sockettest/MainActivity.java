package com.app.jssl.sockettest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.Arrays;

public class MainActivity extends BaseActivity {
    private TextView send, init, receive, reconnect, jump;
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
        jump = findViewById(R.id.jump);
        //socket初始化
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
                                } catch (Exception e) {
                                    //当socket心跳无法发送时，进入重连

                                }
                                mHandler.postDelayed(this, 60 * 1000 * 10);
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

        jump.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OtherActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void initSocket() {
        threadPools.execute(() -> SocketUtils.getSocket());
    }
}
