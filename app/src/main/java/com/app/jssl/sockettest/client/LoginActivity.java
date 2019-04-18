package com.app.jssl.sockettest.client;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.jssl.sockettest.R;
import com.app.jssl.sockettest.base.BaseActivity;
import com.app.jssl.sockettest.eventbus.LoginEvent;
import com.app.jssl.sockettest.service.HeartBeatService;
import com.app.jssl.sockettest.service.ReconnectUtils;
import com.app.jssl.sockettest.service.SocketService;
import com.app.jssl.sockettest.utils.NetConnectUtils;
import com.app.jssl.sockettest.utils.SocketUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

/**
 * Author: ls
 * Time:   2019/4/1 10:10
 * Desc:   This is LoginActivity：
 * <p>
 * 登录页面
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private TextView mBtnLogin;
    private View progress, mInputLayout;
    private float mWidth;
    private LinearLayout mName, mPsw;
    private AnimatorSet set;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        startService();
        initView();
    }

    /**
     * 开启服务连接socket
     */
    private void startService() {
        Intent intent = new Intent(LoginActivity.this, SocketService.class);
        startService(intent);
        Intent mIntent = new Intent(LoginActivity.this, HeartBeatService.class);
        startService(mIntent);
    }

    private void initView() {
        mBtnLogin = findViewById(R.id.main_btn_login);
        progress = findViewById(R.id.layout_progress);
        mInputLayout = findViewById(R.id.input_layout);
        mName = findViewById(R.id.input_layout_name);
        mPsw = findViewById(R.id.input_layout_psw);
        mBtnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mBtnLogin.setClickable(false);
        mWidth = mBtnLogin.getMeasuredWidth();
        mName.setVisibility(View.INVISIBLE);
        mPsw.setVisibility(View.INVISIBLE);
        inputAnimator(mInputLayout, mWidth);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(LoginEvent entity) {
        //解析服务器返回数据
        switch (entity.getType()) {
            case "连接":
                if (!entity.isResult()) {
                    mBtnLogin.setClickable(false);
                    //2.服务器地址不对
                    if (NetConnectUtils.hasNet(this)) {
                        //3.需要重连
                        if ("socket连接失败".equals(entity.getMessage())) {
                            ReconnectUtils.getInstance(this, getSupportFragmentManager()).reconnect();
                        } else {
                            Toast.makeText(this, entity.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //1.无网络
                        Toast.makeText(this, "当前无网络", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    mBtnLogin.setClickable(true);
                }
                break;
            case "登录":
                if (entity.isResult()) {
                    progress.setVisibility(View.GONE);
                    toMainActivity();
                } else {
                    login();
                }
                break;
        }
    }

    private void toMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void login() {
        threadPools.execute(() -> {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "login");
                jsonObject.put("name", "admin");
                jsonObject.put("password", "123");
                String socketData = jsonObject.toString();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(SocketUtils.outputStream));
                writer.write(socketData);
                writer.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void inputAnimator(final View view, float w) {
        set = new AnimatorSet();
        //输入框添加margin值设置尽量和按钮对齐
        ValueAnimator animator = ValueAnimator.ofFloat(0, w);
        animator.addUpdateListener(animation -> {
            float value = (Float) animation.getAnimatedValue();
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view
                    .getLayoutParams();
            params.leftMargin = (int) value;
            params.rightMargin = (int) value;
            view.setLayoutParams(params);
        });
        //输入框宽度缩放一半动画
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout, "scaleX", 1f, 0.5f);
        set.setDuration(1000);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.playTogether(animator, animator2);
        set.start();
        set.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                progress.setVisibility(View.VISIBLE);
                progressAnimator(progress);
                mInputLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
    }

    /**
     * 进度框动画
     *
     * @param view
     */
    private void progressAnimator(final View view) {
        PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX", 0.5f, 1f);
        PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY", 0.5f, 1f);
        ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(view, animator, animator2);
        animator3.setDuration(1000);
        animator3.setInterpolator(new MyInterpolator());
        animator3.start();
        animator3.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                login();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public class MyInterpolator extends LinearInterpolator {
        private float factor;

        public MyInterpolator() {
            this.factor = 0.15f;
        }

        @Override
        public float getInterpolation(float input) {
            return (float) (Math.pow(2, -10 * input)
                    * Math.sin((input - factor / 4) * (2 * Math.PI) / factor) + 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
