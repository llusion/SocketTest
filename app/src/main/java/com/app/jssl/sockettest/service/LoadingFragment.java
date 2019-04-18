package com.app.jssl.sockettest.service;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.jssl.sockettest.R;

public class LoadingFragment extends DialogFragment {

    public final static String LOADING = "loading";
    public final static String MISSING = "miss";
    public final static String RECONNECT = "reconnect";
    public final static String KEY_FLAG = "flag";
    protected String flag;
    private TextView tv_loading;

    public LoadingFragment() {

    }

    public static LoadingFragment newInstance(Bundle bundle) {
        LoadingFragment fragment = new LoadingFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flag = getArguments().getString(KEY_FLAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_loading_progressbar, container, false);
        ImageView iv_loading = view.findViewById(R.id.iv_loading);
        tv_loading = view.findViewById(R.id.tv_loading);
        selectDialog(flag);
        ValueAnimator animatorFade = ObjectAnimator.ofFloat(iv_loading, "rotation", 0f, 36000f);
        animatorFade.setInterpolator(new LinearInterpolator());
        animatorFade.setDuration(100000);
        animatorFade.setRepeatCount(ValueAnimator.INFINITE);
        animatorFade.start();
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        return dialog;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void selectDialog(String flag) {
        switch (flag) {
            case LOADING:
                break;
            case MISSING:
                tv_loading.setText("无法连接网络，正在连接...");
                break;
            case RECONNECT:
                tv_loading.setText("已掉线，正在重连...");
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void dismissAllowingStateLoss() {
        if (getDialog() != null && getDialog().isShowing()) {
            super.dismissAllowingStateLoss();
        }
    }
}
