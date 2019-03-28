package com.app.jssl.sockettest;

import android.content.Intent;
import android.os.Bundle;

/**
 * Author: ls
 * Time:   2019/3/21 13:53
 * Desc:   This is OtherActivity：
 */
public class OtherActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
        findViewById(R.id.btn_back).setOnClickListener(v -> {
            Intent intent = new Intent(OtherActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        threadPools.execute(() -> SocketUtils.getSocket());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        threadPools.shutdown();
    }
}
